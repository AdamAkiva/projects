package com.akiva.adam.finalproject.dagger;

import android.content.Context;

import com.akiva.adam.finalproject.classes.MySharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    @Provides
    @Singleton
    MySharedPreferences provideSharedPreferences(Context context) {
        return new MySharedPreferences(context);
    }
}
