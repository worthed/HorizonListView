package com.worthed.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

import java.util.LinkedList;

/**
 * 水平ListView
 */
public class HorizonListView extends AdapterView<Adapter> {

    // 新添加的所有子视图在当前最当前最后一个子视图后添加的布局模型
    private static final int LAYOUT_MODE_RIGHT = 0;
    // 与LAYOUT_MODE_BELOW相反方向添加的布局模型
    private static final int LAYOUT_MODE_LEFT = 1;

    private static final int INVALID_INDEX = -1;

    // 视图和数据适配
    private Adapter mAdapter;
    // 当前显示最后一个Item在Adapter中位置
    private int mLastItemPosition = -1;
    // 当前显示第一个Item在Adapter中位置
    private int mFirstItemPosition;

    // 当前顶部第一个item
    private int mListLeft;
    private int mListLeftOffset;
    private int mListLeftStart;

    private int mTouchStartX;
    private int mTouchStartY;

    // View复用当前仅支持一种类型Item视图复用
    // 想更多了解ListView视图如何复用可以看AbsListView内部类RecycleBin
    private final LinkedList<View> mCachedItemViews = new LinkedList<View>();
    private Runnable mLongPressRunnable;
    private Rect mRect;

    private Scroller scroller;

    private GestureDetector gestureDetector;

    private int maxX = Integer.MAX_VALUE;

    public HorizonListView(Context context) {
        super(context);
        initListView(context);
    }

    public HorizonListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initListView(context);
    }

    public HorizonListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initListView(context);
    }

    private void initListView(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        scroller = new Scroller(context);
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public View getSelectedView() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setSelection(int position) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 异常处理
        if (mAdapter == null) {
            return;
        }

        if (scroller.computeScrollOffset()) {
            int scrollx = scroller.getCurrX();
            mListLeft = scrollx;
        }

        /*if (mListLeft <= 0) {
            mListLeft = 0;
            scroller.forceFinished(true);
        }*/
        /*if (mListLeft >= maxX) {
            mListLeft = maxX;
            scroller.forceFinished(true);
        }*/

        // 当前ListView没有任何子视图(Item)，所以依次在从上向下填充子视图
        if (getChildCount() == 0) {
            mLastItemPosition = -1;
            // add and measure
            fillListDown(mListLeft, 0);
        } else {
            final int offset = mListLeft + mListLeftOffset - getChildAt(0).getLeft();
            // final int offset = mNextX - getChildAt(0).getLeft();
            // 移除可视区域的都干掉
            removeNonVisibleViews(offset);
            fillList(offset);
        }


        // layout，添加测量完后，获取视图摆放位置
        positionItems();

        // draw， 上面子视图都添加完了，重绘布局把子视图绘制出来吧
        invalidate();

        if (!scroller.isFinished()) {
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });

        }
    }

    /**
     * 向当前最后一个子视图下面添加，填充到当前ListView底部无再可填充区域为止
     *
     * @param bottomEdge 当前最后一个子视图底部边界值
     * @param offset     显示区域偏移量
     */
    private void fillListDown(int bottomEdge, int offset) {
        while (bottomEdge + offset < getWidth() && mLastItemPosition < mAdapter.getCount() - 1) {
            // 现在添加的视图时当前子视图后面，所以位置+1
            mLastItemPosition++;
            // 数据和视图通过Adapter适配，此处从Adapter获取视图。
            // 第二个参数传入复用的View对象，先出入null，之后再添加View对象复用机制
            View newBottomChild = mAdapter.getView(mLastItemPosition, getCachedView(), this);
            // **具体添加视图处理
            addAndMeasureChild(newBottomChild, LAYOUT_MODE_RIGHT);
            // 添加一个子视图(Item)，随之底部边界也发生改变
            bottomEdge += newBottomChild.getMeasuredWidth();
        }

    }

    /**
     * 向当前ListView添加子视图并负责Measure子视图操作
     *
     * @param child      需要添加的ListView子视图(Item)
     * @param layoutMode 在顶部添加上面添加还是在底部下面添加子视图 ， LAYOUT_MODE_LEFT 或 LAYOUT_MODE_RIGHT
     */
    private void addAndMeasureChild(View child, int layoutMode) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        final int index = layoutMode == LAYOUT_MODE_LEFT ? 0 : -1;
        addViewInLayout(child, index, params, true);

        final int itemHeight = getHeight();
        // 位运算 | itemWidth表示添加此当前值
        child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY | itemHeight);
    }


    /**
     * 对所有子视图进行layout操作，取得所有子视图正确的位置
     */
    private void positionItems() {
        int left = mListLeft + mListLeftOffset;

        Log.d("HorizonListView", "left : " + left);

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);

            // 当前视图未虽然添加到ViewGroup但是还未重新进行measure, layout, draw操作
            // 直接通过child.getWidth();获取不到宽度
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            final int top = (getHeight() - height) / 2;

            child.layout(left, top, left + width, top + height);
            left += width;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() == 0) {
            return false;
        }
        return gestureDetector.onTouchEvent(event);
    }

