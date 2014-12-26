package com.worthed.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * 水平ListView适配器
 *
 * Created by zhenguo on 12/23/14.
 */
public class HorizonAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> resIdList;
    private LayoutInflater mInflater;

    public HorizonAdapter(Context context, List<Integer> resId) {
        Log.d("HorizonListView", "HorizonAdapter()");
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
    public String getItem(int position) {
        return "" + position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("HorizonListView", "getView() position : " + position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_customdemo, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        name.setText(getItem(position));

        return convertView;
    }
}
