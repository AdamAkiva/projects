package interfaces;

public interface IEncryption {

    void encrypt(IContainer container);

    void decrypt(IContainer container);

    String getEmail() throws NullPointerException;

    String getPassword() throws NullPointerException;

    byte[] getEncryptedEmail() throws NullPointerException;

    byte[] getEncryptedPassword() throws NullPointerException;
}
