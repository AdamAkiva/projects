package ui;

import classes.Container;
import classes.Encryption;
import interfaces.IEncryption;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static classes.Container.DECRYPTION_REQUEST;

public class App extends Application {

    //
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(App.class.getName());

    // The title of the main window
    private static final String TITLE = "Test";

    @Override
    public void start(Stage primaryStage) {
        // Create a new scene with the new window class
        Scene scene = new Scene(new MainWindow(new Insets(30, 10, 30, 10), 10, 10).createConstraints(5).createEmailRow().createPasswordRow().createRegisterButton().createSubmitButton());
        // Set the stage title
        primaryStage.setTitle(TITLE);
        // Set the stage to not allow a resize of the window
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        // Set the stage scene as the one that was created above
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Check if there is a saved user credentials and if so login
        try {
            new Container<IEncryption>(DECRYPTION_REQUEST, new Encryption());
        }
        // If not show the main javafx window
        catch (IOException | InterruptedException | ExecutionException | ClassCastException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            launch(args);
        }
    }
}
