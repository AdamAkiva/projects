package com.akiva.adam.finalproject.classes;

import android.support.annotation.NonNull;
import android.util.Log;

import com.akiva.adam.finalproject.activities.MyActivity;
import com.akiva.adam.finalproject.interfaces.IFile;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import static com.akiva.adam.finalproject.activities.MyActivity.EMAIL;
import static com.akiva.adam.finalproject.activities.MyActivity.FILE_NAME;
import static com.akiva.adam.finalproject.activities.MyActivity.FIRST_NAME;
import static com.akiva.adam.finalproject.activities.MyActivity.IMAGES;
import static com.akiva.adam.finalproject.activities.MyActivity.LAST_NAME;
import static com.akiva.adam.finalproject.activities.MyActivity.LOGGED_IN;
import static com.akiva.adam.finalproject.activities.MyActivity.PASSWORD;
import static com.akiva.adam.finalproject.activities.MyActivity.SETTINGS;
import static com.akiva.adam.finalproject.activities.MyActivity.UNDEFINED;
import static com.akiva.adam.finalproject.activities.MyActivity.USERS;
import static com.akiva.adam.finalproject.activities.MyActivity.VIEWED;
import static com.akiva.adam.finalproject.activities.MyActivity.VIEW_DATE;
import static com.akiva.adam.finalproject.activities.MyActivity.VIEW_TIME;

public class MyUser implements IUser, Serializable {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private volatile LinkedHashMap<String, IFile> files;
    private Settings settings;

    private ValueEventListener getFiles;

    private volatile ReentrantLock fileListLock = new ReentrantLock();
    private volatile Boolean valueChanged = false;
    private volatile Boolean deleteSuccessful = null;

    @Inject
    public Database database;

    public static final String TAG = MyUser.class.getName();

    @Inject
    public MyUser(Database database) {
        try {
            if (database.getAuthService().getCurrentUser() != null) {
                uid = database.getAuthService().getCurrentUser().getUid();
                email = database.getAuthService().getCurrentUser().getEmail();
                database.getDatabaseService().getReference().child(USERS + "/" + uid + "/" + LOGGED_IN).setValue(true);
                this.database = database;
                files = new LinkedHashMap<String, IFile>();
            }
            getPersonalInformationFromDatabase();
        } catch (ConnectException e) {
            MyActivity.displayError(TAG, e, null, null, null);
        }
    }

    @Override
    public String getUid() {
        if (uid != null) {
            return uid;
        }
        return null;
    }

    @Override
    public void setEmail(String email) throws ConnectException {
        this.email = email;
        database.getDatabaseService().getReference().child(USERS + "/" + uid + "/" + EMAIL).setValue(email);
    }

    @Override
    public String getEmail() {
        if (email != null) {
            return email;
        }
        return null;
    }

    @Override
    public void setFirstName(String firstName) throws ConnectException {
        this.firstName = firstName;
        database.getDatabaseService().getReference().child(USERS + "/" + uid + "/" + FIRST_NAME).setValue(firstName);
    }

    @Override
    public String getFirstName() {
        if (firstName != null) {
            return firstName;
        }
        return null;
    }

    @Override
    public void setLastName(String lastName) throws ConnectException {
        this.lastName = lastName;
        database.getDatabaseService().getReference().child(USERS + "/" + uid + "/" + LAST_NAME).setValue(lastName);
    }

    @Override
    public String getLastName() {
        if (lastName != null) {
            return lastName;
        }
        return null;
    }

    @Override
    public void setPassword(String password) throws ConnectException {
        this.password = password;
        database.getDatabaseService().getReference().child(USERS + "/" + uid + "/" + PASSWORD).setValue(password);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Settings getSettings() {
        if (settings != null) {
            return settings;
        }
        return null;
    }


    public void setDeleteSuccessful(Boolean deleteSuccessful) {
        this.deleteSuccessful = deleteSuccessful;
    }

    public Boolean getDeleteSuccessful() {
        return deleteSuccessful;
    }

    private void getPersonalInformationFromDatabase() throws ConnectException {
        FirebaseUser currentUser = database.getAuthService().getCurrentUser();
        if (currentUser != null) {
            email = currentUser.getEmail();
        }
        database.getDatabaseService().getReference().child(USERS + "/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(FIRST_NAME).exists()) {
                    firstName = dataSnapshot.child(FIRST_NAME).getValue(String.class);
                } else {
                    firstName = UNDEFINED;
                }
                if (dataSnapshot.child(LAST_NAME).exists()) {
                    lastName = dataSnapshot.child(LAST_NAME).getValue(String.class);
                } else {
                    lastName = UNDEFINED;
                }
                if (dataSnapshot.child(PASSWORD).exists()) {
                    password = dataSnapshot.child(PASSWORD).getValue(String.class);
                } else {
                    password = UNDEFINED;
                }
                if (dataSnapshot.child(SETTINGS).exists()) {
                    settings = dataSnapshot.child(SETTINGS).getValue(Settings.class);
                    if (!dataSnapshot.child(SETTINGS).exists()) {
                        settings.setNotifications(true);
                    }
                } else {
                    settings = new Settings(true);
                    try {
                        database.getDatabaseService().getReference(USERS + "/" + uid + "/" + SETTINGS).setValue
                                (settings);
                    } catch (ConnectException e) {
                        MyActivity.displayError(TAG, e, null, null, null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, String.format("%s:%s", TAG, databaseError.getMessage()));
            }
        });
    }

