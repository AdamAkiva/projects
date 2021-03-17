package server_related;

import com.google.firebase.database.DataSnapshot;
import runnables.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * A class which starts the server program to answer requests from user clients
 */
public class Server extends Thread {

    // Static variable which holds a FirebaseServer instance
    private static final FirebaseServer server = FirebaseServer.getInstance();

    private ServerSocket serverSocket = null;

    // Boolean variable that indicates whether the server should be stopped for whatever reason
    private boolean stopped = false;

    // String which holds the port location in the database
    private static final String DATABASE_PORT_LOCATION = "server/port";

    private static final String SERVER_RUNNING = "Server is running on port %d";

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    // A static Integer which indicates how long to wait for a check if the port number could not
    // be received from the database
    private static final int CHECK_FOR_PORT_NUMBER_WAIT_TIME = 1000;

    /**
     * A method which does the main program for the server:
     * It gets the port number from the server and start the ServerSocket at the given port
     * as well as waiting to receive requests from the user's clients and handle them
     */
    @Override
    public void run() {
        while (true) {
            try {
                // Prevents opening two instances of server for whatever reason
                if (server.databaseSnapshot() != null && serverSocket == null) {
                    DataSnapshot portLocation = server.databaseSnapshot().child(DATABASE_PORT_LOCATION);
                    int port = portLocation.getValue(Integer.class);
                    startServer(port);
                    while (!stopped) {
                        Socket connection = serverSocket.accept();
                        new ConnectionHandler(connection).run();
                        if (stopped) {
                            return;
                        }
                    }
                }
                sleep(CHECK_FOR_PORT_NUMBER_WAIT_TIME);
            } catch (IOException | InterruptedException | NullPointerException e) {
                stopServer();
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * A method the start the ServerSocket at the given port
     *
     * @param port Integer which indicates on what port to open the ServerSocket
     * @throws IOException Thrown if the process failed
     */
    private void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        logger.info(String.format(SERVER_RUNNING, port));
    }

    /**
     * A method which close the ServerSocket if the main server loop exited
     * for whatever reason
     */
    private synchronized void stopServer() {
        try {
            stopped = true;
            serverSocket.close();
        } catch (IOException | NullPointerException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
    }
}
