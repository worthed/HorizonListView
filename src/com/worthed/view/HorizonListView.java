package com.worthed.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

public class HorizonListView extends AdapterView<Adapter> {

    // 新添加的所有子视图在当前最当前最后一个子视图后添加的布局模型
    private static final int LAYOUT_MODE_BELOW = 0;
    // 与LAYOUT_MODE_BELOW相反方向添加的布局模型
    private static final int LAYOUT_MODE_ABOVE = 1;

    // 视图和数据适配
    private Adapter mAdapter;
    // 当前显示最后一个Item在Adapter中位置
    private int mLastItemPosition = -1;
    // 当前显示第一个Item在Adapter中位置
    private int mFirstItemPosition;

    // 当前顶部第一个item
    private int mListTop;
    private int mListTopOffset;

    public HorizonListView(Context context) {
        super(context);
    }

    public HorizonListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizonListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

        // 当前ListView没有任何子视图(Item)，所以依次在从上向下填充子视图
        if (getChildCount() == 0) {
            mLastItemPosition = -1;
            // add and measure
            fillListDown(mListTop, 0);
        }

        // layout，添加测量完后，获取视图摆放位置
        positioinItems();

        // draw， 上面子视图都添加完了，重绘布局把子视图绘制出来吧
        invalidate();

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
            View newBottomChild = mAdapter.getView(mLastItemPosition, null, this);
            // **具体添加视图处理
            addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW);
            // 添加一个子视图(Item)，随之底部边界也发生改变
            bottomEdge += newBottomChild.getMeasuredWidth();
        }
    }


    /**
     * 向当前ListView添加子视图并负责Measure子视图操作
     *
     * @param child      需要添加的ListView子视图(Item)
     * @param layoutMode 在顶部添加上面添加还是在底部下面添加子视图 ， LAYOUT_MODE_ABOVE 或 LAYOUT_MODE_BELOW
     */
    private void addAndMeasureChild(View child, int layoutMode) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        final int index = layoutMode == LAYOUT_MODE_ABOVE ? 0 : -1;
        addViewInLayout(child, index, params, true);

        final int itemWidth = getHeight();
        // 位运算 | itemWidth表示添加此当前值
        child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY | itemWidth);
    }


    /**
     * 对所有子视图进行layout操作，取得所有子视图正确的位置
     */
    private void positioinItems() {
        int left = mListTop + mListTopOffset;

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

}