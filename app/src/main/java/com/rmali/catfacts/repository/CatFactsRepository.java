package com.rmali.catfacts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rmali.catfacts.model.pojo.CatFactEntity;
import com.rmali.catfacts.model.pojo.CatFactResponse;
import com.rmali.catfacts.model.webservice.CatFactsApi;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Repository module is responsible for handling data operations.
 * it provides clean Api to the service consumers (ViewModel)
 * it knows where to get the data from and what API calls to make when data is updated
 * You can consider it as mediator between different data sources (persistent model, web service, cache, etc.)
 * .
 * Created by rmali on 11/8/2017.
 */

public class CatFactsRepository {
    private CatFactsApi mFactsApi;
    private MutableLiveData<CatFactResponse> data = new MutableLiveData<>();
    private CompositeDisposable mCompositeDisposable;

    public CatFactsRepository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CatFactsApi.CAT_FACTS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mFactsApi = retrofit.create(CatFactsApi.class);
    }

    /**
     * fetches list of Cat Facts from a web service
     */
    public MutableLiveData<CatFactResponse> getCatFacts(int limit, int max_length, int currentPage) {
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(mFactsApi.getCatFactsList(limit, max_length, currentPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith( new DisposableObserver<CatFactResponse>(){

                    @Override
                    public void onNext(CatFactResponse factResponse) {
                        data.setValue(factResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        data.setValue(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );

        return data;
    }

    /**
     * fetches a random Cat Fact from a web service
     */
    public LiveData<CatFactEntity> getRandomCatFact(int max_length) {
        final MutableLiveData<CatFactEntity> data = new MutableLiveData<>();
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(mFactsApi.getRandomCatFact(max_length)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith( new DisposableObserver<CatFactEntity>(){

                    @Override
                    public void onNext(CatFactEntity factEntity) {
                        data.setValue(factEntity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        data.setValue(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
        return data;
    }
}
