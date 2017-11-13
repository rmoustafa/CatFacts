package com.rmali.catfacts.di;

import com.rmali.catfacts.FactsApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ramadanmoustafa on 11/13/17.
 */

@Module
public class AppModule {
    private FactsApplication mApplication;

    public AppModule(FactsApplication application){
        mApplication = application;
    }

    @Provides
    @Singleton
    FactsApplication providesApplication(){
        return mApplication;
    }

}
