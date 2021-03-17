package server_related;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import interfaces.IDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A singleton class which implements the IDatabase interface for a Firebase google database
 */
public class FirebaseServer implements IDatabase {

    // A singleton FirebaseServer instance
    private static FirebaseServer instance;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    // A DataSnapshot which holds the entire database snapshot
    private DataSnapshot databaseSnapshot;

    // A bucket object which holds the database storage
    private Bucket bucket;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(FirebaseServer.class.getName());

    // String variable which hold the service account path to allow full access to the database functions
    private static final String JSON_SERVICE_ACCOUNT_PATH = "C:\\Users\\ASUS\\Desktop\\Projects\\finalproject-master\\java.server\\final-project-c56f3-db6a5ae82302.json";

    private static final String FIREBASE_DATABASE_URL = "https://final-project-c56f3.firebaseio.com";
    private static final String FIREBASE_STORAGE_URL = "final-project-c56f3.appspot.com";

    // A static Integer to indicate how long it should take to return a response
    // to the user
    private static final int CONNECTION_TIME_OUT = 15000;

    public static final String USERS_DATABASE_LOCATION = "users/";
    public static final String IMAGE_STORAGE_LOCATION = "images/";
    public static final String IMAGE_DATABASE_LOCATION = "images/";
    public static final String SERVER_KEY_LOCATION = "server/key";

    /**
     * A constructor for the FirebaseServer class
     * which sets all the paths to the different options
     * as well as setting up a listener for updating the databaseSnapshot each time
     * any data inside the database changed
     */
    private FirebaseServer() {
        try {
            FileInputStream myServiceAccount = new FileInputStream(JSON_SERVICE_ACCOUNT_PATH);
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(myServiceAccount)).setConnectTimeout(CONNECTION_TIME_OUT).setDatabaseUrl(FIREBASE_DATABASE_URL).setStorageBucket(FIREBASE_STORAGE_URL).build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return;
        }

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                databaseSnapshot = snapshot;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                logger.info(error.getMessage());
            }
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        bucket = StorageClient.getInstance().bucket();
    }

    @Override
    public FirebaseAuth authService() {
        if (instance == null) {
            instance = new FirebaseServer();
        }
        if (auth != null) {
            return auth;
        }
        return null;
    }

    @Override
    public FirebaseDatabase databaseService() {
        if (instance == null) {
            instance = new FirebaseServer();
        }
        if (database != null) {
            return database;
        }
        return null;
    }

    @Override
    public DataSnapshot databaseSnapshot() {
        if (instance == null) {
            instance = new FirebaseServer();
        }
        if (databaseSnapshot != null) {
            return databaseSnapshot;
        }
        return null;
    }

    @Override
    public Bucket storageService() {
        if (instance == null) {
            instance = new FirebaseServer();
        }
        if (bucket != null) {
            return bucket;
        }
        return null;
    }

    /**
     * A method which write the given object to a given String location inside the database
     *
     * @param writeLocation String which indicates where to write the Object to
     * @param obj           Object which should be written to the the given WriteLocation
     */
    @Override
    public void writeToDatabase(String writeLocation, Object obj) {
        database.getReference().child(writeLocation).setValueAsync(obj);
    }

    /**
     * A method which writes the given file (byte array) to the database storage
     *
     * @param fileLocation String which indicates where to write the file to
     * @param fileName     String which holds the file name
     * @param data         byte array that hold the file's data
     * @param fileDesc     String which hold the files type (jpg in this case)
     * @throws StorageException Thrown if the file was not uploaded
     */
    @Override
    public void writeToStorage(String fileLocation, String fileName, byte[] data, String fileDesc) throws StorageException {
        if (!fileLocation.endsWith("/")) {
            fileLocation += "/";
        }
        logger.info("Length: " + data.length);
        bucket.create(String.format("%s%s", fileLocation, fileName), data, fileDesc);
    }

    /**
     * A method to return the same instance of FirebaseServer
     *
     * @return This FirebaseServer instance
     */
    public static FirebaseServer getInstance() {
        if (instance == null) {
            instance = new FirebaseServer();
        }
        return instance;
    }
}