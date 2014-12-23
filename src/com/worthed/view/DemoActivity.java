package com.worthed.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class DemoActivity extends Activity {

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
    }

}
