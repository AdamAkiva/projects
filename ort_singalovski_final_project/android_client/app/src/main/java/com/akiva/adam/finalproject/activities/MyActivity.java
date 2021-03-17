package com.akiva.adam.finalproject.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.uiElements.AlertToast;

/**
 * This activity does not have visualization to the user,
 * it is used to set values and function that all the
 * application activities can use
 */
public class MyActivity extends AppCompatActivity {

    // Static values used by the different activities
    public static final String LETTERS_AND_SPACES_ONLY_REGEX = "^[a-z A-Z]{3,15}";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
    public static final int FIRST_AND_LAST_NAME_MIN_LIMIT = 3;
    public static final int FIRST_AND_LAST_NAME_MAX_LIMIT = 15;
    public static final int PASSWORD_MIN_LIMIT = 8;
    public static final int PASSWORD_MAX_LIMIT = 20;
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String BELOW_EIGHTEEN = "belowEighteen";
    public static final String IMAGES = "images";
    public static final String TOAST_MESSAGE = "toastError";
    public static final long TWO_MEGABYTES = 2048 * 2048;
    public static final String VIEWED = "viewed";
    public static final String KEY = "key";
    public static final String USERS = "users";
    public static final String LOGGED_IN = "loggedIn";
    public static final String USER_INFO = "userInfo";
    public static final String CHANNEL_ID = "0";
    public static final int NOTIFICATION_ID = 0;
    public static final String SETTINGS = "settings";
    public static final String NOTIFICATIONS = "notifications";
    public static final int DEFAULT_DIALOG_TITLE_TEXT_SIZE = 24;
    public static final int DEFAULT_DIALOG_BODY_TEXT_SIZE = 15;
    public static final String UNDEFINED = "Undefined";
    public static final String FILE_NAME = "fileName";
    public static final String VIEW_DATE = "viewDate";
    public static final String VIEW_TIME = "viewTime";

    // Static variable used for logging
    private static final String TAG = MyActivity.class.getName();

    /**
     * A method used to hide the keyboard
     *
     * @param view     Activity main view
     * @param activity Activity object
     */
    public void hideKeyboard(View view, Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * A method used to check the validation of a specific
     * control
     *
     * @param editText  Non null field to be checked
     * @param minLimit  Nullable character minimum limit
     * @param maxLimit  Nullable character maximum limit
     * @param regex     Nullable regex if needed
     * @param fieldName Nullable field name for classification
     * @return Boolean value indicating whether the field is valid or not
     */
    public boolean validateField(@NonNull EditText editText, @Nullable Integer minLimit, @Nullable Integer maxLimit,
                                 @Nullable String regex, @Nullable String fieldName) {
        String text = editText.getText().toString();
        if (text.length() == 0) {
            setFieldAsError(editText, getString(R.string.missingField));
            return false;
        } else if (minLimit != null && text.length() < minLimit) {
            setFieldAsError(editText, getString(R.string.tooShort, minLimit));
            return false;
        } else if (maxLimit != null && text.length() > maxLimit) {
            setFieldAsError(editText, getString(R.string.tooLong, maxLimit));
            return false;
        } else if (regex != null && fieldName != null && !text.matches(regex)) {
            switch (fieldName) {
                case FIRST_NAME:
                case LAST_NAME:
                    setFieldAsError(editText, getString(R.string.invalidField, getString(R.string
                            .lettersAndSpacesOnly)));
                    break;
                case PASSWORD:
                    setFieldAsError(editText, getString(R.string.validPassword));
                    break;
            }
            return false;
        } else if (fieldName != null && fieldName.equals(EMAIL) && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            setFieldAsError(editText, getString(R.string.invalidField, getString(R.string.validEmail)));
            return false;
        } else {
            setFieldAsValid(editText);
            return true;
        }
    }


    /**
     * A method used to set a field as invalid
     *
     * @param editText  Non-null field used to set as invalid
     * @param errorText Nullable text to describe the error
     */
    public void setFieldAsError(@NonNull EditText editText, @Nullable String errorText) {
        editText.setText(errorText);
        editText.setTextColor(getResources().getColor(R.color.red));
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_error),
                null);
    }

    /**
     * A method used to set a field as valid
     *
     * @param editText Non-null field used to set as valid
     */
    public void setFieldAsValid(@NonNull EditText editText) {
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_valid),
                null);
    }

    /**
     * A method used to pass between different activities
     *
     * @param currentContext Non-null current activity context
     * @param destActivity   Non-null target activity
     * @param bundle         Nullable Bundle
     */
    public void goToActivity(@NonNull Activity currentContext, @NonNull Class destActivity, @Nullable Bundle bundle) {
        Intent intent = new Intent(currentContext, destActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * A method to finish an activity because of an exception
     *
     * @param TAG             String indicating to Logger tag
     * @param e               Exception that caused for the activity to finish
     * @param activity        Nullable calling activity
     * @param toastMessage    Nullable String displaying error to the user
     * @param activityHandler Nullable activity's handler
     */
    public static void displayError(String TAG, Exception e, @Nullable Activity activity, @Nullable String
            toastMessage, @Nullable Handler activityHandler) {
        Log.d(TAG, String.format("%s:%s", TAG, e.getMessage()));
        e.printStackTrace();
        if (activity != null && toastMessage != null) {
            AlertToast.makeText(activity, toastMessage, Toast.LENGTH_LONG).show();
        }
        if (activity != null && activityHandler != null) {
            activityHandler.postDelayed(activity::finishAffinity, Toast.LENGTH_LONG);
        }
    }

}

