package com.akiva.adam.finalproject.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.classes.MyToolbar;
import com.akiva.adam.finalproject.classes.MyUser;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.akiva.adam.finalproject.uiElements.MyDialog;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.net.ConnectException;

import javax.inject.Inject;

/**
 * This activity allows the user to update his credentials
 */
public class UpdateProfileActivity extends MyActivity {

    // The database instance
    @Inject
    public Database database;
    private IDatabase mDatabase;

    // The current logged in user instance
    @Inject
    public MyUser user;
    private IUser mUser;

    // Custom made object for a toolbar
    private MyToolbar toolbar;

    private RelativeLayout rlUpdateProfileActivity;

    // Controls for this activity
    private EditText etFirstName;
    private EditText etLastName;
    private ImageView ivPasswordRules;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnSubmit;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Boolean values to reset the fields if they have an error
    private boolean validFirstName = true;
    private boolean validLastName = true;
    private boolean validEmail = true;
    private boolean validPassword = true;
    private boolean validConfirmPassword = false;

    private Handler handler;

    private static final String EMAIL_UPDATED_SUCCESSFULLY = "Email updated successfully";
    private static final String PASSWORD_UPDATED_SUCCESSFULLY = "Password updated successfully";

    // Static variable used for logging
    public static final String TAG = UpdateProfileActivity.class.getName();

