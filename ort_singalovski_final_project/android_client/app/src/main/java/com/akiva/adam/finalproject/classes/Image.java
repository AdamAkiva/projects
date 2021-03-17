package com.akiva.adam.finalproject.classes;

import android.support.annotation.NonNull;

import com.akiva.adam.finalproject.activities.MyActivity;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.interfaces.IFile;

import java.io.Serializable;
import java.net.ConnectException;

import static com.akiva.adam.finalproject.activities.MyActivity.IMAGES;
import static com.akiva.adam.finalproject.activities.MyActivity.VIEWED;

/**
 * A class used to represent an image from the database
 */
public class Image implements IFile, Serializable {
    private String key;
    private String downloadPath;
    private byte[] fileBytes;
    private String fileName;
    private String viewDate;
    private String viewTime;
    private Boolean viewed;

    public IDatabase database;
    public MyUser user;

    private boolean doneDownloading = false;

    public static final String TAG = Image.class.getName();

    /**
     * A constructor for the Image class
     *
     * @param database     Database instance
     * @param user         User instance
     * @param downloadPath String where the image should be downloaded to
     * @param key          String unique image key
     * @param fileName     String containing the image name
     * @param viewDate     String containing the image view date
     * @param viewTime     String containing the image view time
     * @param viewed       Boolean indicating whether the image was viewed
     */
    public Image(Database database, MyUser user, String downloadPath, String key, String fileName, String viewDate,
                 String viewTime, boolean viewed) {
        this.key = key;
        this.downloadPath = downloadPath;
        this.fileName = fileName;
        this.viewDate = viewDate;
        this.viewTime = viewTime;
        this.viewed = viewed;
        this.database = database;
        this.user = user;

    }


    /**
     * A default constructor needed for firebase functionality
     */
    Image() {
    }

    @Override
    public String getKey() {
        if (key != null) {
            return key;
        }
        return null;
    }

    @Override
    public String getFileName() {
        if (fileName != null) {
            return fileName;
        }
        return null;
    }

    @Override
    public String getViewDate() {
        if (viewDate != null) {
            return viewDate;
        }
        return null;
    }

    @Override
    public String getViewTime() {
        if (viewTime != null) {
            return viewTime;
        }
        return null;
    }

    @Override
    public void setViewed() throws ConnectException {
        database.getDatabaseService().getReference().child(IMAGES + "/" + user.getUid() + "/" + key + "/" + VIEWED)
                .setValue(true);
    }

    @Override
    public Boolean getViewed() {
        if (viewed != null) {
            return viewed;
        }
        return null;
    }

    @Override
    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    @Override
    public byte[] getFileBytes() {
        if (fileBytes != null) {
            return fileBytes;
        }
        return null;
    }

    public Boolean getDoneDownloading() {
        return doneDownloading;
    }

    @Override
    public void getFileFromDatabase() throws ConnectException {

        database.getStorageService().getReferenceFromUrl(downloadPath).getBytes(MyActivity.TWO_MEGABYTES)
                .addOnSuccessListener((byte[] bytes) -> {
            setFileBytes(bytes);
            doneDownloading = true;
        }).addOnFailureListener((@NonNull Exception e) -> doneDownloading = false);
    }

    /**
     * A method checking whether two images are equal by
     * comparing the fields
     *
     * @param object Image object
     * @return Boolean value indicating whether the images are equal
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof Image || object instanceof IFile) {
            IFile temp = (IFile) object;
            if (fileName.equals(temp.getFileName())) {
                if (viewDate.equals(temp.getViewDate())) {
                    return viewTime.equals(temp.getViewTime());
                }
            }
        }
        return false;
    }
}