//    /**
//     * 控制ListView进行滚动
//     *
//     * @param x 当前触摸点X轴的值
//     */
//    private void scrollList(int x) { // scrollIfNeeded
//        // 当前手指坐在位置与刚触摸到屏幕之间的距离
//        // 也就是当前手指在屏幕上Y轴总移动位置
//        // int scrolledDistance = x - mTouchStartX;
//        // 改变当前记录的ListView顶部位置
//        // mListLeft = mListLeftStart + scrolledDistance;
//
//        // 关键，要想使相面的计算生效必须重新请求布局
//        // 会触发当前onLayout方法，指定Item位置与绘制先关还是在onLayout中
//        requestLayout();
//    }

    /**
     * ListView向上或者向下移动后需要向顶部或者底部添加视图
     *
     * @param offset
     */
    private void fillList(final int offset) {
        // 最后一个item的下边界值就是当前ListView的下边界值
        final int rightEdge = getChildAt(getChildCount() - 1).getRight();
        fillListDown(rightEdge, offset);

        // 第一个Item的上边界值就是ListVie的上边界值
        final int leftEdge = getChildAt(0).getLeft();
        fillListUp(leftEdge, offset);
    }


    /**
     * 与fillListDown相反方向添加
     *
     * @param leftEdge 当前第一个子视图顶部边界值
     * @param offset   显示区域偏移量
     */
    private void fillListUp(int leftEdge, int offset) {
        while (leftEdge + offset > 0 && mFirstItemPosition > 0) {
            // 现在添加的视图时当前子视图后面，所以位置+1
            mFirstItemPosition--;

            View newTopChild = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
            addAndMeasureChild(newTopChild, LAYOUT_MODE_LEFT);
            int childWidth = newTopChild.getMeasuredWidth();
            leftEdge -= childWidth;

            // 在顶部添加视图后，更新顶部偏移
            mListLeftOffset -= childWidth;
        }
    }

    private void startTouch(MotionEvent event) {
        mTouchStartX = (int) event.getX();
        mTouchStartY = (int) event.getY();
        mListLeftStart = getChildAt(0).getLeft() - mListLeftOffset;
    }

    /**
     * 调用ItemLongClickListener提供点击位置等信息
     *
     * @param index Item索引值
     */
    private void longClickChild(final int index) {
        final View itemView = getChildAt(index);
        final int position = mFirstItemPosition + index;
        final long id = mAdapter.getItemId(position);
        // 从父类获取绑定的OnItemLongClickListener
        OnItemLongClickListener listener = getOnItemLongClickListener();

        if (listener != null) {
            listener.onItemLongClick(this, itemView, position, id);
        }
    }

    /**
     * 调用ItemClickListener提供当前点击位置
     *
     * @param x 触摸点X轴值
     * @param y 触摸点Y轴值
     */
    private void clickChildAt(int x, int y) {
        // 触摸点在当前显示所有Item中哪一个
        final int itemIndex = getContainingChildIndex(x, y);

        if (itemIndex != INVALID_INDEX) {
            final View itemView = getChildAt(itemIndex);
            // 当前Item在ListView所有Item中的位置
            final int position = mFirstItemPosition + itemIndex;
            final long id = mAdapter.getItemId(position);

            // 调用父类方法，会触发ListView ItemClickListener
            performItemClick(itemView, position, id);
        }
    }

    /**
     * 删除当前已经移除可视范围的Item View
     *
     * @param offset 可视区域偏移量
     */
    private void removeNonVisibleViews(final int offset) {
        int childCount = getChildCount();

        /**  ListView向上滚动，删除顶部移除可视区域的所有视图  **/

        // 不在ListView底部，子视图大于1
        if (mLastItemPosition != mAdapter.getCount() - 1 && childCount > 1) {
            View firstChild = getChildAt(0);
            // 通过第二条件判断当前最上面的视图是否被移除可是区域
            while (firstChild != null && firstChild.getRight() + offset < 0) {
                // 既然顶部第一个视图已经移除可视区域从当前ViewGroup中删除掉
                removeViewInLayout(firstChild);
                // 用于下次判断，是否当前顶部还有需要移除的视图
                childCount--;
                // View对象回收，目的是为了复用
                mCachedItemViews.addLast(firstChild);
                // 既然最上面的视图被干掉了，当前ListView第一个显示视图也需要+1
                mFirstItemPosition++;
                // 同上更新
                mListLeftOffset += firstChild.getMeasuredWidth();

                // 为下一次while遍历获取参数
                if (childCount > 1) {
                    // 当前已经删除第一个，再接着去除删除后剩余的第一个
                    firstChild = getChildAt(0);
                } else {
                    // 没啦
                    firstChild = null;
                }
            }
        }


        /**  ListView向下滚动，删除底部移除可视区域的所有视图  **/
        // 与上面操作一样，只是方向相反一个顶部操作一个底部操作
        if (mFirstItemPosition != 0 && childCount > 1) {
            View lastChild = getChildAt(childCount - 1);
            while (lastChild != null && lastChild.getLeft() + offset > getWidth()) {
                removeViewInLayout(lastChild);
                childCount--;
                mCachedItemViews.addLast(lastChild);
                mLastItemPosition--;

                if (childCount > 1) {
                    lastChild = getChildAt(childCount - 1);
                } else {
                    lastChild = null;
                }
            }
        }

        // 这里有问题
        if (childCount > 1) {
            maxX = mAdapter.getCount() * getChildAt(0).getMeasuredWidth() - getWidth();
            Log.d("HorizonListView", "maxX : " + maxX);
        }

    }


    /**
     * 获取一个可以复用的Item View
     *
     * @return view 可以复用的视图或者null
     */
    private View getCachedView() {

        if (mCachedItemViews.size() != 0) {
            return mCachedItemViews.removeFirst();
        }

        return null;
    }

    private int getContainingChildIndex(int x, int y) {
        if (mRect == null) {
            mRect = new Rect();
        }

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).getHitRect(mRect);
            if (mRect.contains(x, y)) {
                return i;
            }
        }

        return INVALID_POSITION;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public GestureListener() {
            super();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("HorizonListView", "onDown()");
            startTouch(e);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mListLeft -= distanceX;
            Log.d("HorizonListView", "onScroll() left : " + mListLeft);
            requestLayout();
            return super.onScroll(e1, e2, distanceX, distanceY);

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("HorizonListView", "onFling()");
            scroller.fling(mListLeft, 0, (int) velocityX, 0, -maxX, 0, 0, 0);
            requestLayout();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("HorizonListView", "onSingleTapConfirmed()");
            clickChildAt((int) e.getX(), (int) e.getY());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("HorizonListView", "onLongPress()");
            final int index = getContainingChildIndex(
                    mTouchStartX, mTouchStartY);
            if (index != INVALID_INDEX) {
                longClickChild(index);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("HorizonListView", "onSingleTapUp()");
            return super.onSingleTapUp(e);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
    }

}