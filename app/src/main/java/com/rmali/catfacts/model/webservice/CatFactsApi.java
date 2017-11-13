package com.rmali.catfacts.model.webservice;

import com.rmali.catfacts.model.pojo.CatFactEntity;
import com.rmali.catfacts.model.pojo.CatFactResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by rmali on 11/8/2017.
 */

public interface CatFactsApi {
    static final String CAT_FACTS_BASE_URL = "https://catfact.ninja/";
    @GET("fact")
    Call<CatFactEntity> getRandomCatFact(@Query("max_length") Integer maxLength);
    @GET("facts")
    Call<CatFactResponse> getCatFactsList(@Query("limit") Integer limit,
                                          @Query("max_length") Integer maxLength,
                                          @Query("page") Integer current_page);
}
