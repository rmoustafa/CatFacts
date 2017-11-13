package com.rmali.catfacts;

import android.app.Application;

import com.rmali.catfacts.di.AppModule;
import com.rmali.catfacts.di.CatFactsRepositoryModule;
import com.rmali.catfacts.di.DaggerFactsComponent;
import com.rmali.catfacts.di.FactsComponent;

/**
 * Created by ramadanmoustafa on 11/13/17.
 */

public class FactsApplication extends Application{
    private FactsComponent mFactsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mFactsComponent = DaggerFactsComponent.builder()
                .appModule(new AppModule(this))
                .catFactsRepositoryModule(new CatFactsRepositoryModule())
                .build();
    }

    public FactsComponent getFactsComponent(){
        return mFactsComponent;
    }
}
