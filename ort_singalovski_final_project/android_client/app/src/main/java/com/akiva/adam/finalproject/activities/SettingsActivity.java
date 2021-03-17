package com.akiva.adam.finalproject.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.classes.MyToolbar;
import com.akiva.adam.finalproject.classes.MyUser;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.akiva.adam.finalproject.uiElements.MyDialog;

import java.net.ConnectException;

import javax.inject.Inject;

/**
 * This activity is used to change the user's setting related
 * to the notification service
 */
public class SettingsActivity extends MyActivity {

    @Inject
    public MyUser user;
    private IUser mUser;

    @Inject
    public Database database;
    private IDatabase mDatabase;

    private MyToolbar toolbar;

    private Switch swNotifications;
    private Button btnSave;

    private Handler handler;

    // Static variable used for logging
    public static final String TAG = SettingsActivity.class.getName();

    /**
     * A method that runs when the activity is started
     * mainly used to create the visual look of the
     * login screen
     *
     * @param savedInstanceState Parameters to recover when the activity is destroyed
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);
        mDatabase = database;
        mUser = user;

        handler = new Handler(Looper.getMainLooper());

        try {
            // Checks whether the user is logged in, if not return the user
            // to the login screen
            if (database.getAuthService().getCurrentUser() == null || user.getUid() == null) {
                goToActivity(SettingsActivity.this, MainActivity.class, null);
            }

            // Checks whether the user was sent back to this activity
            // with an error message, and if so display the error in a toast
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TOAST_MESSAGE)) {
                AlertToast.makeText(SettingsActivity.this, getIntent().getExtras().getString(TOAST_MESSAGE), Toast
                        .LENGTH_LONG).show();
            }

            setContentView(R.layout.activity_settings);

            swNotifications = (Switch) findViewById(R.id.swNotifications);
            swNotifications.setChecked(mUser.getSettings().getNotifications());
            btnSave = (Button) findViewById(R.id.btnSave);

            // Setup the toolbar for this activity
            toolbar = new MyToolbar(SettingsActivity.this, (ViewGroup) findViewById(R.id.rlSettingsActivity));

            // Setup an alert message when the user tries to leave the page
            // alerting him that he did not save the new settings
            toolbar.setCustomLogoutButtonEvent((View v) -> {
                if (swNotifications.isChecked() != mUser.getSettings().getNotifications()) {
                    final MyDialog dialog = new MyDialog(SettingsActivity.this);
                    dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString
                            (R.string.didNotPressSave), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R
                            .string.yes), (View v2) -> {
                        dialog.dismiss();
                        goToActivity(SettingsActivity.this, ImageListActivity.class, null);
                    }).setNegativeButton(getString(R.string.no), (View v3) -> dialog.dismiss()).show();
                } else {
                    try {
                        mUser.requestLogout();
                        goToActivity(SettingsActivity.this, MainActivity.class, null);
                    } catch (ConnectException e) {
                        MyActivity.displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
                    }
                }
            }).setCustomHomeButtonEvent((View v) -> {
                if (swNotifications.isChecked() != mUser.getSettings().getNotifications()) {
                    final MyDialog dialog = new MyDialog(SettingsActivity.this);
                    dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString
                            (R.string.didNotPressSave), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R
                            .string.yes), (View v2) -> {
                        dialog.dismiss();
                        goToActivity(SettingsActivity.this, ImageListActivity.class, null);
                    }).setNegativeButton(getString(R.string.no), (View v3) -> dialog.dismiss()).show();
                } else {
                    goToActivity(SettingsActivity.this, ImageListActivity.class, null);
                }
            }).setCustomUpdateProfileEvent((View v) -> {
                if (swNotifications.isChecked() != mUser.getSettings().getNotifications()) {
                    final MyDialog dialog = new MyDialog(SettingsActivity.this);
                    dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString
                            (R.string.didNotPressSave), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R
                            .string.yes), (View v2) -> {
                        dialog.dismiss();
                        goToActivity(SettingsActivity.this, ImageListActivity.class, null);
                    }).setNegativeButton(getString(R.string.no), (View v2) -> dialog.dismiss()).show();
                } else {
                    goToActivity(SettingsActivity.this, ImageListActivity.class, null);
                }
            });
            // On clicking the save button save the notification field
            // in the database for the current user according to the chosen
            // setting, as well as returning the user to the ImageListActivity
            btnSave.setOnClickListener((View v) -> {
                if (swNotifications.isChecked()) {
                    mUser.getSettings().setNotifications(true);
                } else {
                    mUser.getSettings().setNotifications(false);
                }
                new Thread(() -> {
                    try {
                        mDatabase.getDatabaseService().getReference().child(USERS + "/" + mUser.getUid() + "/" +
                                SETTINGS).setValue(mUser.getSettings());
                    } catch (ConnectException e) {
                        MyActivity.displayError(TAG, e, this, getString(R.string.settingsSaveFailed), null);
                    }
                }).start();
                goToActivity(SettingsActivity.this, ImageListActivity.class, null);
            });

        } catch (ConnectException e) {
            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
        }
    }

    @Override
    public void onBackPressed() {
        if (swNotifications.isChecked() != mUser.getSettings().getNotifications()) {
            final MyDialog dialog = new MyDialog(SettingsActivity.this);
            dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString(R
                    .string.didNotPressSave), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R.string
                    .yes), (View v) -> {

                dialog.dismiss();
                goToActivity(SettingsActivity.this, ImageListActivity.class, null);
            }).setNegativeButton(getString(R.string.no), (View v) -> dialog.dismiss()).show();
        } else {
            super.onBackPressed();
        }
    }
}
