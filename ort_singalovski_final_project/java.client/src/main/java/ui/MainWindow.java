package ui;

import classes.TrackingProcess;
import classes.Container;
import classes.Encryption;
import classes.User;
import interfaces.IContainer;
import interfaces.IEncryption;
import interfaces.IUser;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static classes.Container.CREATE_USER_REQUEST;
import static classes.Container.ENCRYPTION_REQUEST;

/**
 * A class that extends the GridPane javafx object to create a MainWindow object
 * with the GridPane restrictions and resources
 */
public class MainWindow extends GridPane {

    // Variables to hold the javafx ui controls
    private Text tEmail;
    private Text tPassword;
    private Text tEmailError;
    private Text tPasswordError;
    private TextField tfEmail;
    private PasswordField pfPassword;
    private Button btnSubmit;
    private Button btnRegister;

    // Event handler to clean the fields on mouse click
    private EventHandler<MouseEvent> clearErrorFields;

    // Variables that check the TextFields for any invalid inputs
    private boolean emailValidation;
    private boolean passwordValidation;

    private MyDialog dialog;
    private Boolean enterPressed;

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    // Static variables which hold the max width and height percentages of the javafx window
    private static final double MAX_WIDTH_PERCENTAGE = 85.0;
    private static final double MAX_HEIGHT_PERCENTAGE = 100.0;

    // Static variables which hold String values
    private static final String EMAIL = "Email";
    private static final String ENTER_EMAIL = "Enter email here";
    private static final String PASSWORD = "Password";
    private static final String ENTER_PASSWORD = "Enter password here";
    private static final String SUBMIT = "Submit";
    private static final String REGISTER = "Register";
    private static final String MUST_CONTAIN_VALUE_ERROR = "%s field must contain value";
    private static final String EMAIL_DOES_NOT_MATCH_REGEX = "%s field must have a valid format";
    private static final String NO_ERROR = "";
    private static final String CREDENTIALS_ERROR_TITLE = "Credentials error";
    private static final String COLON = ":";
    private static final String SERVER_ERROR_TITLE = "Server error";
    private static final String SERVER_ERROR_BODY = "Our server seem to be down,\nPlease try again later";
    private static final String UNKNOWN_ERROR_TITLE = "Unknown error";
    private static final String UNKNOWN_ERROR_BODY = "Something went wrong, please try again later";

    // Static variable which hold the regular expression for a valid email address
    private static final Pattern VALID_EMAIL_ADDRESS = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    // Static variables which hold the default font sizes
    private static final int DEFAULT_FONT_SIZE = 14;
    private static final int ERROR_FIELD_FONT_SIZE = 12;

    /**
     * A constructor for a new javafx window
     *
     * @param insets Nullable Insets margins for the window
     * @param vGap   Nullable integer for the vertical gap between rows
     * @param hGap   Nullable integer for the horizontal gap between rows
     */
    public MainWindow(@Nullable Insets insets, @Nullable Integer vGap, @Nullable Integer hGap) {

        if (insets != null) {
            setPadding(insets);
        }
        if (vGap != null) {
            setVgap(vGap);
        }
        if (hGap != null) {
            setHgap(hGap);
        }

        emailValidation = true;
        passwordValidation = true;

        dialog = null;

        // Set the mouse event handler to clear the specified controls if they are not valid by clicking on them
        clearErrorFields = (MouseEvent event) -> {
            if (!emailValidation) {
                tfEmail.clear();
                tEmailError.setText(NO_ERROR);
                pfPassword.clear();
            }
            if (!passwordValidation) {
                pfPassword.clear();
                tPasswordError.setText(NO_ERROR);
            }
            emailValidation = true;
            passwordValidation = true;
        };
    }

    /**
     * A method which created the rows and columns constraints according
     * to the given row count
     *
     * @param rowCount Integer which specified the row count
     * @return The window which the function was called on
     */
    public MainWindow createConstraints(int rowCount) {

        // Create a row constraint and set it to give each row an equal size
        // scaling to the window size
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight((MAX_HEIGHT_PERCENTAGE - 20.0) / (rowCount - 2));

        // Create a row constraint for the register button on the button of the window
        RowConstraints rc2 = new RowConstraints();
        rc.setPercentHeight(20.0);

        // Create two columns constraints:
        // the first is for 1/3 of the window's width
        // the second is for 2/3 of the window's width
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(MAX_WIDTH_PERCENTAGE * 0.33);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(MAX_WIDTH_PERCENTAGE * 0.66);

        // Set the constraints
        getColumnConstraints().add(cc);
        getColumnConstraints().add(cc2);
        for (int i = 0; i < rowCount - 1; i++) {
            getRowConstraints().add(rc);
        }
        getRowConstraints().add(rc2);

        return this;
    }


