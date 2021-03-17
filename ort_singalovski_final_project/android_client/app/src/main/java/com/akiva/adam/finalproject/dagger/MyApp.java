package com.akiva.adam.finalproject.dagger;

import android.app.Application;

public class MyApp extends Application {

    private DatabaseComponent databaseComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseComponent = DaggerDatabaseComponent.builder().contextModule(new ContextModule(getApplicationContext()
        )).databaseModule(new DatabaseModule()).userModule(new UserModule()).build();
    }

    public DatabaseComponent getDatabaseComponent() {
        return databaseComponent;
    }
}
