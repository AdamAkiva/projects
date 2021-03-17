package interfaces;

import com.google.cloud.storage.Bucket;

public interface IImage {

    boolean checkForSexualContent(IContainer container);

    boolean validateParams(IContainer container);

    IUser getUser();

    String getFileName();

    String getViewDate();

    String getViewTime();

    Boolean getViewed();

    byte[] getFile();

    Bucket getBucket();

    boolean uploadFileToDatabase();
}