    @Override
    public void setFiles() throws ConnectException {
        fileListLock.lock();
        getFiles = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (MyUser.this.files.isEmpty() || MyUser.this.files.size() < dataSnapshot.getChildrenCount()) {
                    for (DataSnapshot image : dataSnapshot.getChildren()) {
                        String fileName = null;
                        String viewDate = null;
                        String viewTime = null;
                        Boolean viewed = null;
                        if (image.child(FILE_NAME).exists()) {
                            fileName = image.child(FILE_NAME).getValue(String.class);
                        } else {
                            Log.d(TAG, TAG + ": File name is null");
                        }
                        if (image.child(VIEW_DATE).exists()) {
                            viewDate = image.child(VIEW_DATE).getValue(String.class);
                        } else {
                            Log.d(TAG, TAG + ": View date is null");
                        }
                        if (image.child(VIEW_TIME).exists()) {
                            viewTime = image.child(VIEW_TIME).getValue(String.class);
                        } else {
                            Log.d(TAG, TAG + ": View time is null");
                        }
                        if (image.child(VIEWED).exists()) {
                            viewed = image.child(VIEWED).getValue(Boolean.class);
                        } else {
                            Log.d(TAG, TAG + ": Viewed is null");
                        }
                        if (fileName != null && viewDate != null && viewTime != null && viewed != null) {
                            MyUser.this.files.put(image.getKey(), new Image(database, MyUser.this,
                                    "gs://final-project-c56f3.appspot.com/" + MyActivity.IMAGES + "/" + uid + "/" +
                                            fileName, image.getKey(), fileName, viewDate, viewTime, viewed));
                            valueChanged = true;
                        }
                    }
                }
                    if (fileListLock.isLocked()) {
                        fileListLock.unlock();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, String.format("%s:%s", TAG, databaseError.getMessage()));
            }
        };
        database.getDatabaseService().getReference().child(IMAGES + "/" + uid).addValueEventListener(getFiles);
    }

    @Override
    public synchronized ReentrantLock getFileListLock() {
        return fileListLock;
    }

    @Override
    public synchronized Boolean getValueChanged() {
        return valueChanged;
    }

    @Override
    public synchronized void setValueChanged(Boolean value) {
        this.valueChanged = value;
    }

    @Override
    public LinkedHashMap<String, IFile> getFiles() {
        return files;
    }

    @Override
    public void deleteFile(final IFile file) throws ConnectException {
        database.getDatabaseService().getReference().child(IMAGES + "/" + uid + "/" + file.getKey()).removeValue()
                .addOnCompleteListener((@NonNull Task<Void> task) -> {
            if (task.isSuccessful()) {
                try {
                    database.getStorageService().getReference().child(IMAGES + "/" + uid + "/" + file.getFileName())
                            .delete();
                } catch (ConnectException e) {
                    MyActivity.displayError(TAG, e, null, null, null);
                }
                MyUser.this.files.remove(file.getKey());
                deleteSuccessful = true;
            } else {
                deleteSuccessful = false;
            }
        });
    }

    @Override
    public void requestLogout() throws ConnectException {
        database.getDatabaseService().getReference().child(USERS + "/" + uid + "/" + LOGGED_IN).setValue(false);
        database.getAuthService().signOut();
    }

    @Override
    public void writeUserCredentialsToDatabase(CharSequence[] userInfo) throws ConnectException {
        DatabaseReference dbRef = database.getDatabaseService().getReference().child(USERS + "/" + uid);
        dbRef.child(FIRST_NAME).setValue(userInfo[0]);
        dbRef.child(LAST_NAME).setValue(userInfo[1]);
        dbRef.child(EMAIL).setValue(userInfo[2]);
        dbRef.child(PASSWORD).setValue(userInfo[3]);
        dbRef.child(LOGGED_IN).setValue(false);
    }
}
