package com.rmali.catfacts.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.rmali.catfacts.model.pojo.CatFactEntity;
import com.rmali.catfacts.model.pojo.CatFactResponse;
import com.rmali.catfacts.model.webservice.CatFactsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    public CatFactsRepository(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CatFactsApi.CAT_FACTS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mFactsApi = retrofit.create(CatFactsApi.class);
    }

    /**
     * fetches list of Cat Facts from a web service
     */
    public MutableLiveData<CatFactResponse> getCatFacts(int limit, int max_length, int currentPage) {
        mFactsApi.getCatFactsList(limit, max_length, currentPage).enqueue(new Callback<CatFactResponse>() {
            @Override
            public void onResponse(Call<CatFactResponse> call, Response<CatFactResponse> response) {
                if(response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CatFactResponse> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }

    /**
     * fetches a random Cat Fact from a web service
     */
    public LiveData<CatFactEntity> getRandomCatFact(int max_length) {
        final MutableLiveData<CatFactEntity> data = new MutableLiveData<>();
        mFactsApi.getRandomCatFact(max_length).enqueue(new Callback<CatFactEntity>() {
            @Override
            public void onResponse(Call<CatFactEntity> call, Response<CatFactEntity> response) {
                if(response.isSuccessful())
                    data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<CatFactEntity> call, Throwable t) {
                Log.e("Failed",t.getMessage().toString());
                data.setValue(null);
            }
        });

        return data;
    }
}
