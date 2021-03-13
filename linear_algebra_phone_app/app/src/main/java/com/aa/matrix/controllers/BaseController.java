package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseController {

    public static final String OPERATION = "Operation: ";

    public static final CharSequence INPUT_VALUES = "Please input rows and cols values";
    public static final CharSequence NOT_A_MATRIX = "Please input a matrix, not a vector";
    public static final String ROWS_INVALID = "Between 2 to 5";
    public static final String COLS_INVALID = "Between 2 to 5";
    public static final String EMPTY_STRING = "";
    public static final CharSequence SMART_ASS = "Why are you a smartass?";
    public static final CharSequence MATRIX_MUST_BE_FULL = "Matrix must be full";
    public static final CharSequence VECTOR_MUST_BE_FULL = "Vector must be full";

    public static final int DETERMINANT = 1;
    public static final int GAUSS_JORDAN = 2;
    public static final int INVERSE_MATRIX = 3;

    private static final int MAXIMUM_THREAD_NUMBER = 5;
    private static final ExecutorService service = Executors.newFixedThreadPool(MAXIMUM_THREAD_NUMBER);

    public ExecutorService getService() {
        return service;
    }

    public View.OnFocusChangeListener buildHideKeyboardListener(final View view, final Activity activity) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(view, activity);
                }
            }
        };
    }

    private void hideKeyboard(View view, Context activityContext) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activityContext.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void goToResultActivity(Context context, Class<?> activityToStart, String key, int value) {
        Intent intent = new Intent(context, activityToStart);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }
}
