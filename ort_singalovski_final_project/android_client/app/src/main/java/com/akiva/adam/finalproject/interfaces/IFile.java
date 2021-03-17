package com.akiva.adam.finalproject.interfaces;

import java.net.ConnectException;

public interface IFile {

    String getKey();

    String getFileName();

    String getViewDate();

    String getViewTime();

    void setViewed() throws ConnectException;

    Boolean getViewed();

    void setFileBytes(byte[] fileBytes);

    byte[] getFileBytes();

    Boolean getDoneDownloading();

    void getFileFromDatabase() throws ConnectException;

}
