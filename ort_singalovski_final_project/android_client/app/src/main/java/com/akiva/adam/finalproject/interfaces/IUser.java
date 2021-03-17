package com.akiva.adam.finalproject.interfaces;

import com.akiva.adam.finalproject.classes.Settings;

import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

public interface IUser {

    String getUid();

    void setFirstName(String firstName) throws ConnectException;

    String getFirstName();

    void setLastName(String lastName) throws ConnectException;

    String getLastName();

    void setEmail(String email) throws ConnectException;

    String getEmail();

    void setPassword(String password) throws ConnectException;

    String getPassword();

    Settings getSettings();

    void setFiles() throws ConnectException;

    LinkedHashMap<String, IFile> getFiles();

    void deleteFile(IFile file) throws ConnectException;

    void requestLogout() throws ConnectException;

    void writeUserCredentialsToDatabase(CharSequence[] userInfo) throws ConnectException;

    ReentrantLock getFileListLock();

    Boolean getValueChanged();

    void setValueChanged(Boolean value);

    void setDeleteSuccessful(Boolean value);

    Boolean getDeleteSuccessful();
}
