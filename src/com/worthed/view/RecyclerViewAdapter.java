package com.worthed.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenguo on 12/26/14.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;

    private List<String> dataList;

    public RecyclerViewAdapter(Context context, List<String> dataList) {
        mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_recyclerview,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);

        viewHolder.textView = (TextView) view.findViewById(R.id.textView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(R.drawable.ic_launcher);
        viewHolder.textView.setText(dataList.get(i));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
