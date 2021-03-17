package com.akiva.adam.finalproject.dagger;

import android.content.Context;

import com.akiva.adam.finalproject.classes.Database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    Database provideDatabase(Context context) {
        return new Database(context);
    }

}
