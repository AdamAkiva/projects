package com.akiva.adam.finalproject.activities;

import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.uiElements.MyDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.net.ConnectException;

import javax.inject.Inject;

/**
 * This activity is for a new user to sign up
 */
public class SignupActivity extends MyActivity {

    // The database instance
    @Inject
    public Database database;
    private IDatabase mDatabase;

    // Controls for this activity
    private RelativeLayout rlSignupActivity;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private CheckBox cbBelowEighteen;
    private Button btnSubmit;
    private ImageView ivPasswordRules;
    private ProgressBar pbLogin;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Boolean values to reset the fields if they have an error
    private boolean validFirstName = false;
    private boolean validLastName = false;
    private boolean validEmail = false;
    private boolean validPassword = false;
    private boolean validConfirmPassword = false;

    private Runnable signUpRunnable;
    private Runnable showLoading;
    private Runnable hideLoading;

    private Handler handler;

    // Static variable used for logging
    private static final String TAG = SignupActivity.class.getName();

    /**
     * A method that runs when the activity is started
     * mainly used to create the visual look of the
     * login screen
     *
     * @param savedInstanceState Parameters to recover when the activity is destroyed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);
        mDatabase = database;

        handler = new Handler(Looper.getMainLooper());

        try {
            // Checks whether the user is already logged in, and if so
            // move to the ImageListActivity
            if (mDatabase.getAuthService().getCurrentUser() != null) {
                goToActivity(SignupActivity.this, ImageListActivity.class, null);
            }

            // Checks whether the user was sent back to this activity
            // with an error message, and if so display the error in a toast
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TOAST_MESSAGE)) {
                AlertToast.makeText(SignupActivity.this, getIntent().getExtras().getString(TOAST_MESSAGE), Toast
                        .LENGTH_LONG).show();
            }

            setContentView(R.layout.activity_signup);

            showLoading = () -> pbLogin.setVisibility(View.VISIBLE);
            hideLoading = () -> pbLogin.setVisibility(View.GONE);

            signUpRunnable = () -> {
                try {
                    runOnUiThread(showLoading);
                    mDatabase.getAuthService().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            (@NonNull Task<AuthResult> task) -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, TAG + ": Signup successful");
                            try {
                                final CharSequence[] userInfo = {firstName, lastName, email, password};
                                mDatabase.getAuthService().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener((@NonNull Task<AuthResult> task2) -> {
                                    if (task2.isSuccessful()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putCharSequenceArray(USER_INFO, userInfo);
                                        goToActivity(SignupActivity.this, ImageListActivity.class, bundle);
                                    }
                                });
                            } catch (ConnectException | NullPointerException e) {
                                pbLogin.setVisibility(View.GONE);
                                if (e instanceof ConnectException) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(TOAST_MESSAGE, getString(R.string.loginError));
                                    MyActivity.displayError(TAG, e, null, null, null);
                                    goToActivity(SignupActivity.this, MainActivity.class, bundle);
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(TOAST_MESSAGE, getString(R.string.unknownError));
                                    MyActivity.displayError(TAG, e, null, null, null);
                                    goToActivity(SignupActivity.this, MainActivity.class, bundle);
                                }
                            }
                        } else {
                            runOnUiThread(hideLoading);
                            Log.d(TAG, TAG + ": Signup failed");
                            AlertToast.makeText(SignupActivity.this, getString(R.string.signupError, getString(R
                                    .string.noInternetConnection)), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (ConnectException | NullPointerException e) {
                    runOnUiThread(hideLoading);
                    Log.d(TAG, String.format("%s:%s", TAG, e.getMessage()));
                    e.printStackTrace();
                    if (e instanceof ConnectException) {
                        AlertToast.makeText(SignupActivity.this, getString(R.string.noInternetConnection), Toast
                                .LENGTH_LONG).show();
                    } else {
                        AlertToast.makeText(SignupActivity.this, getString(R.string.unknownError), Toast.LENGTH_LONG)
                                .show();
                    }
                }
            };

            rlSignupActivity = (RelativeLayout) findViewById(R.id.rlSignupActivity);

            etFirstName = (EditText) findViewById(R.id.etFirstName);
            etLastName = (EditText) findViewById(R.id.etLastName);
            etEmail = (EditText) findViewById(R.id.etEmail);
            etPassword = (EditText) findViewById(R.id.etPassword);
            etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
            cbBelowEighteen = (CheckBox) findViewById(R.id.cbBelowEighteen);
            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            pbLogin = (ProgressBar) findViewById(R.id.pbLogin);

            // An ImageView used to display to the user the password rules
            ivPasswordRules = (ImageView) findViewById(R.id.ivPasswordRules);
            ivPasswordRules.bringToFront();

            // Recover the information if it is available in the savedInstanceBundle
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(FIRST_NAME)) {
                    etFirstName.setText(savedInstanceState.getString(FIRST_NAME));
                    validFirstName = validateField(etFirstName, FIRST_AND_LAST_NAME_MIN_LIMIT,
                            FIRST_AND_LAST_NAME_MAX_LIMIT, LETTERS_AND_SPACES_ONLY_REGEX, FIRST_NAME);
                }
                if (savedInstanceState.containsKey(LAST_NAME)) {
                    etLastName.setText(savedInstanceState.getString(LAST_NAME));
                    validFirstName = validateField(etLastName, FIRST_AND_LAST_NAME_MIN_LIMIT,
                            FIRST_AND_LAST_NAME_MAX_LIMIT, LETTERS_AND_SPACES_ONLY_REGEX, FIRST_NAME);
                }
                if (savedInstanceState.containsKey(EMAIL)) {
                    etEmail.setText(savedInstanceState.getString(EMAIL));
                    validEmail = validateField(etEmail, null, null, null, EMAIL);
                }
                if (savedInstanceState.containsKey(PASSWORD)) {
                    etPassword.setText(savedInstanceState.getString(PASSWORD));
                    validPassword = validateField(etPassword, PASSWORD_MIN_LIMIT, PASSWORD_MAX_LIMIT, PASSWORD_REGEX,
                            PASSWORD);
                }
                if (savedInstanceState.containsKey(CONFIRM_PASSWORD)) {
                    etConfirmPassword.setText(savedInstanceState.getString(CONFIRM_PASSWORD));
                    if (etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                        setFieldAsValid(etConfirmPassword);
                    }
                }
                if (savedInstanceState.containsKey(BELOW_EIGHTEEN)) {
                    cbBelowEighteen.setChecked(true);
                }
            }

            // Hide the keyboard on screen press
            rlSignupActivity.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (hasFocus) {
                    hideKeyboard(v, SignupActivity.this);
                }
            });

            // Setup listeners for the fields to be checked right as they entered
            // and display an image if they are valid or not
            etFirstName.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (!hasFocus && etFirstName.getText().toString().length() > 0) {
                    validFirstName = validateField(etFirstName, FIRST_AND_LAST_NAME_MIN_LIMIT,
                            FIRST_AND_LAST_NAME_MAX_LIMIT, LETTERS_AND_SPACES_ONLY_REGEX, FIRST_NAME);
                } else if (etFirstName.getText().toString().length() > 0 && !validFirstName) {
                    etFirstName.setText("");
                    etFirstName.setTextColor(getResources().getColor(R.color.black));
                }
            });

            etLastName.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (!hasFocus && etLastName.getText().toString().length() > 0) {
                    validLastName = validateField(etLastName, FIRST_AND_LAST_NAME_MIN_LIMIT,
                            FIRST_AND_LAST_NAME_MAX_LIMIT, LETTERS_AND_SPACES_ONLY_REGEX, LAST_NAME);
                } else if (etLastName.getText().toString().length() > 0 && !validLastName) {
                    etLastName.setText("");
                    etLastName.setTextColor(getResources().getColor(R.color.black));
                }
            });

            etEmail.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (!hasFocus && etEmail.getText().toString().length() > 0) {
                    validEmail = validateField(etEmail, null, null, null, EMAIL);
                } else if (etEmail.getText().toString().length() > 0 && !validEmail) {
                    etEmail.setText("");
                    etEmail.setTextColor(getResources().getColor(R.color.black));
                }
            });

            etPassword.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (hasFocus && etPassword.getText().toString().length() > 0 && !validPassword) {
                    etPassword.setText("");
                    etPassword.setTextColor(getResources().getColor(R.color.black));
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else if (!hasFocus && etPassword.getText().toString().length() > 0) {
                    validPassword = validateField(etPassword, PASSWORD_MIN_LIMIT, PASSWORD_MAX_LIMIT, PASSWORD_REGEX,
                            PASSWORD);
                    if (!validPassword) {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }
            });

            etConfirmPassword.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (hasFocus && etConfirmPassword.getText().toString().length() > 0 && !validConfirmPassword) {
                    etConfirmPassword.setText("");
                    etConfirmPassword.setTextColor(getResources().getColor(R.color.black));
                    etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                if (!hasFocus && etConfirmPassword.getText().toString().length() > 0 && etConfirmPassword.getText()
                        .toString().equals(etPassword.getText().toString())) {
                    setFieldAsValid(etConfirmPassword);
                    validConfirmPassword = true;
                } else if (!hasFocus) {
                    setFieldAsError(etConfirmPassword, getString(R.string.passwordsDoNotMatch));
                    etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    validConfirmPassword = false;
                }
            });
            etConfirmPassword.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etConfirmPassword.getText().toString().length() > 0 && etConfirmPassword.getText()
                            .toString().equals(etPassword.getText().toString())) {
                        setFieldAsValid(etConfirmPassword);
                        validConfirmPassword = true;
                    } else {
                        setFieldAsError(etConfirmPassword, getString(R.string.passwordsDoNotMatch));
                        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        validConfirmPassword = false;
                    }
                    rlSignupActivity.requestFocus();
                }
                return false;
            });

            ivPasswordRules.setOnClickListener((View v) -> {
                final MyDialog dialog = new MyDialog(SignupActivity.this);
                dialog.setTitle(getString(R.string.passwordRulesTitle), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody
                        (getString(R.string.passwordRulesBody), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton
                        (getString(R.string.gotIt), (View v2) -> dialog.dismiss()).show();
            });
            btnSubmit.setOnClickListener((View v) -> {
                if (validFirstName && validLastName && validEmail && validPassword && validConfirmPassword) {
                    if (!cbBelowEighteen.isChecked()) {
                        AlertToast.makeText(SignupActivity.this, getString(R.string.notChecked), Toast.LENGTH_LONG)
                                .show();
                    }
                    firstName = etFirstName.getText().toString();
                    lastName = etLastName.getText().toString();
                    email = etEmail.getText().toString();
                    password = etPassword.getText().toString();
                    new Thread(signUpRunnable).start();
                } else {
                    AlertToast.makeText(SignupActivity.this, getString(R.string.fixErrors), Toast.LENGTH_LONG).show();
                }
            });
        } catch (ConnectException e)

        {
            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
        }
    }

    /**
     * An inner thread class used to check the login credentials,
     * display a loading and login the user if the credentials
     * are valid
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (etFirstName.getText().toString().length() > 0) {
            outState.putString(FIRST_NAME, etFirstName.getText().toString());
        }
        if (etLastName.getText().toString().length() > 0) {
            outState.putString(LAST_NAME, etLastName.getText().toString());
        }
        if (etEmail.getText().toString().length() > 0) {
            outState.putString(EMAIL, etEmail.getText().toString());
        }
        if (etPassword.getText().toString().length() > 0) {
            outState.putString(PASSWORD, etPassword.getText().toString());
        }
        if (etConfirmPassword.getText().toString().length() > 0) {
            outState.putString(CONFIRM_PASSWORD, etConfirmPassword.getText().toString());
        }
        if (cbBelowEighteen.isChecked()) {
            outState.putBoolean(BELOW_EIGHTEEN, true);
        }
    }
}
