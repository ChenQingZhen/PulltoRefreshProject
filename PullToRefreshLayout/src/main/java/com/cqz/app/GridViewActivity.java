package com.cqz.app;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.cqz.app.view.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class GridViewActivity extends AppCompatActivity implements PullToRefreshLayout.OnRefreshListener {
    private PullToRefreshLayout mPullToRefreshLayout;
    private List<String> mDataList;
    private ArrayAdapter mAdapter;
    private int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        initialWidgets();
    }

    private void initialWidgets() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPullToRefreshLayout= (PullToRefreshLayout) findViewById(R.id.ptrl);
        GridView gvTest= (GridView) findViewById(R.id.gv_test);
        gvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(GridViewActivity.this,"单击："+i,Toast.LENGTH_SHORT).show();
            }
        });
        gvTest.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(GridViewActivity.this, "长按：" + i, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mDataList=new ArrayList<String>();
        mDataList.add("1");
        mDataList.add("2");
        mDataList.add("3");
        mDataList.add("4");
        mDataList.add("5");
        mDataList.add("6");
        mDataList.add("7");
        mDataList.add("8");
        mDataList.add("9");
        mDataList.add("10");
        mDataList.add("11");
        mDataList.add("12");
        mDataList.add("13");
        mDataList.add("14");
        mDataList.add("15");
        mDataList.add("16");
        mDataList.add("17");
        i=17;
        mAdapter= new ArrayAdapter(this,R.layout.view_girdview_item,R.id.tv_content,mDataList);
        gvTest.setAdapter(mAdapter);
        mPullToRefreshLayout.setRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                i++;
                mDataList.add("" +i);
                mAdapter.notifyDataSetChanged();
                mPullToRefreshLayout.setRefreshing(false);
            }
        },3000);


    }
    private Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
}
