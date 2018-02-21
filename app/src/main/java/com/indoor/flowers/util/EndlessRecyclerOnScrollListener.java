package com.indoor.flowers.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.security.InvalidParameterException;

public class EndlessRecyclerOnScrollListener extends OnScrollListener implements OnTouchListener {

    private boolean loading = false;
    private LinearLayoutManager mLinearLayoutManager;
    private LoadMoreScrollListener listener;
    private GestureDetector gestureDetector;
    private float verticalChange = 0;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            verticalChange = distanceY;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    public EndlessRecyclerOnScrollListener(RecyclerView recyclerView, LoadMoreScrollListener listener) {
        this.listener = listener;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            this.mLinearLayoutManager = (LinearLayoutManager) layoutManager;
            this.mLinearLayoutManager.setSmoothScrollbarEnabled(true);
        } else {
            throw new InvalidParameterException("EndlessRecyclerOnScrollListener supports only LinearLayoutManager and nested classes.");
        }

        recyclerView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(recyclerView.getContext(), gestureListener);
    }

    public void onLoadingCompleted() {
        loading = false;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            boolean isStackFromEnd = mLinearLayoutManager.getStackFromEnd();
            if (isStackFromEnd && verticalChange < 0) {
                int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!loading && firstVisiblePosition <= 0) {
                    setLoading();
                }
            } else if (!isStackFromEnd && verticalChange > 0) {
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && (totalItemCount - lastVisiblePosition) <= 1) {
                    setLoading();
                }
            }
        }
    }

    private void setLoading() {
        loading = true;
        if (listener != null) {
            listener.onLoadMore();
        }
    }

    public interface LoadMoreScrollListener {
        void onLoadMore();
    }
}