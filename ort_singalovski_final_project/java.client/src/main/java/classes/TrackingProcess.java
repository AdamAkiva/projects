package classes;

import interfaces.IImage;
import interfaces.IUser;
import runnables.UploadImages;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * A class which holds the main tracking thread on the user's computer
 * the run method capture the user's screen every 30 seconds to check
 * for any sexual content
 */
public class TrackingProcess extends Thread {

    // Variable to hold the current user
    private IUser user;

    // Set the logger for the this file
    private Logger logger = Logger.getLogger(TrackingProcess.class.getName());

    // String which holds the captured images save location
    public static final String IMAGE_SAVE_LOCATION = "C:\\Users\\ASUS\\Desktop\\Projects\\finalproject-master\\java.client\\images\\";

    // String which holds the images extension (jpg in this case)
    public static final String EXTENSION = "jpg";

    // Static Strings the indicate the separators for naming the file and for creating the Image object
    public static final String FILE_NAME_SEPARATOR = "-";
    public static final String FILE_NAME_SUB_SEPARATOR = "_";
    public static final String DATE_SEPARATOR = ".";
    public static final String TIME_SEPARATOR = ":";

    private static final String CREATED_NEW_IMAGE_FOLDER = "Successfully created new Image folder";
    private static final String ERROR_CREATING_FILE = "File creation failed";

    private static final int TIME_BETWEEN_IMAGE_CAPTURES = 45000;

    // need to deliver here some ui element as well that I can print the result on
    public TrackingProcess(IUser user) {
        this.user = user;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Checks if there are any images from the last time the program was running that
                // that were not checked for sexual content for some reason and if there are any
                // run the UploadImages runnable
                if (new File(IMAGE_SAVE_LOCATION).exists()) {
                    String[] images = new File(IMAGE_SAVE_LOCATION).list();
                    if (images != null && images.length > 0) {
                        new UploadImages(IMAGE_SAVE_LOCATION, user).run();
                    }
                } else {
                    if (new File(IMAGE_SAVE_LOCATION).mkdirs()) {
                        logger.info(CREATED_NEW_IMAGE_FOLDER);
                    }
                }
                sleep(TIME_BETWEEN_IMAGE_CAPTURES);
                buildImages(setScreens());
            } catch (AWTException | IOException | InterruptedException | ExecutionException | NullPointerException e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    /**
     * A method which is used mainly to check if the user has multiple screens and include them in the screen capture
     *
     * @return A rectangle object at the size of the entire monitor setup
     */
    private ArrayList<Rectangle> setScreens() {
        ArrayList<Rectangle> recs = new ArrayList<Rectangle>();
        int numOfScreens = 0;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        for (GraphicsDevice gd : gds) {
            GraphicsConfiguration[] gcs = gd.getConfigurations();
            Rectangle rec = new Rectangle();
            for (GraphicsConfiguration gc : gcs) {
                rec = rec.union(gc.getBounds());
            }
            if (numOfScreens >= 1) {
                for (Rectangle temp : recs) {
                    rec.x += temp.getWidth();
                    rec.width -= temp.getWidth();
                }
            }
            recs.add(numOfScreens++, rec);
        }
        return recs;
    }

    private void buildImages(ArrayList<Rectangle> recs) throws InterruptedException, AWTException, IOException, ExecutionException {
        for (Rectangle rec : recs) {
            sleep(2000);
            String fileName = getCurrentTimeAndDate();
            String path = String.format("%s%s.%s", IMAGE_SAVE_LOCATION, fileName, EXTENSION);
            File file = new File(path);
            BufferedImage capturedImage = new Robot().createScreenCapture(rec);
            if (writeToFile(capturedImage, file)) {
                Container container = new Container<IImage>(Container.CHECK_AND_UPLOAD_IMAGE_REQUEST, createImageObject(capturedImage, fileName));
                logger.info(container.toString());
            } else {
                logger.info(ERROR_CREATING_FILE);
            }
        }
    }

    /**
     * A method to get the current computer set time and date as a String variable
     *
     * @return String which contains the current time and date at the format of "dd_MM_yyyy-HH_mm_ss"
     */
    private String getCurrentTimeAndDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss");
        return dateFormat.format(new Date());
    }

    /**
     * A method which turns the BufferedImage object to a ByteArrayOutputStream to be sent to the server
     *
     * @param capturedImage BufferedImage object which contains the captured image
     * @param file          Image location on the computer as a file object
     * @return boolean value which indicates whether the process was successful
     */
    private boolean writeToFile(BufferedImage capturedImage, File file) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(capturedImage, EXTENSION, baos);
            try (OutputStream ops = new FileOutputStream(file)) {
                baos.writeTo(ops);
            }
            return true;
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method the turns the BufferedImage object to a Image object
     *
     * @param capturedImage BufferedImage object which contains the captured image
     * @param fileName      String which contains the file name
     * @return IImage interface for a Image object
     * @throws IOException if the process failed
     */
    private IImage createImageObject(BufferedImage capturedImage, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(capturedImage, EXTENSION, baos);
        return new Image(user, String.format("%s.%s", fileName, EXTENSION), fileName.split(FILE_NAME_SEPARATOR)[0].replace(FILE_NAME_SUB_SEPARATOR, DATE_SEPARATOR), fileName.split(FILE_NAME_SEPARATOR)[1].replace(FILE_NAME_SUB_SEPARATOR, TIME_SEPARATOR), baos.toByteArray());
    }
}
