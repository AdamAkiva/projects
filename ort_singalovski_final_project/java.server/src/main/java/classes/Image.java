package classes;

import com.google.cloud.storage.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.Exclude;
import interfaces.IContainer;
import interfaces.IImage;
import interfaces.IToken;
import interfaces.IUser;
import runnables.CheckForDuplicateEntry;
import server_related.FirebaseServer;

import java.io.Serializable;
import java.util.logging.Logger;

import static server_related.FirebaseServer.IMAGE_DATABASE_LOCATION;
import static server_related.FirebaseServer.IMAGE_STORAGE_LOCATION;

/**
 * An Image object which contains the image values that are needed for a server check
 * and for a database upload
 */
public class Image implements Serializable, IImage {

    // Exclude = ignore when uploading class to database
    @Exclude
    private static final FirebaseServer server = FirebaseServer.getInstance();

    @Exclude
    private User user;

    private String fileName;
    private String viewDate;
    private String viewTime;
    private Boolean viewed;

    @Exclude
    private byte[] file;

    // transient = not passed via socket
    @Exclude
    transient private Bucket bucket;

    // Set the logger for the this file
    @Exclude
    private static final Logger logger = Logger.getLogger(Image.class.getName());

    @Exclude
    private static final String FILE_NAME_REGEX = "(0[1-9]|[1-2][0-9]|3[0-2])_(0[1-9]|1[0-2])_(20[1-5][0-9])-(0[1-9]|1[0-9]|2[0-3])_(0[1-9]|[1-5][0-9])_(0[1-9]|[1-5][0-9]).jp(g|eg)";
    @Exclude
    private static final String VIEW_DATE_REGEX = "(0[1-9]|[1-2][0-9]|3[0-2])\\.(0[1-9]|1[0-2])\\.(20[1-5][0-9])";
    @Exclude
    private static final String VIEW_TIME_REGEX = "(0[1-9]|1[0-9]|2[0-3]):(0[1-9]|[1-5][0-9]):(0[1-9]|[1-5][0-9])";

    @Exclude
    private static final String FILE_DESC = "image/jpeg";
    @Exclude
    private static final String NO_SEXUAL_CONTENT = "No sexual content was found in the given image";
    @Exclude
    private static final String IMAGE_UPLOAD_SUCCESSFUL = "Image uploaded successfully";
    @Exclude
    private static final String IMAGE_UPLOAD_FAILED = "Image upload failed";
    @Exclude
    private static final String SMART_ASS = "Stop being a smart ass";
    @Exclude
    private static final String NAME_CHANGED = "File name was changed by user";
    @Exclude
    private static final String DATE_CHANGED = "File date was changed by user";
    @Exclude
    private static final String TIME_CHANGED = "File time was changed by user";

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    @Exclude
    private static final long serialVersionUID = 4569852587412589654L;

    /**
     * A method which checks the given image inside the user's sent container
     * for any sexual content and modify the container accordingly
     *
     * @param container Container object sent by the client that contains an Image object
     * @return Boolean value which indicates whether sexual content was found or not
     * @throws NullPointerException Thrown if the TensorFlow module was not found
     */
    @Override
    public boolean checkForSexualContent(final IContainer container) throws NullPointerException {
        logger.info(String.format("Image name:%s", ((IImage) container.getObject()).getFileName()));
        if (TensorFlow.predict(((IImage) container.getObject()).getFile())) {
            return true;
        }
        container.setResult(true);
        container.setLogMessage(NO_SEXUAL_CONTENT);
        return false;
    }

    /**
     * A method which checks the given Image object for valid fields
     * using the stated above regexps and modifying the container accordingly
     *
     * @param container Container object sent by the client that contains an Image object
     * @return Boolean value which indicates whether the image was uploaded successfully or not
     */
    @Override
    public boolean validateParams(final IContainer container) {
        logger.info(container.getObject().toString());
        if (fileName.matches(FILE_NAME_REGEX)) {
            if (viewDate.matches(VIEW_DATE_REGEX)) {
                if (viewTime.matches(VIEW_TIME_REGEX)) {
                    if (uploadFileToDatabase()) {
                        container.setResult(true);
                        container.setLogMessage(IMAGE_UPLOAD_SUCCESSFUL);
                        return true;
                    } else {
                        container.setResult(false);
                        container.setLogMessage(IMAGE_UPLOAD_FAILED);
                        return false;
                    }
                }
                container.setResult(false);
                container.setUserMessage(SMART_ASS);
                container.setLogMessage(TIME_CHANGED);
                return false;
            }
            container.setResult(false);
            container.setUserMessage(SMART_ASS);
            container.setLogMessage(DATE_CHANGED);
            return false;
        }
        container.setResult(false);
        container.setUserMessage(SMART_ASS);
        container.setLogMessage(NAME_CHANGED);
        return false;
    }

    /**
     * A method which uploads the checked and verified image to the database under the indicated user
     * under a unique token as well as to the the indicated user's storage
     *
     * @return Boolean value whether the upload was successful or not
     */
    @Override
    public boolean uploadFileToDatabase() {
        IToken token = new Token(20);
        try {
            String uid = server.authService().getUserByEmail(user.getEmail()).getUid();
            new CheckForDuplicateEntry(this, uid).run();
            server.writeToDatabase(String.format("%s/%s/%s", IMAGE_DATABASE_LOCATION, uid, token.getToken()), this);
            server.writeToStorage(String.format("%s%s", IMAGE_STORAGE_LOCATION, uid), fileName, file, FILE_DESC);
            return true;
        } catch (FirebaseAuthException | StorageException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Exclude
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
    public Boolean getViewed() {
        return viewed;
    }

    @Exclude
    @Override
    public byte[] getFile() {
        return file;
    }

    @Exclude
    @Override
    public Bucket getBucket() {
        return bucket;
    }

    @Override
    public String toString() {
        return String.format("Image owned by %s %s:\nImage name: %s\nImage view time: %s\nImage view time: %s", user.getFirstName(), user.getLastName(), fileName, viewDate, viewTime);
    }
}