package classes;

import interfaces.IContainer;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * A class which contains a sort of container to hold a multiple range of objects that can be sent
 * to the server if they implement the Serializable interface
 *
 * @param <T> Generic which holds the class that is sent over the socket as long
 *            as it implements the Serializable interface
 */
public class Container<T> implements IContainer, Serializable {

    /**
     * A variable which hold the request number:
     * 10 - Encryption
     * 11 - Decryption
     * 20 - User creation
     * 30 - Check image for sexual content and upload
     */
    private int request;

    // Boolean value that indicate whether the request was successful or not
    private boolean result;

    // String value that hold the log message from the request result
    private String logMessage;

    // String value that hold the user message from the request result
    private String userMessage;

    //Generic that holds the associated class per request
    private T t;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Container.class.getName());

    // Static integers that indicate the request type
    public static final int ENCRYPTION_REQUEST = 10;
    public static final int DECRYPTION_REQUEST = 11;
    public static final int CREATE_USER_REQUEST = 20;
    public static final int CHECK_AND_UPLOAD_IMAGE_REQUEST = 30;

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    private static final long serialVersionUID = 1574258964123587456L;


    @Override
    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    @Override
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    @Override
    public int getRequest() {
        return request;
    }

    @Override
    public boolean getResult() {
        return result;
    }

    @Override
    public String getLogMessage() {
        return logMessage;
    }

    @Override
    public String getUserMessage() {
        return userMessage;
    }

    @Override
    public T getObject() {
        return t;
    }

    @Override
    public String toString() throws NullPointerException {
        String temp;
        if (result) {
            temp = "Success";
        } else {
            temp = "Failed";
        }
        return String.format("Request: %d\nSucceed: %s\nSent object of type: %s\nLog message: %s\nUser message: %s", request, temp, t.getClass().getName(), logMessage, userMessage);
    }
}
