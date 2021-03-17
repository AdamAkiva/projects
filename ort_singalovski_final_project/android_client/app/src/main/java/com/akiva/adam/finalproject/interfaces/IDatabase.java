package com.akiva.adam.finalproject.interfaces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.net.ConnectException;

public interface IDatabase {

    FirebaseAuth getAuthService() throws ConnectException;

    FirebaseDatabase getDatabaseService() throws ConnectException;

    FirebaseStorage getStorageService() throws ConnectException;

    boolean checkIfInternetConnectionIsAvailable();
}
