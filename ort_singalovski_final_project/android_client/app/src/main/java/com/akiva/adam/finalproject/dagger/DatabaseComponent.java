package com.akiva.adam.finalproject.dagger;

import android.content.Context;

import com.akiva.adam.finalproject.activities.ImageListActivity;
import com.akiva.adam.finalproject.activities.MainActivity;
import com.akiva.adam.finalproject.activities.SettingsActivity;
import com.akiva.adam.finalproject.activities.UpdateProfileActivity;
import com.akiva.adam.finalproject.classes.MyAdapter;
import com.akiva.adam.finalproject.activities.ShowImageActivity;
import com.akiva.adam.finalproject.activities.SignupActivity;
import com.akiva.adam.finalproject.classes.MyToolbar;
import com.akiva.adam.finalproject.notifications.NotificationService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DatabaseModule.class, UserModule.class, SharedPreferencesModule.class, ContextModule.class})
public interface DatabaseComponent {

    Context context();

    void inject(MainActivity mainActivity);

    void inject(SignupActivity signupActivity);

    void inject(ImageListActivity imageListActivity);

    void inject(ShowImageActivity showImageActivity);

    void inject(NotificationService notification);

    void inject(UpdateProfileActivity updateProfileActivity);

    void inject(MyAdapter myAdapter);

    void inject(SettingsActivity settingsActivity);

    void inject(MyToolbar toolbar);
}
