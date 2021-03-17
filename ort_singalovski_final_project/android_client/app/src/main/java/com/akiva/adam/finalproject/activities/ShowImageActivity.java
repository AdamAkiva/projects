package com.akiva.adam.finalproject.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.classes.MyToolbar;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.interfaces.IFile;
import com.akiva.adam.finalproject.classes.MyUser;
import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.github.chrisbanes.photoview.PhotoView;

import java.net.ConnectException;

import javax.inject.Inject;

/**
 * This activity is used to show a specific image from the list
 * in the ImageListActivity
 */
public class ShowImageActivity extends MyActivity {

    @Inject
    public Database database;
    private IDatabase mDatabase;

    @Inject
    public MyUser user;
    private IUser mUser;

    private MyToolbar toolbar;

    private TextView tvTimeAndDateInformation;

    // Library used to display an image with zoom options
    private PhotoView ivImage;

    private Handler handler;

    private static final String TAG = ShowImageActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);
        mDatabase = database;
        mUser = user;

        handler = new Handler(Looper.getMainLooper());

        try {
            // Checks whether the user is logged in, if not return the user
            // to the login screen
            if (mDatabase.getAuthService().getCurrentUser() == null || mUser.getUid() == null) {
                goToActivity(ShowImageActivity.this, MainActivity.class, null);
            }

            // Hold the image file
            IFile file = null;

            // Checks whether the activity was started with an image key,
            // if so use it to display the image,
            // if not return to the ImageListActivity
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(KEY)) {
                file = mUser.getFiles().get(getIntent().getExtras().getString(KEY));
            } else {
                goToActivity(ShowImageActivity.this, ImageListActivity.class, null);
            }

            setContentView(R.layout.activity_show_image);

            toolbar = new MyToolbar(ShowImageActivity.this, (ViewGroup) findViewById(R.id.rlShowImageActivity));

            if (file != null) {
                tvTimeAndDateInformation = (TextView) findViewById(R.id.tvTimeAndDateInformation);
                tvTimeAndDateInformation.setText(getString(R.string.timeAndDateAlert, file.getViewDate(), file
                        .getViewTime()));
                ivImage = (PhotoView) findViewById(R.id.ivImage);
                ivImage.setImageBitmap(BitmapFactory.decodeByteArray(file.getFileBytes(), 0, file.getFileBytes()
                        .length));
            }

        } catch (ConnectException e) {
            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
        }
    }
}