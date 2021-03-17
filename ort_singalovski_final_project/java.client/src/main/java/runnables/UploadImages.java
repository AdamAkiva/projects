package runnables;

import classes.Container;
import classes.Image;
import interfaces.IImage;
import interfaces.IUser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static classes.TrackingProcess.*;

/**
 * A class which receives a path to an image folder of files that
 * was not checked and uploaded for some reason. The class list the images and
 * sends them to the server to be checked for any sexual content,
 * if there is upload it to the database to the attributed user, if not delete it
 */
public class UploadImages implements Runnable {

    private String path;
    private IUser user;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(UploadImages.class.getName());

    /**
     * @param path String which hold the path images location
     * @param user A user object to attribute the images to
     */
    public UploadImages(String path, IUser user) {
        this.path = path;
        this.user = user;
    }

    @Override
    public void run() {
        try {
            if (new File(path).exists()) {
                String[] images = new File(path).list();
                if (images != null) {
                    for (String fileName : images) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        BufferedImage image = ImageIO.read(new File(path + fileName));
                        ImageIO.write(image, EXTENSION, bos);
                        String viewDate = fileName.split(FILE_NAME_SEPARATOR)[0].replace(FILE_NAME_SUB_SEPARATOR, DATE_SEPARATOR);
                        String viewTime = fileName.split(FILE_NAME_SEPARATOR)[1].substring(0, fileName.split(FILE_NAME_SEPARATOR)[1].length() - EXTENSION.length() - 1).replace(FILE_NAME_SUB_SEPARATOR, TIME_SEPARATOR);
                        try {
                            new Container<IImage>(Container.CHECK_AND_UPLOAD_IMAGE_REQUEST, new Image(user, String.format("%s", fileName), viewDate, viewTime, bos.toByteArray()));
                        } catch (IOException | InterruptedException | ExecutionException | ClassCastException e) {
                            logger.info(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
