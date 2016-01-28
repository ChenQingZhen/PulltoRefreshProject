package com.cqz.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cqz.app.view.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity implements PullToRefreshLayout.OnRefreshListener {
    private PullToRefreshLayout mPullToRefreshLayout;
    private RecyclerView rvTest;
    private RecyclerAdapter mAdapter;
    private List<String> mDataList;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initialWidgets();
    }

    private void initialWidgets() {
        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptrl);
        rvTest = (RecyclerView) findViewById(R.id.rv_test);
        rvTest.setHasFixedSize(true);
        LinearLayoutManager lm=new LinearLayoutManager(this);
        rvTest.setLayoutManager(lm);
        mDataList = new ArrayList<String>();
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
        mAdapter = new RecyclerAdapter();
        rvTest.setAdapter(mAdapter);
        mPullToRefreshLayout.setRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                i++;
                mDataList.add(0,""+i);
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



    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private final View.OnClickListener mClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position= rvTest.getChildAdapterPosition(view);
                Toast.makeText(RecyclerViewActivity.this,"单击："+position,Toast.LENGTH_SHORT).show();
            }
        };
        private final View.OnLongClickListener mLongClickListener=new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position=rvTest.getChildAdapterPosition(view);
                Toast.makeText(RecyclerViewActivity.this,"长按："+position,Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RecyclerViewActivity.this).inflate(R.layout.view_recyclerview_item, parent, false);
            view.setOnClickListener(mClickListener);
            view.setOnLongClickListener(mLongClickListener);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String text = mDataList.get(position);
            holder.tvContent.setText(text);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvContent;

            public ViewHolder(View itemView) {
                super(itemView);
                tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            }


        }


    }


}
