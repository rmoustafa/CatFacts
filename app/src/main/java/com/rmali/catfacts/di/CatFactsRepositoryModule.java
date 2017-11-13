package com.rmali.catfacts.di;

import com.rmali.catfacts.repository.CatFactsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rmali on 11/8/2017.
 */

@Module
public class CatFactsRepositoryModule {
    @Provides
    @Singleton
    public CatFactsRepository provideFactsRepository(){
        return new CatFactsRepository();
    }
}
