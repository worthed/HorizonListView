package com.worthed.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

public class DemoActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private HorizonListView horizon_list_view;
    private HorizonAdapter horizonAdapter;

    private ArrayList<Integer> resIdList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d("HorizonListView", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        horizon_list_view = (HorizonListView) findViewById(R.id.horizon_list_view);

        resIdList = new ArrayList<Integer>();

        for (int i = 0; i < 20; i++) {
            resIdList.add(R.drawable.ic_launcher);
        }

        horizonAdapter = new HorizonAdapter(this, resIdList);
        horizon_list_view.setAdapter(horizonAdapter);
        horizon_list_view.setOnItemClickListener(this);
        horizon_list_view.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "onItemClick() position : " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "onItemLongClick() position : " + position, Toast.LENGTH_SHORT).show();
        return false;
    }
}
