package com.aa.matrix.etc;

/**
 * @author Adam Akiva
 * Class used to represent an error from a parameter to a constructor
 */
public class InvalidParameterException extends Exception {

    public InvalidParameterException(final String message) {
        super(message);
    }
}
