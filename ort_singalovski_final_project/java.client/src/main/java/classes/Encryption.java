package classes;

import interfaces.IEncryption;

import java.io.*;
import java.util.logging.Logger;

/**
 * A class that builds the request for the server to either
 * encrypt and decrypt the user credentials
 */
public class Encryption implements Serializable, IEncryption {

    // Strings that hold the user's email and password
    private String email;
    private String password;

    // byte arrays that hold the user's encrypted email and password
    private byte[] encryptedEmail;
    private byte[] encryptedPassword;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Encryption.class.getName());

    // Static Strings that hold the location of the encrypted email and password files
    private static final String EMAIL_PATH = "C:\\Users\\ASUS\\Desktop\\Projects\\finalproject-master\\java.client\\-";
    private static final String PASSWORD_PATH = "C:\\Users\\ASUS\\Desktop\\Projects\\finalproject-master\\java.client\\--";

    private static final String CREATED_NEW_EMAIL_FILE = "Successfully created new email file";
    private static final String CREATED_NEW_PASSWORD_FILE = "Successfully created new password file";
    private static final String FILE_NOT_FOUND = "File does not exist";

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    private static final long serialVersionUID = 1458947511485698745L;

    /**
     * A constructor for an encryption request
     *
     * @param email    String that holds the user's email
     * @param password String that holds the user's password
     * @throws IOException if the process of creating the files for the encrypted email and password failed
     */
    public Encryption(String email, String password) throws IOException {
        this.email = email;
        this.password = password;

        File f = new File(EMAIL_PATH);
        if (!f.exists()) {
            if (f.createNewFile()) {
                logger.info(CREATED_NEW_EMAIL_FILE);
            }
        }

        File f2 = new File(PASSWORD_PATH);
        if (f2.createNewFile()) {
            logger.info(CREATED_NEW_PASSWORD_FILE);
        }
    }

    /**
     * A constructor for a decryption request
     *
     * @throws IOException If one of the email or password files were not found or one or both of the file's
     *                     length was equal to 0
     */
    public Encryption() throws IOException {
        if (!new File(EMAIL_PATH).exists() || new File(EMAIL_PATH).length() == 0) {
            throw new IOException(FILE_NOT_FOUND);
        }
        if (!new File(PASSWORD_PATH).exists() || new File(PASSWORD_PATH).length() == 0) {
            throw new IOException(FILE_NOT_FOUND);
        } else {
            readFromFiles();
        }
    }

    /**
     * A method which writes the received encrypted email and password byte arrays to the specified files
     *
     * @return Boolean value which indicates whether the process was successful or not
     */
    @Override
    public boolean writeToFiles() {
        try (FileOutputStream fos = new FileOutputStream(new File(EMAIL_PATH))) {
            fos.write(encryptedEmail);
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
        try (FileOutputStream fos = new FileOutputStream(new File(PASSWORD_PATH))) {
            fos.write(encryptedPassword);
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * A method which reads the byte arrays from the email and password files
     * to the attributed variables to be sent to decryption on the server
     *
     * @return Boolean values which indicates whether the process was successful or not
     */
    @Override
    public boolean readFromFiles() {
        encryptedEmail = new byte[(int) new File(EMAIL_PATH).length()];
        encryptedPassword = new byte[(int) new File(PASSWORD_PATH).length()];
        try (FileInputStream fis = new FileInputStream(new File(EMAIL_PATH))) {
            fis.read(encryptedEmail);
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
        try (FileInputStream fis = new FileInputStream(new File(PASSWORD_PATH))) {
            fis.read(encryptedPassword);
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
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