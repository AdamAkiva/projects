package com.akiva.adam.finalproject.dagger;

import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.classes.MyUser;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UserModule {

    @Provides
    @Singleton
    MyUser providesUser(Database database) {
        return new MyUser(database);
    }
}
