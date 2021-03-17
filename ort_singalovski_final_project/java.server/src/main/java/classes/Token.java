package classes;

import interfaces.IToken;

import java.security.SecureRandom;
import java.util.logging.Logger;

/**
 * A class which creates a unique UID for a new user
 */
public class Token implements IToken {

    // What characters can be used in the token's creation
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // String that holds the token (User unique uid)
    private final String token;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(Token.class.getName());

    /**
     * A constructor for the Token class
     *
     * @param length Integer which indicates the desired unique token length
     */
    public Token(int length) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        token = sb.toString();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return String.format("Token is: %s", token);
    }
}
