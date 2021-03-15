package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Adam Akiva
 * Base class for all the controllers
 */
public abstract class BaseController {

    // String constants for user output
    public static final String OPERATION = "Operation: ";
    public static final CharSequence INPUT_VALUES = "Please input rows and cols values";
    public static final CharSequence NOT_A_MATRIX = "Please input a matrix, not a vector";
    public static final String ROWS_INVALID = "Between 2 to 5";
    public static final String COLS_INVALID = "Between 2 to 5";
    public static final String EMPTY_STRING = "";
    public static final CharSequence SMART_ASS = "Why are you a smartass?";
    public static final CharSequence MATRIX_MUST_BE_FULL = "Matrix must be full";
    public static final CharSequence VECTOR_MUST_BE_FULL = "Vector must be full";

    // Thread factory for the calculation threads
    private static final int MAXIMUM_THREAD_NUMBER = 5;
    private static final ExecutorService service = Executors.newFixedThreadPool(MAXIMUM_THREAD_NUMBER);

    /**
     * @return ExecutorService to run threads on
     */
    public ExecutorService getService() {
        return service;
    }

    /**
     * @param view Parent view to get android.os.IBinder windowToken from
     * @param context Context to get System Service from
     * @return View.OnFocusChangeListener to hide the keyboard on focus
     */
    public View.OnFocusChangeListener buildHideKeyboardListener(final View view, final Context context) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(view, context);
                }
            }
        };
    }

    /**
     * @param view Parent view to get android.os.IBinder windowToken from
     * @param context Context to get System Service from
     */
    private void hideKeyboard(View view, Context context) {
        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * @param context A Context of the application package implementing this class
     * @param activityToStart Which activity to start
     */
    protected void goToResultActivity(Context context, Class<?> activityToStart) {
        Intent intent = new Intent(context, activityToStart);
        context.startActivity(intent);
    }
}
