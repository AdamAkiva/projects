package classes;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.Exclude;
import interfaces.IContainer;
import interfaces.IUser;
import server_related.FirebaseServer;

import java.io.Serializable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static server_related.FirebaseServer.USERS_DATABASE_LOCATION;

/**
 * A class which holds the current logged in user
 */
public class User implements Serializable, IUser {

    // Exclude = ignore when uploading class to database
    @Exclude
    private static final FirebaseServer server = FirebaseServer.getInstance();

    // Variables that hold the user's information
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    // Variables for server side errors
    @Exclude
    private String firstNameError;
    @Exclude
    private String lastNameError;
    @Exclude
    private String emailError;
    @Exclude
    private Boolean passwordError;

    // Set the logger for the this file
    @Exclude
    private static final Logger logger = Logger.getLogger(User.class.getName());

    @Exclude
    private static final String WELCOME = "Welcome %s %s !!!";
    @Exclude
    private static final String USER_CREATED_SUCCESSFULLY = "User created successfully";
    @Exclude
    private static final String SOMETHING_WENT_WRONG = "Something went wrong :(";
    @Exclude
    private static final String USER_TEMPERED_WITH_DATA = "User tempered with data";

    // Static variables which hold String values
    @Exclude
    private static final String FIRST_NAME = "First name";
    @Exclude
    private static final String LAST_NAME = "Last name";
    @Exclude
    private static final String EMAIL = "Email";
    @Exclude
    private static final String MUST_CONTAIN_LETTERS_ONLY = "%s field must contain letters only";
    @Exclude
    private static final String MUST_BE_LONGER_THAN = "Must be longer or equal to %d letters";
    @Exclude
    private static final String MUST_NOT_BE_LONGER_THAN = "Must not be longer than %d letters";
    @Exclude
    private static final String MUST_CONTAIN_VALUE_ERROR = "%s field must contain value";
    @Exclude
    private static final String EMAIL_DOES_NOT_MATCH_REGEX = "%s field must have a valid format";

    // Static String values which holds regular expression for a valid first and last name
    @Exclude
    private static final String VALID_FIRST_AND_LAST_NAME = "^[a-zA-Z]{2,15}";
    // Static variable which hold the regular expression for a valid email address
    @Exclude
    private static final Pattern VALID_EMAIL_ADDRESS = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    // Static String values which holds regular expression for a valid password value (the restrictions can be seen above)
    @Exclude
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
    @Exclude
    // Static int value to hold the minimum characters limit
    private static final int FIRST_AND_LAST_NAME_MIN_CHARACTERS_LIMIT = 2;
    // Static int value to hold the maximum characters limit
    @Exclude
    private static final int FIRST_AND_LAST_NAME_MAX_CHARACTERS_LIMIT = 15;

    // Set the serialVersionUID to allow synchronization of the classes between
    // the java.client project and the java.server project
    @Exclude
    private static final long serialVersionUID = 1458756984741225457L;


    /**
     * A method to check the user's credentials a second time
     * on the server side and modify the given container accordingly
     *
     * @param container Container object received from the client that contains a User object
     * @return Boolean value which indicates whether the credentials are valid
     */
    public Boolean checkUserCredentials(IContainer container) {
        if (firstName != null && lastName != null && email != null && password != null) {
            if (firstName.length() == 0) {
                firstNameError = String.format(MUST_CONTAIN_VALUE_ERROR, FIRST_NAME);
            } else if (!firstName.matches(VALID_FIRST_AND_LAST_NAME)) {
                if (firstName.length() > 15) {
                    firstNameError = String.format(MUST_NOT_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MAX_CHARACTERS_LIMIT);
                } else if (firstName.length() < 3) {
                    firstNameError = String.format(MUST_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MIN_CHARACTERS_LIMIT);
                } else {
                    firstNameError = String.format(MUST_CONTAIN_LETTERS_ONLY, FIRST_NAME);
                }
            }
            if (lastName.length() == 0) {
                lastNameError = String.format(MUST_CONTAIN_VALUE_ERROR, LAST_NAME);
            } else if (!lastName.matches(VALID_FIRST_AND_LAST_NAME)) {
                if (lastName.length() > 15) {
                    lastNameError = String.format(MUST_NOT_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MAX_CHARACTERS_LIMIT);
                } else if (lastName.length() < 3) {
                    lastNameError = String.format(MUST_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MIN_CHARACTERS_LIMIT);
                } else {
                    lastNameError = String.format(MUST_CONTAIN_LETTERS_ONLY, LAST_NAME);
                }
            }
            if (email.length() == 0) {
                emailError = String.format(MUST_CONTAIN_VALUE_ERROR, EMAIL);
            } else if (!VALID_EMAIL_ADDRESS.matcher(email).find()) {
                emailError = String.format(EMAIL_DOES_NOT_MATCH_REGEX, EMAIL);
            }
            if (password.length() == 0 || !password.matches(PASSWORD_REGEX)) {
                passwordError = true;
            }
            if ((firstNameError != null && firstNameError.length() > 0) || (lastNameError != null && lastNameError.length() > 0) || (emailError != null && emailError.length() > 0) || (passwordError != null && passwordError)) {
                container.setResult(false);
                container.setUserMessage(SOMETHING_WENT_WRONG);
                container.setLogMessage(USER_TEMPERED_WITH_DATA);
                return false;
            }
        } else {
            container.setResult(false);
            container.setUserMessage(SOMETHING_WENT_WRONG);
            container.setLogMessage(USER_TEMPERED_WITH_DATA);
            return false;
        }
        return true;
    }

    /**
     * A method which receives a container from the user client and tries to create
     * a user in the database with the supplied information (first name, last name,
     * email, password), and modify the given container accordingly
     *
     * @param container Container object received from the client that contains User object
     */
    @Override
    public void createNewUser(IContainer container) {
        String uid = new Token(28).getToken();
        UserRecord.CreateRequest request = new UserRecord.CreateRequest().setUid(uid).setEmail(email).setEmailVerified(false).setPassword(password).setDisplayName(String.format("%s %s", firstName, lastName)).setDisabled(false);
        // If the user was created successfully add the user's information to the database
        try {
            server.authService().createUser(request);
            server.writeToDatabase(String.format("%s%s", USERS_DATABASE_LOCATION, uid), this);
            container.setResult(true);
            container.setUserMessage(String.format(WELCOME, firstName, lastName));
            container.setLogMessage(USER_CREATED_SUCCESSFULLY);
        } catch (FirebaseAuthException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            container.setResult(false);
            container.setUserMessage(SOMETHING_WENT_WRONG);
            container.setLogMessage(e.getMessage());
        }
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
}