package com.rmali.catfacts.view.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rmali.catfacts.R;
import com.rmali.catfacts.model.pojo.CatFactEntity;
import com.rmali.catfacts.view.callback.OnFactItemClickListener;
import com.rmali.catfacts.view.callback.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rmali on 11/8/2017.
 */

public class CatFactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private OnFactItemClickListener mOnFactClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private List<CatFactEntity> mFacts = new ArrayList<>();

    //Below fields are for the load more
    private int mVisibleThreshold = 5;// The minimum amount of items to have below your current scroll position before isLoading more.
    private int mLastVisibleItem, mTotalItemCount;
    private boolean mIsLoading = true;

    public CatFactsListAdapter(List<CatFactEntity> items, RecyclerView recyclerView, OnFactItemClickListener listener) {
        mFacts = items;
        mOnFactClickListener = listener;
        addLoadMoreListener(recyclerView);

    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    private void addLoadMoreListener(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    mTotalItemCount = linearLayoutManager.getItemCount();
                    mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                        // End has been reached
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mIsLoading = true;
                    }
                }
            });
        }

        mIsLoading = false;
    }


    public void setLoadingState(boolean isLoading) {
        mIsLoading = isLoading;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fact_item, parent, false);
           return new FactViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.load_more_progressbar, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof FactViewHolder){
            final FactViewHolder factViewHolder = (FactViewHolder) holder;
            factViewHolder.mFactEntity = mFacts.get(position);
                factViewHolder.fact.setText(factViewHolder.mFactEntity.getFact());
                factViewHolder.mLength.setText(factViewHolder.mFactEntity.getLength()+"");
                factViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnFactClickListener != null)
                        mOnFactClickListener.onFactItemCLicked(factViewHolder.mFactEntity);

                }
            });
        }
        else if(holder instanceof LoadingViewHolder){
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemViewType(int position) {
        return mFacts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM ;
    }

    @Override
    public int getItemCount() {
        return mFacts == null ? 0 : mFacts.size();
    }


    //Normal item view holder
    public static class FactViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CatFactEntity mFactEntity;

        @BindView(R.id.fact)
        TextView fact;
        @BindView(R.id.cat_length)
        TextView mLength;

        public FactViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + fact.getText() + "'";
        }
    }

    //Loading item viewHolder
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.load_more_progressbar)
        ProgressBar progressBar;

        public LoadingViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }
}
