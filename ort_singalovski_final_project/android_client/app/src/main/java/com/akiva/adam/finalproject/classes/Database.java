package com.akiva.adam.finalproject.classes;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.net.ConnectException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A singleton class used to allow different function
 * from the database
 */
@Singleton
public class Database implements IDatabase, Serializable {

    private final Context context;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private final String internetConnectionErrorMessage = "There is no internet connection";

    /**
     * A constructor for the Database class
     *
     * @param context Current Activity context
     */
    @Inject
    public Database(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
        this.mStorage = FirebaseStorage.getInstance();
    }

    /**
     * A method used to get the database's authentication service
     *
     * @return FirebaseAuth object
     * @throws ConnectException Thrown if there is no internet connection
     */
    @Override
    public FirebaseAuth getAuthService() throws ConnectException {
        if (checkIfInternetConnectionIsAvailable()) {
            return mAuth;
        }
        throw new ConnectException(internetConnectionErrorMessage);
    }

    /**
     * A method used to get the database's database service
     *
     * @return FirebaseDatabase object
     * @throws ConnectException Thrown if there is no internet connection
     */
    @Override
    public FirebaseDatabase getDatabaseService() throws ConnectException {
        if (checkIfInternetConnectionIsAvailable()) {
            return mDatabase;
        }
        throw new ConnectException(internetConnectionErrorMessage);
    }

    /**
     * A method used to get the database's storage service
     *
     * @return FirebaseStorage object
     * @throws ConnectException Thrown if there is no internet connection
     */
    @Override
    public FirebaseStorage getStorageService() throws ConnectException {
        if (checkIfInternetConnectionIsAvailable()) {
            return mStorage;
        }
        throw new ConnectException(internetConnectionErrorMessage);
    }

    /**
     * A method used to check if the phone is connected to the internet
     * either by WIFI or by Mobile data
     *
     * @return Boolean value indicating if the phone is connected to the internet
     */
    @Override
    public boolean checkIfInternetConnectionIsAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager
                        .TYPE_MOBILE) {
                    return true;
                }
            }
        }
        return false;
    }
}
