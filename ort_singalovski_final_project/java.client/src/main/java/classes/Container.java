package classes;

import interfaces.IContainer;
import interfaces.IEncryption;
import interfaces.IImage;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static classes.TrackingProcess.EXTENSION;
import static classes.TrackingProcess.IMAGE_SAVE_LOCATION;
import static ui.App.EXECUTOR_SERVICE;

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
    private final int request;

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

    private static final String SERVER_IS_DOWN_LOG_MESSAGE = "Server is down";
    private static final String SERVER_IS_DOWN_USER_MESSAGE = "Our server is down at the moment, please try again later";
    private static final String IMAGE_DELETED_SUCCESSFULLY = "Image deleted successfully";
    private static final String CONNECTION_CLOSED = "Connection with %s via port %d closed";

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    private static final long serialVersionUID = 1574258964123587456L;

    /**
     * A constructor for a container class
     *
     * @param request Integer with the request number for the server to identify
     * @param t       The class associated with the request
     * @throws UnknownHostException Thrown if the host's ip could not be determent
     * @throws IOException          Thrown if there was an I/O while creating the socket, or if the host's ip could not be found,
     *                              or the client could not connect to the server
     * @throws InterruptedException Thrown if the thread was interrupted while waiting
     * @throws ExecutionException   Thrown if computation the threw an exception
     * @throws ConnectException     Thrown if the client could not connect to the server
     */
    public Container(int request, T t) throws IOException, InterruptedException, ExecutionException, ClassCastException {
        this.request = request;
        result = false;
        logMessage = "";
        userMessage = "";
        if (t instanceof Serializable) {
            this.t = t;
            sendContainerOverSocket(this);
        } else {
            throw new ClassCastException("Object transferred via socket must be serializable");
        }
    }

    /**
     * An inner class which holds the server details
     * as well as implementing the Callable interface
     * to allow the a container class to be returned to
     * the main class
     */
    public class ConnectionSender implements Callable {

        // A Socket variable to hold the connection with the server
        private Socket socket;

        // A String to hold the server address (local address in this case 127.0.0.1)
        private final String address;

        // An Integer variable to hold the port number (in this case 9991)
        private final int port;

        // ObjectOutPutStream to hold the container object we wish to send over the socket
        private ObjectOutputStream outputStream;

        // ObjectInputStream to hold the the result from the server
        private ObjectInputStream inputStream;

        // Container object to be sent over the socket as well as
        // being the object to receive the result from the server
        private IContainer container;

        /**
         * A constructor for the inner ConnectionSenderClass
         *
         * @param container The container object you wish to send over the socket
         * @throws UnknownHostException Thrown if the host's ip could not be determent
         * @throws IOException          Thrown if there was an I/O while creating the socket
         * @throws ConnectException     Thrown if the client could not connect to the server
         */
        public ConnectionSender(IContainer container) throws UnknownHostException, ConnectException, IOException {
            address = InetAddress.getLocalHost().getHostAddress();
            port = 9991;
            this.container = container;
            socket = new Socket(address, port);
        }

        /**
         * A method which sends the given container (from the constructor) via the socket to the server
         *
         * @return A container object with the result information
         */
        @Override
        public IContainer call() {
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(container);

                inputStream = new ObjectInputStream(socket.getInputStream());

                try {
                    container = (IContainer) inputStream.readObject();
                } catch (ClassNotFoundException e) {
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }

                logger.info(container.getLogMessage());
                logger.info(container.getUserMessage());

                logger.info(String.format(CONNECTION_CLOSED, address, port));
                outputStream.close();
                inputStream.close();
                socket.close();
                return container;
            } catch (IOException e) {
                logger.info(e.getMessage());
                e.printStackTrace();
                return container;
            }
        }
    }

    /**
     * A method which uses the ConnectionSender class to send the container via the socket
     * receive back a container with the result information that can be used accordingly
     *
     * @throws UnknownHostException Thrown if the host's ip could not be determent
     * @throws IOException          Thrown if there was an I/O while creating the socket
     * @throws InterruptedException Thrown if the thread was interrupted while waiting
     * @throws ExecutionException   Thrown if computation the threw an exception
     * @throws ConnectException     Thrown if the client could not connect to the server
     */
    @SuppressWarnings("unchecked")
    @Override
    public void sendContainerOverSocket(IContainer container) throws UnknownHostException, IOException, InterruptedException, ExecutionException, ConnectException {
        Future<IContainer> res;
        res = EXECUTOR_SERVICE.submit(new ConnectionSender(container));
        IContainer temp = res.get();
        result = temp.getResult();
        logMessage = temp.getLogMessage();
        userMessage = temp.getUserMessage();
        t = (T) temp.getObject();
        if (result) {
            takeActionByResult();
        }

    }

    /**
     * A function which do something according to the container result received from the server
     *
     * @throws IOException Thrown if the process failed
     */
    private void takeActionByResult() throws IOException {
        if (request == ENCRYPTION_REQUEST) {
            ((IEncryption) t).writeToFiles();
        } else if (request == DECRYPTION_REQUEST) {
            new TrackingProcess(new User(((IEncryption) t).getEmail(), ((IEncryption) t).getPassword())).run();
        } else if (request == CREATE_USER_REQUEST) {
            // handled in the MainWindow file in SubWindow inner class in CheckCredentials method
        } else if (request == CHECK_AND_UPLOAD_IMAGE_REQUEST) {
            File file;
            if (((IImage) t).getFileName().endsWith(EXTENSION)) {
                file = new File(String.format("%s%s", IMAGE_SAVE_LOCATION, ((IImage) t).getFileName()));
            } else {
                file = new File(String.format("%s%s.%s", IMAGE_SAVE_LOCATION, ((IImage) t).getFileName(), EXTENSION));
            }
            if (file.delete()) {
                logger.info(IMAGE_DELETED_SUCCESSFULLY);
            }
        }
    }

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
