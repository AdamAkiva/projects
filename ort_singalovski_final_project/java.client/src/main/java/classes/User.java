package classes;

import interfaces.IUser;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * A class which holds the current logged in user
 */
public class User implements Serializable, IUser {

    // Variables that hold the user's information
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    // Variables for server side errors
    private String firstNameError;
    private String lastNameError;
    private String emailError;
    private Boolean passwordError;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(User.class.getName());

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    private static final long serialVersionUID = 1458756984741225457L;

    /**
     * A constructor for the User class with first and last name
     *
     * @param firstName String which hold the user's first name
     * @param lastName  String which hold the user's last name
     * @param email     String which hold the user's email
     * @param password  String which hold the user's password
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * A constructor for the User class without first and last name
     *
     * @param email    String which hold the user's email
     * @param password String which hold the user's password
     */
    public User(String email, String password) {
        this.firstName = "Undefined";
        this.lastName = "Undefined";
        this.email = email;
        this.password = password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setFirstNameError(String firstNameError) {
        this.firstNameError = firstNameError;
    }

    @Override
    public void setLastNameError(String lastNameError) {
        this.lastNameError = lastNameError;
    }

    @Override
    public void setEmailError(String emailError) {
        this.emailError = emailError;
    }

    @Override
    public void setPasswordError(Boolean passwordError) {
        this.passwordError = passwordError;
    }

    @Override
    public String getFirstNameError() {
        return firstNameError;
    }

    @Override
    public String getLastNameError() {
        return lastNameError;
    }

    @Override
    public String getEmailError() {
        return emailError;
    }

    @Override
    public Boolean getPasswordError() {
        return passwordError;
    }

    @Override
    public String toString() {
        return String.format("First name:%s\nLast name:%s\nEmail:%s\nPassword:%s", firstName, lastName, email, password);
    }
}