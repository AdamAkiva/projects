package runnables;

import classes.Image;
import com.google.firebase.database.DataSnapshot;
import interfaces.IImage;
import server_related.FirebaseServer;

import java.util.logging.Logger;

import static server_related.FirebaseServer.IMAGE_DATABASE_LOCATION;
import static server_related.FirebaseServer.IMAGE_STORAGE_LOCATION;

/**
 * A class which checks if there are any duplicate images in a specific user's storage and database,
 * if there were any remove them
 */
public class CheckForDuplicateEntry implements Runnable {

    private final FirebaseServer server = FirebaseServer.getInstance();

    private final IImage image;
    private final String uid;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(CheckForDuplicateEntry.class.getName());

    private static final String NO_DUPLICATE_FILE_WAS_FOUND = "No duplicate file was found";
    private static final String DUPLICATE_FILE_FOUND = "Duplicate file was found and deleted";
    private static final String DUPLICATE_ENTRY_FOUND = "Duplicate entry was found and deleted";

    /**
     * A constructor for CheckForDuplicateFile class
     *
     * @param image Image object to be checked
     * @param uid   String which holds the the user's specific UID
     */
    public CheckForDuplicateEntry(IImage image, String uid) {
        this.image = image;
        this.uid = uid;
    }

    /**
     * A method which checks for a duplicate images in the same user's own storage and database,
     * if there were any remove them
     */
    @Override
    public void run() {
        DataSnapshot userSpecificImages = server.databaseSnapshot().child(IMAGE_DATABASE_LOCATION + uid);
        for (DataSnapshot temp : userSpecificImages.getChildren()) {
            IImage imageToCheck = temp.getValue(Image.class);
            if (image.getFileName().equals(imageToCheck.getFileName())) {
                if (image.getViewDate().equals(imageToCheck.getViewDate())) {
                    if (image.getViewTime().equals(imageToCheck.getViewTime()))
                        logger.info(DUPLICATE_ENTRY_FOUND);
                    server.databaseService().getReference().child(IMAGE_DATABASE_LOCATION + uid + temp.getKey()).removeValueAsync();
                    if (server.storageService().get(String.format("%s%s/%s", IMAGE_STORAGE_LOCATION, uid, image.getFileName())) != null) {
                        logger.info(DUPLICATE_FILE_FOUND);
                        server.storageService().get(String.format("%s%s/%s", IMAGE_STORAGE_LOCATION, uid, image.getFileName())).delete();
                    } else {
                        logger.info(NO_DUPLICATE_FILE_WAS_FOUND);
                    }
                }
            }
        }
    }
}
