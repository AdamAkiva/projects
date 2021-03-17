package interfaces;

import java.io.IOException;

public interface IEncryption {

    String getEmail() throws NullPointerException;

    String getPassword() throws NullPointerException;

    byte[] getEncryptedEmail() throws NullPointerException;

    byte[] getEncryptedPassword() throws NullPointerException;

    boolean writeToFiles() throws IOException;

    boolean readFromFiles() throws IOException;
}
