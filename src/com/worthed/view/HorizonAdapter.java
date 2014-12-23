package com.worthed.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by zhenguo on 12/23/14.
 */
public class HorizonAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> resIdList;

    public HorizonAdapter(Context context, List<Integer> resId) {
        Log.d("HorizonListView", "HorizonAdapter()");
        this.context = context;
        this.resIdList = resId;
    }

    @Override
    public int getCount() {
        if (resIdList != null) {
            return resIdList.size();
        }
        return 0;
    }

    @Override
    public Integer getItem(int position) {
        return resIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("HorizonListView", "getView() position : " + position);
        ImageView iv = new ImageView(context);
        iv.setBackgroundColor(Color.RED);
        iv.setImageResource(getItem(position));

        return iv;
    }
}
