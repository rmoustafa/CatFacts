package com.rmali.catfacts.di;

import com.rmali.catfacts.viewmodel.FactsViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ramadanmoustafa on 11/13/17.
 */

@Singleton
@Component(modules = {AppModule.class, CatFactsRepositoryModule.class})
public interface FactsComponent {
    void inject(FactsViewModel factsViewModel);
}
