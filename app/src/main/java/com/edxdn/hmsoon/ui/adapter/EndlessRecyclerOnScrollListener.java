package com.edxdn.hmsoon.ui.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private boolean firstLoad = false;
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int min_current_page = 1;
    private int max_current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                onScrolledUpDown(3);
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                onScrolledUpDown(4);
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                System.out.println("Scroll Settling");
                break;

        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(firstLoad){
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // Do something
                max_current_page++;
                onLoadMore(max_current_page);

                loading = true;
            }
        }


        if(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition()==0){
            if(firstLoad){
                if(min_current_page -1 != 0){
                    min_current_page--;
                    if(min_current_page != max_current_page && min_current_page != 0){
                        onScrolledToTop(min_current_page);
                    }
                }

            }


        }else if (dy < 0) {
            onScrolledUpDown(1);
            firstLoad = true;
        } else if (dy > 0) {
            onScrolledUpDown(2);
            firstLoad = true;
        }

    }

    public abstract void onLoadMore(int current_page);
    public abstract void onScrolledToTop(int current_page);
    public abstract void onScrolledUpDown(int dy);
    public void actLoadMore(){
        max_current_page++;
        onLoadMore(max_current_page);
    }

    public void setCurrent_page(int current_page){
        this.max_current_page = current_page;
        this.min_current_page = current_page;
    }

    public int getMinCurrentPage(){
        return min_current_page;
    }

    public void setMinCurrentPage(int min_page){
        this.min_current_page = min_page;
    }
}