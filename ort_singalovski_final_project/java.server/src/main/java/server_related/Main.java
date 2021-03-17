package server_related;

import java.util.logging.Logger;

public class Main {

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        new Server().start();
    }
}