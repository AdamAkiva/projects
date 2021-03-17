package classes;

import interfaces.IImage;
import interfaces.IUser;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * An Image object which contains the image values that are needed for a server check
 * and for a database upload
 */
public class Image implements Serializable, IImage {

    // User object to hold the current logged in user
    private final IUser user;
    private final String fileName;
    private final String viewDate;
    private final String viewTime;
    private final Boolean viewed;
    private final byte[] file;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Image.class.getName());

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    private static final long serialVersionUID = 4569852587412589654L;

    /**
     * A constructor for the Image class
     *
     * @param user     User object for the current logged in user
     * @param fileName String which holds the the file name (should look like "dd_MM_yyyy-HH_mm_ss.jpg")
     * @param viewDate String which holds the view date of the file (should look like dd.MM.yyyy)
     * @param viewTime String which holds the view time of the file (should look like HH:mm:ss)
     * @param file     byte array with the image data
     */
    public Image(IUser user, String fileName, String viewDate, String viewTime, byte[] file) {
        this.user = user;
        this.fileName = fileName;
        this.viewDate = viewDate;
        this.viewTime = viewTime;
        this.viewed = false;
        this.file = file;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getViewDate() {
        return viewDate;
    }

    @Override
    public String getViewTime() {
        return viewTime;
    }

    @Override
    public String toString() {
        return String.format("Image owned by %s %s:\nImage name: %s\nImage view time: %s\nImage view time: %s", user.getFirstName(), user.getLastName(), fileName, viewDate, viewTime);
    }
}