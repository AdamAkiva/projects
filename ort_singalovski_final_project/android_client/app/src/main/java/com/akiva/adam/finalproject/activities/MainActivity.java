package com.akiva.adam.finalproject.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IDatabase;
import com.akiva.adam.finalproject.notifications.NotificationService;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.akiva.adam.finalproject.classes.Database;
import com.akiva.adam.finalproject.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.net.ConnectException;

import javax.inject.Inject;


/**
 * The main activity which opens why you run the application
 * contains a login screen with email and password
 */
public class MainActivity extends MyActivity {

    // The database instance
    @Inject
    public Database database;
    private IDatabase mDatabase;

    // Controls for this activity
    private RelativeLayout rlMainActivity;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSubmit;
    private Button btnSignup;
    private ProgressBar pbLogin;

    // Variables which contains the email and password values
    private String email;
    private String password;

    private Runnable showLoading;
    private Runnable hideLoading;
    private Runnable loginRunnable;

    private Handler handler;

    // Static variable used for logging
    private static final String TAG = MainActivity.class.getName();

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

        handler = new Handler(getMainLooper());

        try {
            // Checks whether the user is already logged in, and if so
            // move to the ImageListActivity
            if (mDatabase.getAuthService().getCurrentUser() != null) {
                goToActivity(MainActivity.this, ImageListActivity.class, null);
            }
            // Checks whether the user was sent back to this activity
            // with an error message, and if so display the error in a toast
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TOAST_MESSAGE)) {
                AlertToast.makeText(MainActivity.this, getIntent().getExtras().getString(TOAST_MESSAGE), Toast
                        .LENGTH_LONG).show();
            }

            setContentView(R.layout.activity_main);

            showLoading = () -> pbLogin.setVisibility(View.VISIBLE);
            hideLoading = () -> pbLogin.setVisibility(View.GONE);

            // Runnable implementation for login action
            loginRunnable = () -> {
                try {
                    runOnUiThread(showLoading);
                    mDatabase.getAuthService().signInWithEmailAndPassword(email, password).addOnCompleteListener(
                            (@NonNull Task<AuthResult> task) -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, TAG + ": Login successful");
                            goToActivity(MainActivity.this, ImageListActivity.class, null);
                        } else {
                            Log.d(TAG, TAG + ": Login failed");
                            AlertToast.makeText(MainActivity.this, getString(R.string.oneOrMoreFieldsIsIncorrect),
                                    Toast.LENGTH_LONG).show();
                            runOnUiThread(hideLoading);
                        }
                    });
                } catch (ConnectException | NullPointerException e) {
                    runOnUiThread(hideLoading);
                    MyActivity.displayError(TAG, e, null, null, null);
                    if (e instanceof ConnectException) {
                        AlertToast.makeText(MainActivity.this, getString(R.string.noInternetConnection), Toast
                                .LENGTH_LONG).show();
                    } else {
                        AlertToast.makeText(MainActivity.this, getString(R.string.unknownError), Toast.LENGTH_LONG)
                                .show();
                    }
                    runOnUiThread(hideLoading);
                }
            };

            rlMainActivity = (RelativeLayout) findViewById(R.id.rlMainActivity);
            etEmail = (EditText) findViewById(R.id.etEmail);
            etPassword = (EditText) findViewById(R.id.etPassword);
            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            btnSignup = (Button) findViewById(R.id.btnSignup);
            pbLogin = (ProgressBar) findViewById(R.id.pbLogin);

            // Recover the values from the savedBundleInstance (if they exist)
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(EMAIL)) {
                    etEmail.setText(savedInstanceState.getString(EMAIL));
                }
                if (savedInstanceState.containsKey(PASSWORD)) {
                    etPassword.setText(savedInstanceState.getString(PASSWORD));
                }
            }

            // Hide the keyboard on screen press
            rlMainActivity.setOnFocusChangeListener((View v, boolean hasFocus) -> {
                if (hasFocus) {
                    hideKeyboard(v, MainActivity.this);
                }
            });

            btnSubmit.setOnClickListener((View v) -> {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (email.length() == 0 || password.length() == 0) {
                    AlertToast.makeText(MainActivity.this, getString(R.string.mustHaveValues), Toast.LENGTH_LONG)
                            .show();
                } else {
                    new Thread(loginRunnable).start();
                }
            });
            btnSignup.setOnClickListener((View v) -> {
                goToActivity(MainActivity.this, SignupActivity.class, null);
            });
        } catch (ConnectException e) {
            displayError(TAG, e, this, getString(R.string.noInternetConnection), handler);
        }
    }

    /**
     * A method used to save the email and password values
     * when the activity is destroyed
     *
     * @param outState Bundle in which to save the values
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (etEmail != null && etEmail.getText().toString().length() > 0) {
            outState.putString(EMAIL, etEmail.getText().toString());
        }
        if (etPassword != null && etPassword.getText().toString().length() > 0) {
            outState.putString(PASSWORD, etPassword.getText().toString());
        }
    }

    /**
     * A method which causes the return button on
     * this activity to do nothing
     */
    @Override
    public void onBackPressed() {
        // do nothing
    }
}
