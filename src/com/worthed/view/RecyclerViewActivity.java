package com.worthed.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenguo on 12/26/14.
 */
public class RecyclerViewActivity extends Activity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        init();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        dataList = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            dataList.add("" + i);
        }
        recyclerViewAdapter = new RecyclerViewAdapter(this, dataList);

        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