    /**
     * A method which creates the email row consisting of a two Text controls
     * and one TextField control
     *
     * @return The window which the function was called on
     */
    public MainWindow createEmailRow() {

        tEmail = new Text(EMAIL + COLON);
        tEmail.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
        add(tEmail, 0, 0);

        tfEmail = new TextField();
        tfEmail.setPromptText(ENTER_EMAIL);
        add(tfEmail, 1, 0);
        tfEmail.addEventHandler(KeyEvent.KEY_RELEASED, this::checkCredentials);
        tfEmail.addEventHandler(MouseEvent.MOUSE_RELEASED, clearErrorFields);

        tEmailError = new Text();
        tEmailError.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, ERROR_FIELD_FONT_SIZE));
        tEmailError.setFill(Color.RED);
        add(tEmailError, 0, 1, 2, 1);

        GridPane.setHalignment(tEmail, HPos.RIGHT);
        GridPane.setHalignment(tfEmail, HPos.LEFT);
        GridPane.setHalignment(tEmailError, HPos.CENTER);

        return this;
    }

    /**
     * A method which creates the password row consisting of a two text controls
     * and one PasswordField control
     *
     * @return The window which the function was called on
     */
    public MainWindow createPasswordRow() {

        tPassword = new Text(PASSWORD + COLON);
        tPassword.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
        add(tPassword, 0, 2);

        pfPassword = new PasswordField();
        pfPassword.setPromptText(ENTER_PASSWORD);
        add(pfPassword, 1, 2);
        pfPassword.addEventHandler(KeyEvent.KEY_RELEASED, this::checkCredentials);
        pfPassword.addEventHandler(MouseEvent.MOUSE_RELEASED, clearErrorFields);

        tPasswordError = new Text();
        tPasswordError.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, ERROR_FIELD_FONT_SIZE));
        tPasswordError.setFill(Color.RED);
        add(tPasswordError, 0, 3, 2, 1);

        GridPane.setHalignment(tPassword, HPos.RIGHT);
        GridPane.setHalignment(pfPassword, HPos.LEFT);
        GridPane.setHalignment(tPasswordError, HPos.CENTER);

        return this;
    }

    /**
     * A method which creates a new Button control for a submit button
     *
     * @return The window which the function was called on
     */
    public MainWindow createSubmitButton() {
        btnSubmit = new Button(SUBMIT);
        add(btnSubmit, 0, 4);
        GridPane.setHalignment(btnSubmit, HPos.RIGHT);
        btnSubmit.addEventHandler(MouseEvent.MOUSE_RELEASED, this::checkCredentials);

        return this;
    }

    /**
     * A method which creates a new Button control which opens a new window
     * that allows a new user to register
     *
     * @return The window which the function was called on
     */
    public MainWindow createRegisterButton() {
        btnRegister = new Button(REGISTER);
        add(btnRegister, 1, 4);
        GridPane.setHalignment(btnRegister, HPos.LEFT);
        GridPane.setValignment(btnRegister, VPos.CENTER);

        btnRegister.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent event) -> {
            // Hide the MainWindow and create a new SubWindow which
            // allows registration
            ((Stage) getScene().getWindow()).hide();
            Stage stage = new Stage();
            stage.setTitle(REGISTER);
            Scene scene = new Scene(new MainWindow.SubWindow(new Insets(30, 10, 60, 10), 10, 10).createConstraints(9).createFirstNameRow().createLastNameRow().createEmailRow().createPasswordRow().createSubmitButton());
            stage.setResizable(false);
            stage.setScene(scene);
            // When the SubWindow is closed reopen the MainWindow
            stage.setOnHidden((WindowEvent closeEvent) -> {
                ((Stage) getScene().getWindow()).show();
            });
            stage.show();
        });

        return this;
    }

    /**
     * A method which checks the the TextField controls for a valid user input
     *
     * @param event The InputEvent which caused this function to activate
     */
    private void checkCredentials(final InputEvent event) {
        // Each button pressed while hovering the TextField controls cause this if to be checked
        // however only an Enter press or click on the submit button should proceed
        if (enterPressed != null && enterPressed && !(event instanceof MouseEvent)) {
            enterPressed = false;
        } else if (((event instanceof KeyEvent && ((KeyEvent) event).getCode() == KeyCode.ENTER) || event instanceof MouseEvent)) {
            String email = tfEmail.getText();
            String password = pfPassword.getText();
            if (email.length() == 0) {
                tEmailError.setText(String.format(MUST_CONTAIN_VALUE_ERROR, EMAIL));
                emailValidation = false;
            } else if (!VALID_EMAIL_ADDRESS.matcher(email).find()) {
                tEmailError.setText(EMAIL_DOES_NOT_MATCH_REGEX);
                emailValidation = false;
            }
            if (password.length() == 0) {
                pfPassword.clear();
                tPasswordError.setText(String.format(MUST_CONTAIN_VALUE_ERROR, PASSWORD));
                passwordValidation = false;
            }
            // If the fields are valid send an encryption request to the server which returns
            // a file that contains encrypted user credentials to allow the system to login automatically
            // the next time the program is started
            if (emailValidation && passwordValidation) {
                try {
                    IContainer container = new Container<IEncryption>(ENCRYPTION_REQUEST, new Encryption(email, password));
                    logger.info(container.toString());
                    if (container.getResult()) {
                        ((Stage) getScene().getWindow()).hide();
                        IUser user = new User(email, password);
                        new TrackingProcess(user).start();
                    } else {
                        dialog = new MyDialog(container, CREDENTIALS_ERROR_TITLE);
                        dialog.showAndWait();
                        enterPressed = true;
                    }
                } catch (IOException | InterruptedException | ExecutionException | ClassCastException e) {
                    if (e instanceof IOException) {
                        dialog = new MyDialog(SERVER_ERROR_TITLE, SERVER_ERROR_TITLE, SERVER_ERROR_BODY, null);
                        dialog.showAndWait();
                        enterPressed = true;
                    } else {
                        dialog = new MyDialog(UNKNOWN_ERROR_TITLE, UNKNOWN_ERROR_TITLE, UNKNOWN_ERROR_BODY, null);
                        dialog.showAndWait();
                        enterPressed = true;
                    }
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * An inner class that extends the GridPane javafx object to create a SubWindow object
     * with the GridPane restrictions and resources
     */
    private class SubWindow extends MainWindow {

        // Variables to hold the javafx ui controls
        private Text tFirstName;
        private Text tLastName;
        private Text tEmail;
        private Text tPassword;
        private Text tFirstNameError;
        private Text tLastNameError;
        private Text tEmailError;
        private Text tPasswordError;
        private TextArea taPasswordRules;
        private TextField tfFirstName;
        private TextField tfLastName;
        private TextField tfEmail;
        private PasswordField pfPassword;
        private Button btnSubmit;

        // Variables that check the TextFields for any invalid inputs
        private boolean firstNameValidation;
        private boolean lastNameValidation;
        private boolean emailValidation;
        private boolean passwordValidation;

        // Event handler to clean the fields on mouse click
        private EventHandler<MouseEvent> clearErrorFields;

        // Static variables which hold String values
        private static final String FIRST_NAME = "First name";
        private static final String LAST_NAME = "Last name";
        private static final String ENTER_FIRST_NAME = "Enter first name here";
        private static final String ENTER_LAST_NAME = "Enter last name here";
        private static final String MUST_CONTAIN_LETTERS_ONLY = "%s field must contain letters only";
        private static final String MUST_BE_LONGER_THAN = "Must be longer or equal to %d letters";
        private static final String MUST_NOT_BE_LONGER_THAN = "Must not be longer than %d letters";
        private static final String PASSWORD_RULES_TITLE = "Password rules";
        private static final String PASSWORD_RULES_BODY = "1.At least one digit(0-9)\n" + "2.One lower case letter(a-z)\n" + "3.One upper case letter(A-Z)\n" + "4.One special character(@,#,$,%,^,&,+,=)\n" + "5.No spaces\n" + "6.Between 8 to 20 characters";
        private static final String PASSWORD_MUST_CONTAIN = "Password must contain:";
        private static final String USER_CREATION_TITLE = "User creation dialog";
        private static final String USER_CREATION_FAILED_TITLE = "User creation failed";

        // Static String values which holds regular expression for a valid first and last name
        private static final String VALID_FIRST_AND_LAST_NAME = "^[a-zA-Z]{2,15}";
        // Static String values which holds regular expression for a valid password value (the restrictions can be seen above)
        private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        // Static int value to hold the minimum characters limit
        private static final int FIRST_AND_LAST_NAME_MIN_CHARACTERS_LIMIT = 2;
        // Static int value to hold the maximum characters limit
        private static final int FIRST_AND_LAST_NAME_MAX_CHARACTERS_LIMIT = 15;

        /**
         * A constructor for a new javafx SubWindow
         *
         * @param insets Nullable Insets margins for the window
         * @param vGap   Nullable integer for the vertical gap between rows
         * @param hGap   Nullable integer for the horizontal gap between rows
         */
        public SubWindow(@Nullable Insets insets, @Nullable Integer vGap, @Nullable Integer hGap) {

            super(insets, vGap, hGap);

            // Set the TextFields validation fields to true as default value
            firstNameValidation = true;
            lastNameValidation = true;
            emailValidation = true;
            passwordValidation = true;

            // Set the mouse event handler to clear the specified controls if they are not valid by clicking on them
            clearErrorFields = (MouseEvent event) -> {
                if (!firstNameValidation || !lastNameValidation || !emailValidation || !passwordValidation) {
                    pfPassword.clear();
                }
                firstNameValidation = true;
                lastNameValidation = true;
                emailValidation = true;
                passwordValidation = true;
            };
        }

        @Override
        public ui.MainWindow.SubWindow createConstraints(int rowCount) {

            super.createConstraints(rowCount);

            return this;
        }

        /**
         * A method which creates the FirstName row consisting of a two text controls
         * and one TextField control
         *
         * @return The window which the function was called on
         */
        public ui.MainWindow.SubWindow createFirstNameRow() {
            tFirstName = new Text(FIRST_NAME + COLON);
            tFirstName.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
            add(tFirstName, 0, 0);

            tfFirstName = new TextField();
            tfFirstName.setPromptText(ENTER_FIRST_NAME);
            add(tfFirstName, 1, 0);
            tfFirstName.addEventHandler(KeyEvent.KEY_RELEASED, this::checkCredentials);
            tfFirstName.addEventHandler(MouseEvent.MOUSE_RELEASED, clearErrorFields);

            tFirstNameError = new Text();
            tFirstNameError.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, ERROR_FIELD_FONT_SIZE));
            tFirstNameError.setFill(Color.RED);
            add(tFirstNameError, 0, 1, 2, 1);

            GridPane.setHalignment(tFirstName, HPos.RIGHT);
            GridPane.setHalignment(tfFirstName, HPos.LEFT);
            GridPane.setHalignment(tFirstNameError, HPos.CENTER);

            return this;
        }

        /**
         * A method which creates the LastName row consisting of a two text controls
         * and one TextField control
         *
         * @return The window which the function was called on
         */
        public ui.MainWindow.SubWindow createLastNameRow() {
            tLastName = new Text(LAST_NAME + COLON);
            tLastName.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
            add(tLastName, 0, 2);

            tfLastName = new TextField();
            tfLastName.setPromptText(ENTER_LAST_NAME);
            add(tfLastName, 1, 2);
            tfLastName.addEventHandler(KeyEvent.KEY_RELEASED, this::checkCredentials);
            tfLastName.addEventHandler(MouseEvent.MOUSE_RELEASED, clearErrorFields);

            tLastNameError = new Text();
            tLastNameError.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, ERROR_FIELD_FONT_SIZE));
            tLastNameError.setFill(Color.RED);
            add(tLastNameError, 0, 3, 2, 1);

            GridPane.setHalignment(tLastName, HPos.RIGHT);
            GridPane.setHalignment(tfLastName, HPos.LEFT);
            GridPane.setHalignment(tLastNameError, HPos.CENTER);

            return this;
        }

        @Override
        public ui.MainWindow.SubWindow createEmailRow() {
            tEmail = new Text(EMAIL + COLON);
            tEmail.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
            add(tEmail, 0, 4);

            tfEmail = new TextField();
            tfEmail.setPromptText(ENTER_EMAIL);
            add(tfEmail, 1, 4);
            tfEmail.addEventHandler(KeyEvent.KEY_RELEASED, this::checkCredentials);
            tfEmail.addEventHandler(MouseEvent.MOUSE_RELEASED, clearErrorFields);

            tEmailError = new Text();
            tEmailError.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, ERROR_FIELD_FONT_SIZE));
            tEmailError.setFill(Color.RED);
            add(tEmailError, 0, 5, 2, 1);

            GridPane.setHalignment(tEmail, HPos.RIGHT);
            GridPane.setHalignment(tfEmail, HPos.LEFT);
            GridPane.setHalignment(tEmailError, HPos.CENTER);

            return this;
        }

        @Override
        public ui.MainWindow.SubWindow createPasswordRow() {
            tPassword = new Text(PASSWORD + COLON);
            tPassword.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
            add(tPassword, 0, 6);

            pfPassword = new PasswordField();
            pfPassword.setPromptText(ENTER_PASSWORD);
            add(pfPassword, 1, 6);
            pfPassword.addEventHandler(KeyEvent.KEY_RELEASED, this::checkCredentials);
            pfPassword.addEventHandler(MouseEvent.MOUSE_RELEASED, clearErrorFields);

            tPasswordError = new Text();
            tPasswordError.setFont(Font.font("David", FontWeight.NORMAL, FontPosture.REGULAR, ERROR_FIELD_FONT_SIZE));
            tPasswordError.setFill(Color.RED);
            add(tPasswordError, 0, 7, 2, 1);

            GridPane.setHalignment(tPassword, HPos.RIGHT);
            GridPane.setHalignment(pfPassword, HPos.LEFT);
            GridPane.setHalignment(tPasswordError, HPos.CENTER);

            return this;
        }

        @Override
        public ui.MainWindow.SubWindow createSubmitButton() {
            btnSubmit = new Button(SUBMIT);
            add(btnSubmit, 0, 8);
            GridPane.setHalignment(btnSubmit, HPos.RIGHT);
            btnSubmit.addEventHandler(MouseEvent.MOUSE_RELEASED, this::checkCredentials);

            return this;
        }

        /**
         * A method which checks the the TextField controls for a valid user input
         *
         * @param event The InputEvent which caused this function to activate
         */
        private void checkCredentials(final InputEvent event) {
            // Each button pressed while hovering the TextField controls cause this if to be checked
            // however only an Enter press or click on the submit button should proceed
            if (event instanceof KeyEvent && ((KeyEvent) event).getCode() == KeyCode.ENTER || event instanceof MouseEvent) {
                String firstName = tfFirstName.getText();
                String lastName = tfLastName.getText();
                String email = tfEmail.getText();
                String password = pfPassword.getText();
                if (firstName.length() == 0) {
                    tFirstNameError.setText(String.format(MUST_CONTAIN_VALUE_ERROR, FIRST_NAME));
                    firstNameValidation = false;
                } else if (!firstName.matches(VALID_FIRST_AND_LAST_NAME)) {
                    if (firstName.length() > 15) {
                        tFirstNameError.setText(String.format(MUST_NOT_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MAX_CHARACTERS_LIMIT));
                        firstNameValidation = false;
                    } else if (firstName.length() < 3) {
                        tFirstNameError.setText(String.format(MUST_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MIN_CHARACTERS_LIMIT));
                        firstNameValidation = false;
                    } else {
                        tFirstNameError.setText(String.format(MUST_CONTAIN_LETTERS_ONLY, FIRST_NAME));
                        firstNameValidation = false;
                    }
                }

                if (lastName.length() == 0) {
                    tLastNameError.setText(String.format(MUST_CONTAIN_VALUE_ERROR, LAST_NAME));
                    lastNameValidation = false;
                } else if (!lastName.matches(VALID_FIRST_AND_LAST_NAME)) {
                    if (lastName.length() > 15) {
                        tLastNameError.setText(String.format(MUST_NOT_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MAX_CHARACTERS_LIMIT));
                        lastNameValidation = false;
                    } else if (lastName.length() < 3) {
                        tLastNameError.setText(String.format(MUST_BE_LONGER_THAN, FIRST_AND_LAST_NAME_MIN_CHARACTERS_LIMIT));
                        lastNameValidation = false;
                    } else {
                        tLastNameError.setText(String.format(MUST_CONTAIN_LETTERS_ONLY, LAST_NAME));
                        lastNameValidation = false;
                    }
                }

                if (email.length() == 0) {
                    tEmailError.setText(String.format(MUST_CONTAIN_VALUE_ERROR, EMAIL));
                    emailValidation = false;
                } else if (!VALID_EMAIL_ADDRESS.matcher(email).find()) {
                    tEmailError.setText(String.format(EMAIL_DOES_NOT_MATCH_REGEX, EMAIL));
                    emailValidation = false;
                }

                if (password.length() == 0) {
                    pfPassword.clear();
                    tPasswordError.setText(String.format(MUST_CONTAIN_VALUE_ERROR, PASSWORD));
                    passwordValidation = false;
                } else if (!password.matches(PASSWORD_REGEX)) {
                    pfPassword.clear();

                    taPasswordRules = new TextArea(PASSWORD_RULES_BODY);
                    taPasswordRules.setEditable(false);

                    new MyDialog(PASSWORD_RULES_TITLE, PASSWORD_MUST_CONTAIN, PASSWORD_RULES_BODY, null).showAndWait();

                    passwordValidation = false;
                }
                // If the fields are valid send a registration request to the server which returns
                // a response whether the user was created successfully or not
                // if it was successful close the SubWindow and return to the MainWindow
                // if it was not successful display the error to the user
                if (firstNameValidation && lastNameValidation && emailValidation && passwordValidation) {
                    tFirstNameError.setText(NO_ERROR);
                    tLastNameError.setText(NO_ERROR);
                    tEmailError.setText(NO_ERROR);
                    tPasswordError.setText(NO_ERROR);

                    firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                    lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                }
                try {
                    IUser user = new User(firstName, lastName, email, password);
                    IContainer container = new Container<IUser>(CREATE_USER_REQUEST, user);
                    logger.info(container.toString());

                    if (!container.getResult()) {
                        IUser temp = ((IUser) container.getObject());
                        if (temp.getFirstNameError() != null && temp.getFirstNameError().length() > 0) {
                            tFirstNameError.setText(temp.getFirstNameError());
                            firstNameValidation = false;
                        }
                        if (temp.getLastNameError() != null && temp.getLastNameError().length() > 0) {
                            tLastNameError.setText(temp.getLastNameError());
                            lastNameValidation = false;
                        }
                        if (temp.getEmailError() != null && temp.getEmailError().length() > 0) {
                            tEmailError.setText(temp.getEmailError());
                            emailValidation = false;
                        }
                        if (temp.getPasswordError() != null && temp.getPasswordError()) {
                            pfPassword.clear();

                            taPasswordRules = new TextArea(PASSWORD_RULES_BODY);
                            taPasswordRules.setEditable(false);

                            new MyDialog(PASSWORD_RULES_TITLE, PASSWORD_MUST_CONTAIN, PASSWORD_RULES_BODY, null).showAndWait();

                            passwordValidation = false;
                        }
                    } else {
                        new MyDialog(container, USER_CREATION_TITLE).showAndWait();
                        ((Stage) getScene().getWindow()).hide();
                    }

                } catch (IOException | InterruptedException | ExecutionException | ClassCastException e) {
                    if (e instanceof IOException) {
                        dialog = new MyDialog(SERVER_ERROR_TITLE, SERVER_ERROR_TITLE, SERVER_ERROR_BODY, null);
                        dialog.showAndWait();
                        ((Stage) getScene().getWindow()).hide();
                    } else {
                        dialog = new MyDialog(USER_CREATION_TITLE, USER_CREATION_FAILED_TITLE, UNKNOWN_ERROR_BODY, null);
                        dialog.showAndWait();
                        ((Stage) getScene().getWindow()).hide();
                    }
                    logger.info(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}

