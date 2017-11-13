package com.rmali.catfacts.view.uicontrollers;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rmali.catfacts.R;
import com.rmali.catfacts.model.pojo.CatFactEntity;
import com.rmali.catfacts.view.adapter.CatFactsListAdapter;
import com.rmali.catfacts.view.callback.OnFactItemClickListener;
import com.rmali.catfacts.view.callback.OnLoadMoreListener;
import com.rmali.catfacts.viewmodel.FactsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * a CatFactsListActivity responsible for displaying the cat facts data in a recyclerview
 * It represents the View in this MVVM application architecture
 *
 * @author rmali
 */
public class CatFactsListActivity extends AppCompatActivity {
    private static final String TAG = CatFactsListActivity.class.getSimpleName();

    @BindView(R.id.facts_recyclerview)
    RecyclerView mFactsRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mLoadDataProgressBar;
    @BindView(R.id.no_data_text)
    TextView mNoDataTextView;
    @BindView(R.id.maxLength_seekbar)
    SeekBar mMaxLengthSlider;
    @BindView(R.id.max_length)
    TextView mMaxLengthVaue;

    private CatFactsListAdapter mFactsAdapter;
    private List<CatFactEntity> mFacts = new ArrayList<>();
    private FactsViewModel mFactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cats_list);
        ButterKnife.bind(this);
        initLayoutViews();

        mFactViewModel = ViewModelProviders.of(this).get(FactsViewModel.class);
        observeFactsData();
        observeMaxLengthValue();
        observeProgessBarState();

    }

    /**
     * Initializes the recyclerview, slider, and any other ui widgets
     */
    private void initLayoutViews() {

        mFactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFactsRecyclerView.setHasFixedSize(true);
        mFactsAdapter = new CatFactsListAdapter(mFacts, mFactsRecyclerView, new OnFactItemClickListener() {

            @Override
            public void onFactItemCLicked(@NonNull CatFactEntity factEntity) {
                shareFact(factEntity.getFact());
            }
        });
        mFactsRecyclerView.setAdapter(mFactsAdapter);

        mFactsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreFacts();
            }
        });

        mMaxLengthSlider.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        mMaxLengthSlider.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        mMaxLengthSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mFactViewModel.setMaxLength(seekBar.getProgress());

            }
        });

    }

    /**
     * observes the catfacts data changes, provided by the {@link FactsViewModel}
     */
    private void observeFactsData() {
        mFactViewModel.getFacts().observe(this, new Observer<List<CatFactEntity>>() {
            @Override
            public void onChanged(@Nullable List<CatFactEntity> catFactEntities) {
                if(catFactEntities != null) {
                    //update ui
                    mFacts.clear();
                    mFacts.addAll(catFactEntities);
                    mFactsAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Facts count: " + mFacts.size());
                }
                if(mFacts != null && mFacts.size() >0)
                    showErrorMessage(false);
                else
                    showErrorMessage(true);
                mFactsAdapter.setLoadingState(false);

            }
        });
    }

    /**
     * Load more facts when user scrolls to the end of the list
     */
    private void loadMoreFacts() {
        Log.d(TAG,"Load more triggered ...");

        if(mFactViewModel.loadMoreFacts()){
            //Show Loading item in list adapter
            mFacts.add(null);
            mFactsRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mFactsAdapter.notifyItemInserted(mFacts.size() - 1);
                }
            });
        }
        else
            mFactsAdapter.setLoadingState(false);

    }

    /**
     * observes the max length value, to be retained on configuaration changed
     */
    private void observeMaxLengthValue() {
        mFactViewModel.getMaxLength().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                mMaxLengthSlider.setProgress(progress);
                mMaxLengthVaue.setText(String.valueOf(progress));
            }
        });
    }

    private void observeProgessBarState() {
        mFactViewModel.getProgressState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean showState) {
                showProgressBar(showState);
            }
        });

    }

    /**
     * shows no data found message when no data fetched
     * @param visible
     */
    private void showErrorMessage(boolean visible) {
        mNoDataTextView.setVisibility(!visible? View.GONE : View.VISIBLE);
    }

    private void showProgressBar(Boolean showState) {
        mLoadDataProgressBar.setVisibility(!showState? View.GONE : View.VISIBLE);
    }

    private void shareFact(String fact){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, fact);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_title)));
    }

    /**
     * removes the loading row at the end of the list, when loading is done
     */
    /*private void removeLoadMoreItemFromList() {
        //if the last item = null (refers to loading row item, then remove it from list
        if(mFacts != null && mFacts.size() > 0 && mFacts.get(mFacts.size() -1) == null){
            mFacts.remove(mFacts.size() - 1);
            mFactsAdapter.notifyItemRemoved(mFacts.size());
            mFactsAdapter.setLoadingState(false);
        }
    }*/

}