    /**
     * A method that runs when the activity is started
     * mainly used to create the visual look of the
     * login screen
     *
     * @param savedInstanceState Parameters to recover when the activity is destroyed
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) getApplicationContext()).getDatabaseComponent().inject(this);

        mDatabase = database;
        mUser = user;

        handler = new Handler(Looper.getMainLooper());

        try {
            // Checks whether the user is logged in, if not return the user
            // to the login screen
            if (mUser == null && mDatabase.getAuthService().getCurrentUser() == null) {
                goToActivity(UpdateProfileActivity.this, MainActivity.class, null);
            }
            // Checks whether the user was sent back to this activity
            // with an error message, and if so display the error in a toast
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TOAST_MESSAGE)) {
                AlertToast.makeText(UpdateProfileActivity.this, getIntent().getExtras().getString(TOAST_MESSAGE),
                        Toast.LENGTH_LONG).show();
            }
            setContentView(R.layout.activity_update_profile);

            rlUpdateProfileActivity = (RelativeLayout) findViewById(R.id.rlUpdateProfileActivity);

            etFirstName = (EditText) findViewById(R.id.etFirstName);
            etLastName = (EditText) findViewById(R.id.etLastName);
            etEmail = (EditText) findViewById(R.id.etEmail);
            ivPasswordRules = (ImageView) findViewById(R.id.ivPasswordRules);
            etPassword = (EditText) findViewById(R.id.etPassword);
            etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
            btnSubmit = (Button) findViewById(R.id.btnSubmit);

            etFirstName.setText(mUser.getFirstName());
            etLastName.setText(mUser.getLastName());
            etEmail.setText(mUser.getEmail());
            etPassword.setText(mUser.getPassword());

            // Setup the toolbar for this activity
            toolbar = new MyToolbar(UpdateProfileActivity.this, (ViewGroup) rlUpdateProfileActivity);

            // Setup an error message for the toolbar alerting the user
            // that if he leaves the page his information will be lost
            toolbar.setCustomLogoutButtonEvent((View v) -> {
                if (!etFirstName.getText().toString().equals(mUser.getFirstName()) || !etLastName.getText()
                        .toString().equals(mUser.getLastName()) || !etEmail.getText().toString().equals(mUser
                        .getEmail()) || !etPassword.getText().toString().equals(mUser.getPassword())) {
                    final MyDialog dialog = new MyDialog(UpdateProfileActivity.this);
                    dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString
                            (R.string.loseAllInfo), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R
                            .string.yes), (View v2) -> {
                        try {
                            mUser.requestLogout();
                            dialog.dismiss();
                            goToActivity(UpdateProfileActivity.this, MainActivity.class, null);
                        } catch (ConnectException e) {
                            MyActivity.displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
                        }
                    }).setNegativeButton(getString(R.string.no), (View v2) -> dialog.dismiss()).show();
                } else {
                    try {
                        mUser.requestLogout();
                        goToActivity(UpdateProfileActivity.this, MainActivity.class, null);
                    } catch (ConnectException e) {
                        MyActivity.displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
                    }
                }
            });
            toolbar.setCustomSettingsButtonEvent((View v) -> {
                if (!etFirstName.getText().toString().equals(mUser.getFirstName()) || !etLastName.getText().toString
                        ().equals(mUser.getLastName()) || !etEmail.getText().toString().equals(mUser.getEmail()) ||
                        !etPassword.getText().toString().equals(mUser.getPassword())) {
                    final MyDialog dialog = new MyDialog(UpdateProfileActivity.this);
                    dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString
                            (R.string.loseAllInfo), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R
                            .string.yes), (View v2) -> {

                        dialog.dismiss();
                        goToActivity(UpdateProfileActivity.this, SettingsActivity.class, null);
                    }).setNegativeButton(getString(R.string.no), (View v2) -> dialog.dismiss()).show();
                } else {
                    goToActivity(UpdateProfileActivity.this, SettingsActivity.class, null);
                }
            });
            toolbar.setCustomHomeButtonEvent((View v) -> {
                        if (!etFirstName.getText().toString().equals(mUser.getFirstName()) || !etLastName.getText()
                                .toString().equals(mUser.getLastName()) || !etEmail.getText().toString().equals(mUser
                                .getEmail()) || !etPassword.getText().toString().equals(mUser.getPassword())) {
                            final MyDialog dialog = new MyDialog(UpdateProfileActivity.this);
                            dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody
                                    (getString(R.string.loseAllInfo), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton
                                    (getString(R.string.yes), (View v2) -> {

                                        dialog.dismiss();
                                        goToActivity(UpdateProfileActivity.this, ImageListActivity.class, null);
                                    }).setNegativeButton(getString(R.string.no), (View v2) -> dialog.dismiss()).show();
                        } else {
                            goToActivity(UpdateProfileActivity.this, ImageListActivity.class, null);
                        }
                    });
            // Recover the information if it is available in the savedInstanceBundle
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(FIRST_NAME)) {
                    etFirstName.setText(savedInstanceState.getString(FIRST_NAME));
                    validFirstName = validateField(etFirstName, FIRST_AND_LAST_NAME_MIN_LIMIT,
                            FIRST_AND_LAST_NAME_MAX_LIMIT, LETTERS_AND_SPACES_ONLY_REGEX, FIRST_NAME);
                }
                if (savedInstanceState.containsKey(LAST_NAME)) {
                    etLastName.setText(savedInstanceState.getString(LAST_NAME));
                    validLastName = validateField(etLastName, FIRST_AND_LAST_NAME_MIN_LIMIT,
                            FIRST_AND_LAST_NAME_MAX_LIMIT, LETTERS_AND_SPACES_ONLY_REGEX, LAST_NAME);
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
            }

            // Hide the keyboard on screen press
            rlUpdateProfileActivity.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (hasFocus) {
                    hideKeyboard(v, UpdateProfileActivity.this);
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

            ivPasswordRules.setOnClickListener((View v) -> {
                final MyDialog dialog = new MyDialog(UpdateProfileActivity.this);
                dialog.setTitle(getString(R.string.passwordRulesTitle), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody
                        (getString(R.string.passwordRulesBody), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton
                        (getString(R.string.gotIt), (View v2) -> dialog.dismiss()).show();
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
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (etConfirmPassword.getText().toString().length() > 0 && etConfirmPassword.getText()
                            .toString().equals(etPassword.getText().toString())) {
                        setFieldAsValid(etConfirmPassword);
                        validConfirmPassword = true;
                    } else {
                        setFieldAsError(etConfirmPassword, getString(R.string.passwordsDoNotMatch));
                        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        validConfirmPassword = false;
                    }
                    rlUpdateProfileActivity.requestFocus();
                }
                return false;
            });

            // On a valid submit update the information for the user and
            // logging him out
            btnSubmit.setOnClickListener((View v) -> {
                final MyDialog dialog = new MyDialog(UpdateProfileActivity.this);
                dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString(R
                        .string.changeInfoDialog), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R
                        .string.yes), (View v2) -> {
                    if (validFirstName && validLastName && validEmail && validPassword && validConfirmPassword) {
                        dialog.setProgressBar();
                        AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), mUser
                                .getPassword());
                        firstName = etFirstName.getText().toString();
                        lastName = etLastName.getText().toString();
                        email = etEmail.getText().toString();
                        password = etPassword.getText().toString();
                        try {
                            mDatabase.getAuthService().getCurrentUser().reauthenticate(credential)
                                    .addOnCompleteListener((@NonNull Task<Void> task) -> {
                                try {
                                    if (task.isSuccessful()) {
                                        mUser.setFirstName(firstName);
                                        mUser.setLastName(lastName);
                                        mDatabase.getAuthService().getCurrentUser().updateEmail(email)
                                                .addOnCompleteListener((@NonNull Task<Void> task2) -> {
                                            try {
                                                if (task2.isSuccessful()) {
                                                    mUser.setEmail(email);
                                                    Log.d(TAG, String.format("%s:%s", TAG, EMAIL_UPDATED_SUCCESSFULLY));
                                                    mDatabase.getAuthService().getCurrentUser().updatePassword
                                                            (password).addOnCompleteListener((@NonNull Task<Void> task3) -> {
                                                        try {
                                                            if (task3.isSuccessful()) {
                                                                mUser.setPassword(password);
                                                                Log.d(TAG, String.format("%s:%s", TAG,
                                                                        PASSWORD_UPDATED_SUCCESSFULLY));
                                                                mUser.requestLogout();
                                                                dialog.dismiss();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString(TOAST_MESSAGE, getString(R.string
                                                                        .changesSuccessfulPleaseRelog));
                                                                goToActivity(UpdateProfileActivity.this, MainActivity
                                                                        .class, bundle);
                                                            }
                                                        } catch (ConnectException e) {
                                                            displayError(TAG, e, this, getString(R.string
                                                                    .noInternetConnection), handler);
                                                        }
                                                    });
                                                }
                                            } catch (ConnectException e) {
                                                displayError(TAG, e, this, getString(R.string.noInternetConnection),
                                                        handler);
                                            }
                                        });
                                    }
                                } catch (ConnectException e) {
                                    displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
                                }
                            });
                        } catch (ConnectException e) {
                            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
                        }
                    } else {
                        dialog.dismiss();
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                        AlertToast.makeText(UpdateProfileActivity.this, getString(R.string.fixErrors), Toast
                                .LENGTH_LONG).show();
                    }
                }).

                        setNegativeButton(getString(R.string.no), (View v2) -> dialog.dismiss()).

                        show();
            });
        } catch (ConnectException e) {
            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
        }
    }

    /**
     * An inner thread class used to check the login credentials,
     * display a loading and login the user if the credentials
     * are valid
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
    }

    @Override
    public void onBackPressed() {
        if (!etFirstName.getText().toString().equals(mUser.getFirstName()) || !etLastName.getText().toString().equals
                (mUser.getLastName()) || !etEmail.getText().toString().equals(mUser.getEmail()) || !etPassword
                .getText().toString().equals(mUser.getPassword())) {
            final MyDialog dialog = new MyDialog(UpdateProfileActivity.this);
            dialog.setTitle(getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(getString(R
                    .string.loseAllInfo), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(getString(R.string.yes),
                    (View v2) -> {
                dialog.dismiss();
                goToActivity(this, MainActivity.class, null);
            }).setNegativeButton(getString(R.string.no), (View v2) -> dialog.dismiss()).show();
        } else {
            super.onBackPressed();
        }
    }
}
