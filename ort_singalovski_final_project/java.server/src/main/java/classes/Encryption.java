package classes;

import com.google.firebase.database.DataSnapshot;
import interfaces.IContainer;
import interfaces.IEncryption;
import server_related.FirebaseServer;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import static server_related.FirebaseServer.SERVER_KEY_LOCATION;
import static server_related.FirebaseServer.USERS_DATABASE_LOCATION;

/**
 * A class that builds the request for the server to either
 * encrypt and decrypt the user credentials
 */
public class Encryption implements Serializable, IEncryption {

    private static final FirebaseServer server = FirebaseServer.getInstance();

    private String email;
    private String password;
    private byte[] encryptedEmail;
    private byte[] encryptedPassword;

    // String which holds the key used to generate the cypher
    private String key;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Encryption.class.getName());

    private final static String ENCRYPTION_SUCCESSFUL = "Account credentials encrypted successfully";
    private final static String DECRYPTION_SUCCESSFUL = "Account credentials decrypted successfully";
    private final static String LOGIN_SUCCESSFUL = "Welcome";
    private static final String USER_CREDENTIALS_WRONG = "User credentials are wrong";
    private static final String ONE_OR_MORE_FIELD_INCORRECT = "One or more fields are incorrect";

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    private static final long serialVersionUID = 1458947511485698745L;

    /**
     * A method which encrypt the given email and password inside the Encryption class sent by the user
     * inside the container, and modify the container accordingly
     *
     * @param container Container object sent by the client that contains an Encryption object
     */
    @Override
    public void encrypt(final IContainer container) {
        try {
            DataSnapshot keyLocation = server.databaseSnapshot().child(SERVER_KEY_LOCATION);
            key = keyLocation.getValue(String.class);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedEmail = cipher.doFinal(email.getBytes());
            encryptedPassword = cipher.doFinal(password.getBytes());
            DataSnapshot usersLocation = server.databaseSnapshot().child(USERS_DATABASE_LOCATION);
            for (DataSnapshot user : usersLocation.getChildren()) {
                logger.info("Children:" + usersLocation.getChildrenCount());
                logger.info(user.child(EMAIL).getValue(String.class));
                logger.info(user.child(PASSWORD).getValue(String.class));
                if (user.child(EMAIL).getValue(String.class).equals(email) && user.child(PASSWORD).getValue(String.class).equals(password)) {
                    container.setResult(true);
                    container.setLogMessage(ENCRYPTION_SUCCESSFUL);
                    return;
                }
            }
            container.setResult(false);
            container.setLogMessage(USER_CREDENTIALS_WRONG);
            container.setUserMessage(ONE_OR_MORE_FIELD_INCORRECT);
        } catch (GeneralSecurityException | NullPointerException e) {
            logger.info(e.getMessage());
            container.setResult(false);
            container.setLogMessage(e.getMessage());
        }
    }

    /**
     * A method which decrypt the given two byte arrays inside the Encryption class sent by the user
     * inside the container to an email and password Strings, and modify the container accordingly
     *
     * @param container Container object sent by the client that contains an Encryption object
     */
    @Override
    public void decrypt(final IContainer container) {
        try {
            DataSnapshot keyLocation = server.databaseSnapshot().child(SERVER_KEY_LOCATION);
            key = keyLocation.getValue(String.class);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            email = new String(cipher.doFinal(encryptedEmail));
            password = new String(cipher.doFinal(encryptedPassword));
            logger.info(email + "," + password);
            DataSnapshot usersLocation = server.databaseSnapshot().child(USERS_DATABASE_LOCATION);
            for (DataSnapshot user : usersLocation.getChildren()) {
                if (user.child(EMAIL).getValue(String.class).equals(email) && user.child(PASSWORD).getValue(String.class).equals(password)) {
                    container.setResult(true);
                    container.setLogMessage(DECRYPTION_SUCCESSFUL);
                    return;
                }
            }
            container.setResult(false);
            container.setLogMessage(USER_CREDENTIALS_WRONG);
        } catch (GeneralSecurityException e) {
            logger.info(e.getMessage());
            container.setResult(false);
            container.setLogMessage(e.getMessage());
        }
    }

    @Override
    public String getEmail() throws NullPointerException {
        return email;
    }

    @Override
    public String getPassword() throws NullPointerException {
        return password;
    }

    @Override
    public byte[] getEncryptedEmail() throws NullPointerException {
        return encryptedEmail;
    }

    @Override
    public byte[] getEncryptedPassword() throws NullPointerException {
        return encryptedPassword;
    }
}