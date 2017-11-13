package com.rmali.catfacts.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.rmali.catfacts.FactsApplication;
import com.rmali.catfacts.model.pojo.CatFactEntity;
import com.rmali.catfacts.model.pojo.CatFactResponse;
import com.rmali.catfacts.repository.CatFactsRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A viewModel class, that holds the data needed by the view class{@link com.rmali.catfacts.view.uicontrollers.CatFactsListActivity}
 * handles the communication with the business part & model layer
 * it survives the configuration changes
 *
 * Created by rmali on 11/8/2017.
 */

public class FactsViewModel extends AndroidViewModel {

    private MutableLiveData<List<CatFactEntity>> mCatFactsList; //observable data holder for the facts data
    private MutableLiveData<Integer>  mMaxLength = new MutableLiveData<>(); //holds the maxlength value entered by user
    private MutableLiveData<Boolean> mShowProgressState = new MutableLiveData<>();
    private static final int NUMBER_OF_ITEMS_PER_PAGE = 10; //a constant for loading more data from server
    private int mCurrentPage = 1;
    private int mLastPage;

    @Inject
    CatFactsRepository mRepository;
    private List<CatFactEntity> newFacts = new ArrayList<>();

    public FactsViewModel(Application application){
        super(application);
        ((FactsApplication)application).getFactsComponent().inject(this);

        mMaxLength.setValue(100);
    }

    /**
     * returns facts data or asks the repository to fetch them
     * @return
     */
    public LiveData<List<CatFactEntity>> getFacts(){
        if(mCatFactsList == null) { //No data
            mCatFactsList = new MutableLiveData<>();
            mShowProgressState.setValue(true); //shows the progressbar till loading data is done
            loadFacts();
        }
        return mCatFactsList;
    }

    /**
     * fetches data from repository data source based on page number, max lenght, and limit items per bage
     */
    public void loadFacts(){
        final MutableLiveData<CatFactResponse> response = mRepository.getCatFacts(NUMBER_OF_ITEMS_PER_PAGE, mMaxLength.getValue(), mCurrentPage);

        mCatFactsList = (MutableLiveData<List<CatFactEntity>>) Transformations.map(response, new Function<CatFactResponse, List<CatFactEntity>>() {

            @Override
            public List<CatFactEntity> apply(CatFactResponse input) {
                mShowProgressState.setValue(false);
                if(input != null) {
                    newFacts.addAll(input.getData());
                    mLastPage = input.getLastPage();
                    mCurrentPage = input.getCurrentPage();
                }
                return newFacts;
            }
        });

    }


    public LiveData<Integer> getMaxLength(){
        return mMaxLength;
    }

    public void setMaxLength(int value){
        // load data if max length changed
        if(mMaxLength.getValue() != value){
            mMaxLength.setValue(value);
            newFacts.clear();
            mShowProgressState.setValue(true); //shows the progressbar till loading data is done
            loadFacts();

        }
    }

    public LiveData<Boolean> getProgressState(){
        return mShowProgressState;
    }


    /**
     * loads more data if there is.
     * @return false if end of data is reached
     */
    public boolean loadMoreFacts(){
        if(mCurrentPage < mLastPage) {
            mCurrentPage++;
            loadFacts();
            return true;
        }
        return false;

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
