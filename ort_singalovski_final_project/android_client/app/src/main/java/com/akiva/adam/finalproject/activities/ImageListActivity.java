package com.akiva.adam.finalproject.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.classes.MyAdapter;
import com.akiva.adam.finalproject.classes.MySharedPreferences;
import com.akiva.adam.finalproject.classes.MyToolbar;
import com.akiva.adam.finalproject.classes.MyUser;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.interfaces.IFile;
import com.akiva.adam.finalproject.interfaces.ISharedPreferences;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.akiva.adam.finalproject.notifications.NotificationService;
import com.akiva.adam.finalproject.uiElements.AlertToast;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

/**
 * The "main page" activity which is shown to the user
 * after a successful login
 */
public class ImageListActivity extends MyActivity {

    // The database instance
    @Inject
    public Database database;
    private IDatabase mDatabase;

    // The current logged in user instance
    @Inject
    public MyUser user;
    private IUser mUser;

    // The SharedPreferences instance
    @Inject
    public MySharedPreferences sharedPreferences;
    private ISharedPreferences mSharedPreferences;

    // Custom made object for a toolbar
    private MyToolbar toolbar;

    // Progress bar to be shown while loading
    private ProgressBar pbLoading;

    // RecyclerView for the image list
    private RecyclerView rvImages;
    private RecyclerView.LayoutManager mLayoutManager;

    // An adapter for the RecyclerView
    private MyAdapter adapter;

    // Thread which updates the image list
    private Thread updateImageList;

    // Indicating the first run of the thread
    private boolean firstRun;

    // Handler to be used to run a process a runnable object using the MessageQueue
    private Handler handler;

    // Static variable used for logging
    private static final String TAG = ImageListActivity.class.getName();

    /**
     * A method that runs when the activity is started
     * mainly used to create the visual look of the
     * login screen
     *
     * @param savedInstanceState Parameters to recover when the activity is destroyed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);

        // Different method to start a service depending on android version
        Intent intent = new Intent(this, NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        mDatabase = database;
        mUser = user;
        mSharedPreferences = sharedPreferences;

        handler = new Handler(Looper.getMainLooper());

        try {
            // Checks whether the user is logged in, if not return the user
            // to the login screen
            if (mDatabase.getAuthService().getCurrentUser() == null) {
                goToActivity(ImageListActivity.this, MainActivity.class, null);
            }

            if (mUser.getFiles().isEmpty()) {
                mUser.setFiles();
            }

            setContentView(R.layout.activity_image_list);

            mSharedPreferences.writeToPreferences(ImageListActivity.class.getName(), true);

            // Checks whether the user was sent back to this activity
            // with an error message, and if so display the error in a toast
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TOAST_MESSAGE)) {
                AlertToast.makeText(ImageListActivity.this, getIntent().getExtras().getString(TOAST_MESSAGE), Toast
                        .LENGTH_LONG).show();
            }

            // Checks whether the user was sent to this activity with credentials from the
            // SignupActivity,
            // and if so write the credentials the FirebaseDatabase
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(USER_INFO)) {
                mUser.writeUserCredentialsToDatabase(getIntent().getExtras().getCharSequenceArray(USER_INFO));
            }

            // Setup the toolbar for this activity
            toolbar = new MyToolbar(ImageListActivity.this, (ViewGroup) findViewById(R.id.rlImageListActivity));

            pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

            rvImages = (RecyclerView) findViewById(R.id.rvImages);
            mLayoutManager = new LinearLayoutManager(ImageListActivity.this);
            rvImages.setLayoutManager(mLayoutManager);

            updateImageList = new Thread() {
                @Override
                public void run() {
                    try {
                        firstRun = true;
                        int i = 0;
                        while (true) {
                            i++;
                            if (firstRun && !mUser.getFileListLock().isLocked() || firstRun && i >= 8) {
                                runOnUiThread(() -> {
                                    mUser.getFileListLock().lock();
                                    adapter = new MyAdapter(ImageListActivity.this, new ArrayList<String>(mUser.getFiles().keySet()), getString(R
                                            .string.showImage), toolbar);
                                    pbLoading.setVisibility(View.GONE);
                                    rvImages.setAdapter(adapter);
                                    mUser.getFileListLock().unlock();
                                    firstRun = false;
                                });
                                for (Map.Entry<String, IFile> entry : mUser.getFiles().entrySet()) {
                                    entry.getValue().setViewed();
                                }
                                mUser.setValueChanged(false);
                            } else if (!firstRun && mUser.getValueChanged() && !mUser.getFileListLock().isLocked()) {
                                runOnUiThread(() -> {
                                    mUser.getFileListLock().lock();
                                    adapter = new MyAdapter(ImageListActivity.this, new ArrayList<String>(mUser.getFiles().keySet()), getString(R
                                            .string.showImage), toolbar);
                                    rvImages.swapAdapter(adapter, true);
                                    mUser.getFileListLock().unlock();
                                });
                                for (Map.Entry<String, IFile> entry : mUser.getFiles().entrySet()) {
                                    entry.getValue().setViewed();
                                }
                                mUser.setValueChanged(false);
                            }
                            else {
                                Thread.sleep(500);
                            }
                        }
                    } catch (InterruptedException | ConnectException e) {
                        MyActivity.displayError(TAG, e, null, null, null);
                    }
                }
            };

                if (!updateImageList.isAlive() && !updateImageList.isInterrupted()) {
                    updateImageList.start();
                }

        } catch (ConnectException e) {
            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
        }
    }

    /**
     * A method used to display the list of files
     * back on the screen after returning to the activity
     * without getting them from the database again
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (updateImageList != null && updateImageList.isInterrupted()) {
            updateImageList.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (updateImageList != null && !updateImageList.isInterrupted()) {
            updateImageList.interrupt();
        }
        mSharedPreferences.writeToPreferences(ImageListActivity.class.getName(), false);
    }

    /**
     * A method used to set the back button
     * to do nothing if the user is logged in
     * and if not to do the default action
     */
    @Override
    public void onBackPressed() {
        if (mUser.getUid() != null) {
            // do nothing
        } else {
            super.onBackPressed();
        }
    }
}
