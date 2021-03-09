package com.aa.matrix.views;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int MAXIMUM_THREAD_NUMBER = 5;
    private static final ExecutorService service = Executors.newFixedThreadPool(MAXIMUM_THREAD_NUMBER);

    public static final int ERROR_TEXT_VIEW_TOP_MARGIN = 20;
    public static final int ERROR_TEXT_VIEW_BOTTOM_MARGIN = 20;
    public static final int ERROR_TEXT_VIEW_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int ERROR_TEXT_VIEW_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT;
    public static final float ERROR_TEXT_VIEW_TEXT_SIZE = 14f;
    public static final int EDIT_TEXT_WIDTH = 100;
    public static final int EDIT_TEXT_HEIGHT = 40;
    public static final int EDIT_TEXT_START_MARGIN = 40;
    public static final float EDIT_TEXT_SIZE = 13f;
    public static final int EDIT_TEXT_MAX_LENGTH = 5;
    public static final float BUTTON_TEXT_SIZE = 14f;
    public static final int ZERO = 0;

    public static final String CANNOT_INVERSE_MATRIX = "Cannot inverse matrix";
    public static final String PARENT_VIEW_MUST_BE_RELATIVE_LAYOUT = "Parent view must be relative" +
            "layout";
    public static final String EMPTY_STRING = "";
    public static final String SMART_ASS = "Why are you a smartass?";
    public static final String MATRIX_MUST_BE_FULL = "Matrix must be full";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong please try again";

    public static final int DETERMINANT = 1;
    public static final int GAUSS_JORDAN = 2;
    public static final int INVERSE_MATRIX = 3;

    public static final String OPERATION = "Operation: ";

    public static ExecutorService getService() {
        return service;
    }

    public static void hideKeyboard(View view, Context baseContext) {
        final InputMethodManager inputMethodManager = (InputMethodManager) baseContext.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), ZERO);
        }
    }

    public static float dpToPixels(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
