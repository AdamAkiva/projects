package com.akiva.adam.finalproject.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.activities.ImageListActivity;
import com.akiva.adam.finalproject.activities.MainActivity;
import com.akiva.adam.finalproject.activities.MyActivity;
import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.classes.MySharedPreferences;
import com.akiva.adam.finalproject.classes.MyUser;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.interfaces.ISharedPreferences;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.net.ConnectException;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import static com.akiva.adam.finalproject.activities.MyActivity.CHANNEL_ID;
import static com.akiva.adam.finalproject.activities.MyActivity.IMAGES;
import static com.akiva.adam.finalproject.activities.MyActivity.LOGGED_IN;
import static com.akiva.adam.finalproject.activities.MyActivity.NOTIFICATIONS;
import static com.akiva.adam.finalproject.activities.MyActivity.NOTIFICATION_ID;
import static com.akiva.adam.finalproject.activities.MyActivity.SETTINGS;
import static com.akiva.adam.finalproject.activities.MyActivity.USERS;
import static com.akiva.adam.finalproject.activities.MyActivity.VIEWED;


/**
 * A class used to build a service which displays a notification
 * when a new image is added to the database for the current logged in user
 */
public class NotificationService extends Service {

    // The database instance
    @Inject
    public Database database;
    private IDatabase mDatabase;

    // The current logged in user instance
    @Inject
    public MyUser user;
    private IUser mUser;

    //The shared preferences instance
    @Inject
    public MySharedPreferences sharedPreferences;
    private ISharedPreferences mSharedPreferences;

    private NotificationManagerCompat notificationManagerCompat;

    private ReentrantLock notificationsLock = new ReentrantLock();
    private volatile Boolean notifications;

    private ValueEventListener userListener;
    private ValueEventListener imagesListener;

    // Static variable used for logging
    private static final String TAG = NotificationService.class.getName();

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);
        mDatabase = database;
        mUser = user;
        mSharedPreferences = sharedPreferences;

        // After reboot if the user is not logged in stop the service
        if (mUser == null || mUser.getUid() == null || mUser.getUid().isEmpty() || mUser.getEmail() == null || mUser.getEmail()
                .isEmpty()) {
            stopSelf();
        }

        createNotificationChannel();
        notificationManagerCompat = NotificationManagerCompat.from(NotificationService.this);
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                new Thread(() -> {
                    notificationsLock.lock();
                    if (dataSnapshot.child(LOGGED_IN).exists()) {
                        Boolean loggedIn = dataSnapshot.child(LOGGED_IN).getValue(Boolean.class);
                        if (loggedIn != null && !loggedIn)
                            NotificationService.this.stopSelf();
                    } else {
                        NotificationService.this.stopSelf();
                    }
                    if (dataSnapshot.child(SETTINGS + "/" + NOTIFICATIONS).exists()) {
                        notifications = dataSnapshot.child(SETTINGS + "/" + NOTIFICATIONS).getValue(Boolean.class);
                        if (notifications != null && !notifications) {
                            notificationManagerCompat.cancel(NOTIFICATION_ID);
                        }
                    }
                    notificationsLock.unlock();
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, String.format("%s:%s", TAG, databaseError.getMessage()));
            }
        };
        imagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                new Thread(() -> {
                    notificationsLock.lock();
                    if ((!mSharedPreferences.readFromStorage(ImageListActivity.class.getName(), Boolean.class)) &&
                            (notifications != null && notifications)) {
                        int postsNotViewed = 0;
                        for (DataSnapshot image : dataSnapshot.getChildren()) {
                            if (image.child(VIEWED).exists()) {
                                Boolean viewed = image.child(VIEWED).getValue(Boolean.class);
                                if (viewed != null && !viewed) {
                                    postsNotViewed++;
                                }
                            }
                        }
                        if (postsNotViewed == 0) {
                            notificationManagerCompat.cancel(NOTIFICATION_ID);
                        } else if (postsNotViewed >= 1) {
                            Intent intent = new Intent(NotificationService.this, MainActivity.class);
                            notificationManagerCompat.notify(NOTIFICATION_ID, createNotification(R.drawable
                                    .ic_notification_alert, getString(R.string.newImageTitle), getString(R.string
                                    .newImageText, postsNotViewed), true, createIntentPath(intent, null)).build());
                        }
                    }
                    notificationsLock.unlock();
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, String.format("%s:%s", TAG, databaseError.getMessage()));
            }
        };

        try {
            mDatabase.getDatabaseService().getReference().child(USERS + "/" + mUser.getUid()).addValueEventListener
                    (userListener);
            mDatabase.getDatabaseService().getReference().child(IMAGES + "/" + mUser.getUid()).addValueEventListener
                    (imagesListener);
        } catch (ConnectException e) {
            MyActivity.displayError(TAG, e, null, null, null);
        }

    }

    /**
     * Used only for phones above version 26
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.channelName),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(getString(R.string.channelDesc));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context
                    .NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * A method which creates the notification
     *
     * @param icon          Integer indicating the icon for the notification
     * @param title         String containing the title of the notification
     * @param text          String containing the text of the notification
     * @param cancelOnClick Boolean indicating whether the notification
     *                      should be destroyed after being clicked
     * @param pendingIntent Intent which indicates which activity to open
     *                      after clicking the notification
     * @return Notification builder with the parameters set above
     */
    private NotificationCompat.Builder createNotification(int icon, String title, String text, boolean cancelOnClick,
                                                          PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(NotificationService.this, CHANNEL_ID).setSmallIcon(icon).setPriority
                (NotificationCompat.PRIORITY_HIGH).setContentTitle(title).setContentText(text).setStyle(new
                NotificationCompat.BigTextStyle().bigText(text)).setAutoCancel(cancelOnClick).setContentIntent
                (pendingIntent);
    }

    /**
     * A method which allows to add a bundle to be sent to the given activity
     *
     * @param intent Intent containing the activity which you want to start
     * @param bundle Nullable bundle containing additional parameters to send
     * @return Pending intent with the which activity to start and a Bundle if given
     */
    private PendingIntent createIntentPath(Intent intent, @Nullable Bundle bundle) {
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotificationService.this);
        stackBuilder.addNextIntentWithParentStack(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
