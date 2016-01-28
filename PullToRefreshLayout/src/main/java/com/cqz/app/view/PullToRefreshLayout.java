package com.cqz.app.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cqz.app.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chenqingzhen on 2016/1/12.
 */
public class PullToRefreshLayout extends RelativeLayout {
    private static final float PULL_RATE = .5f;
    //下拉刷新显示在AbsListView上显示的布局
    private View mHeader;
    //可以是AbsListView的子类
    private View mContentView;
    private TextView tvStatus;
    private ImageView ivRefresh;
    private ImageView ivPull;

    private boolean mIsBeingPulled;

    private boolean canPull;

    private float downY;

    private float pullDistance;
    //释放刷新的距离，当下拉距离大于这个距离的时候，便会更改状态
    private static final float REFRESH_DISTANCE = 200;
    private boolean isLayout;
    private int mTouchSlop;

    private static final int PULL_TO_REFRESH=0;
    private static final int RELEASE_TO_REFRESH=1;
    private static final int REFRESHING=2;
    private int mStatus;
    private RotateAnimation mPullAnim;
    private RotateAnimation mRefreshAnim;
    private OnRefreshListener mListener;
    private Timer mBackTimer;


    public PullToRefreshLayout(Context context) {
        this(context, null);

    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mPullAnim= (RotateAnimation) AnimationUtils.loadAnimation(context,R.anim.anim_reverse);
        mRefreshAnim= (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.anim_refresh);
        LinearInterpolator li=new LinearInterpolator();
        mPullAnim.setInterpolator(li);
        mRefreshAnim.setInterpolator(li);
        initView();
    }


    private void initView(){
       if(getChildCount()==0||getChildCount()==1){
           return;
       }
        mHeader=getChildAt(0);
        mContentView= getChildAt(1);
        tvStatus= (TextView) mHeader.findViewById(R.id.tv_status);
        ivRefresh= (ImageView) mHeader.findViewById(R.id.iv_refresh);
        ivPull= (ImageView) mHeader.findViewById(R.id.iv_pull);
    }

    private void changeStatus(int status){
        mStatus=status;
        switch (mStatus){
            case PULL_TO_REFRESH:
                ivRefresh.clearAnimation();
                ivRefresh.setVisibility(View.GONE);
                ivPull.setVisibility(View.VISIBLE);
                ivPull.clearAnimation();
                tvStatus.setText(R.string.pull_to_refresh);
                break;
            case RELEASE_TO_REFRESH:
                ivPull.startAnimation(mPullAnim);
                tvStatus.setText(R.string.release_to_refresh);
                break;
            case REFRESHING:
                ivPull.clearAnimation();
                ivPull.setVisibility(View.GONE);
                ivRefresh.setVisibility(View.VISIBLE);
                ivRefresh.startAnimation(mRefreshAnim);
                tvStatus.setText(R.string.refreshing);
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action= MotionEventCompat.getActionMasked(ev);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mIsBeingPulled=false;
                downY= ev.getY();
                Log.d("down_y", "downY=" + downY);
                if(mBackTimer!=null){
                    mBackTimer.cancel();
                    mBackTimer.purge();
                    mBackTimer=null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(canPull()&&(ev.getY()-downY>0||(mStatus==REFRESHING&&downY-ev.getY()>0))) {
                    mIsBeingPulled = true;
                }else{
                    mIsBeingPulled=false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                    mIsBeingPulled = false;
                break;
        }
        return mIsBeingPulled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE: {
                float upPullDistance=0;
                if(event.getY() - downY>0) {
                    pullDistance =event.getY() - downY;
                }else{
                    upPullDistance=event.getY() - downY;
                }
                if (pullDistance > REFRESH_DISTANCE&&mStatus==PULL_TO_REFRESH) {
                    changeStatus(RELEASE_TO_REFRESH);
                }else if(pullDistance<REFRESH_DISTANCE&&mStatus==RELEASE_TO_REFRESH){
                    changeStatus(PULL_TO_REFRESH);
                }else if(upPullDistance<0&&mStatus==REFRESHING){
                    //正在刷新还上拉的情况
                    pullDistance+=upPullDistance;
                }
                Log.d("pull_distance", "pull_distance=" + pullDistance);
                requestLayout();
            }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mStatus==RELEASE_TO_REFRESH) {
                    pullDistance = REFRESH_DISTANCE;
                    changeStatus(REFRESHING);
                    requestLayout();
                    if(mListener!=null){
                        mListener.onRefresh();
                    }

                }else if(pullDistance>REFRESH_DISTANCE&&mStatus==REFRESHING){
                    //正在刷新还往下拉的情况
                  pullDistance=REFRESH_DISTANCE;
                  requestLayout();
                  }else{
                   setRefreshing(false);
                  }
                break;

        }
        return false;
    }

    private Handler h=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if(msg.what==0x123){
               if(pullDistance<=-8){
                   pullDistance=0;
                   changeStatus(PULL_TO_REFRESH);
                   if(mBackTimer!=null){
                       mBackTimer.cancel();
                       mBackTimer.purge();
                       mBackTimer=null;
                   }
               }
                requestLayout();
           }
        }
    };

    private boolean canPull(){
        if(mContentView instanceof ViewGroup){
            ViewGroup vg= (ViewGroup) mContentView;
            if(vg.getChildCount()==0||vg.getChildAt(0).getTop()>=getPaddingTop()){
                canPull=true;
                return  true;
            }
        }
        canPull=false;
        return false;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isLayout){
            initView();
            isLayout=true;
        }
        if(mContentView!=null&&mHeader!=null&&(pullDistance>0||pullDistance==0)) {
            int moveDistance= (int) (pullDistance*(PULL_RATE+PULL_RATE*(1/pullDistance)));
            mContentView.layout(getPaddingLeft(), getPaddingTop() + moveDistance, getPaddingLeft() + mContentView.getMeasuredWidth(), getPaddingTop() + moveDistance + mContentView.getMeasuredHeight());
            mHeader.layout(getPaddingLeft(), getPaddingTop() - mHeader.getMeasuredHeight() + moveDistance, getPaddingLeft() + mHeader.getMeasuredWidth(), getPaddingTop() + moveDistance);
        }
    }
    public void setRefreshing(boolean isRefresh){
        if(!isRefresh){
            mBackTimer=new Timer();
            mBackTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pullDistance -= 8;
                    h.sendEmptyMessage(0x123);
                }
            }, 0, 3);

        }
    }
    public void setRefreshListener(OnRefreshListener listener){
        this.mListener=listener;
    }

    public interface OnRefreshListener{
        void onRefresh();
    }
}
