package runnables;

import interfaces.IContainer;
import interfaces.IEncryption;
import interfaces.IImage;
import interfaces.IUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import static classes.Container.*;

/**
 * A class that handles the requests sent by the user's clients to the server
 */
public class ConnectionHandler extends Thread {

    // A socket variable for the server connection with a specific client
    private final Socket connection;

    // An objectInputStream to receive the container class from the user
    private ObjectInputStream inputStream;

    // An objectOutputStream to return the modified container class back to the user
    private ObjectOutputStream outputStream;

    // A container class which holds the received container from the user as well as
    // the modified container to return back to the user
    private IContainer container;

    private static final String REQUEST_NOT_FOUND_LOG_MESSAGE = "Request was not recognized";
    private static final String REQUEST_NOT_FOUND_USER_MESSAGE = "";
    private static final String ERROR_WITH_SERVER = "There was an error with the server please try again later";

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());

    /**
     * A constructor for the ConnectionHandler class
     *
     * @param connection A socket received by the Server class as ServerSocket.accept
     */
    public ConnectionHandler(Socket connection) {
        this.connection = connection;
    }

    /**
     * A method which receives a container object from the user and
     * take action by the type of class held inside the container
     */
    @Override
    public void run() {
        try {
            logger.info(String.format("Connected to %s via port %d started", connection.getInetAddress().toString(), connection.getLocalPort()));

            inputStream = new ObjectInputStream(connection.getInputStream());
            outputStream = new ObjectOutputStream(connection.getOutputStream());

            container = (IContainer) inputStream.readObject();

            // If the container contained object is an Encryption object
            // check if the request was to either encrypt or decrypt
            if (container.getObject() instanceof IEncryption) {
                IEncryption encryption = (IEncryption) container.getObject();
                if (container.getRequest() == ENCRYPTION_REQUEST) {
                    encryption.encrypt(container);
                } else if (container.getRequest() == DECRYPTION_REQUEST) {
                    encryption.decrypt(container);
                } else {
                    container.setResult(false);
                    container.setLogMessage(REQUEST_NOT_FOUND_LOG_MESSAGE);
                    container.setUserMessage(REQUEST_NOT_FOUND_USER_MESSAGE);
                }
            }

            // If the container contained object is a User object
            // check if the request was create user request
            // as well as valid user's credentials
            // if it was then create a new user
            if (container.getObject() instanceof IUser) {
                IUser user = (IUser) container.getObject();
                if (container.getRequest() == CREATE_USER_REQUEST) {
                    if (user.checkUserCredentials(container)) {
                        user.createNewUser(container);
                    }
                } else {
                    container.setResult(false);
                    container.setLogMessage(REQUEST_NOT_FOUND_LOG_MESSAGE);
                    container.setUserMessage(REQUEST_NOT_FOUND_USER_MESSAGE);
                }
            }

            // If the container contained object is an Image object
            // check if the request was check and upload image request
            // if it was then check the image for sexual content as well as valid image params
            // if the above was correct, upload the image if it was found
            if (container.getObject() instanceof IImage) {
                IImage image = (IImage) container.getObject();
                if (container.getRequest() == CHECK_AND_UPLOAD_IMAGE_REQUEST) {
                    try {
                        if (image.checkForSexualContent(container)) {
                            image.validateParams(container);
                        }
                    } catch (NullPointerException e) {
                        container.setResult(false);
                        container.setLogMessage(e.getMessage());
                        container.setUserMessage(ERROR_WITH_SERVER);
                    }
                } else {
                    container.setResult(false);
                    container.setLogMessage(REQUEST_NOT_FOUND_LOG_MESSAGE);
                    container.setUserMessage(REQUEST_NOT_FOUND_USER_MESSAGE);
                }
            }

            // return the modified container back to the client
            outputStream.writeObject(container);
            logger.info(container.toString());
            logger.info(String.format("Connection to %s via port %d ended", connection.getInetAddress().toString(), connection.getLocalPort()));

            inputStream.close();
            outputStream.close();
            connection.close();
        } catch (IOException | ClassNotFoundException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("Connection from %s via port %d", connection.getInetAddress(), connection.getPort());
    }
}
