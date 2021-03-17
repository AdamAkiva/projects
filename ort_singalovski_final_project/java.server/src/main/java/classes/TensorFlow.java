package classes;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.nio.FloatBuffer;
import java.util.logging.Logger;

/**
 * A class to load the built in python image recognition module
 * to be used to check whether an image contains sexual content
 */
public class TensorFlow {

    // Static Integers which holds the resized width and height
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(TensorFlow.class.getName());

    // load cv2 external library
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * A method which predict whether the image has sexual content
     *
     * @param bytes Byte array with the image data
     * @return Boolean value which indicates whether the image has sexual content or not
     * @throws NullPointerException Thrown if the module was not found
     */
    public static boolean predict(byte[] bytes) throws NullPointerException {
        SavedModelBundle savedModelBundle = SavedModelBundle.load("C:\\Users\\ASUS\\Desktop\\Projects\\finalproject-master\\tensorflow_model\\export\\1546212168", "serve");

        // create a new shape which holds the bytes of the image data
        long[] shape = new long[]{1, HEIGHT, WIDTH, 1};

        // sends the newly shaped tensor to the module built in python to be assessed
        // and receive back a value of 0-1 of how close is the image to have sexual content
        try (Tensor t = Tensor.create(shape, FloatBuffer.wrap(imageToBytes(bytes)))) {
            logger.info(t.toString());
            Tensor result = savedModelBundle.session().runner().feed("x", t).fetch("softmax_tensor").run().get(0);
            logger.info(result.toString());
            float[][] vector = new float[1][2];
            result.copyTo(vector);
            logger.info(String.format("Nude:%s, Not nude:%s", vector[0][1], vector[0][0]));
            // If the image has more than 50% chance to have sexual content
            // return true, otherwise return false
            if (vector[0][1] > 0.7) {
                return true;
            }
            return false;
        }
    }

    /**
     * A method which create a grey-scaled resized 32x32 cv2 Matrix with the image data
     * as bytes values (1-255), the result looks like
     * [ 5, 6, 128, 0 ...] (length of 32x32x1]
     *
     * @param bytes Byte array which holds the image's bytes values
     * @return Float array which holds the resized image byte values
     */
    private static float[] imageToBytes(byte[] bytes) {
        Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat resizeImage = new Mat();
        Imgproc.resize(image, resizeImage, new Size(WIDTH, HEIGHT));
        byte[] temp = new byte[(int) resizeImage.total() * resizeImage.channels()];
        resizeImage.get(0, 0, temp);
        String str = "";
        float[] result = new float[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = (float) ((temp[i] & 0XFF) / 255.0);
        }
        logger.info(str);
        return result;
    }
}
