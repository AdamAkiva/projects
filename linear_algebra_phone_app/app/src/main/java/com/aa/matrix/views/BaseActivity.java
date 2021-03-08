package com.aa.matrix.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    public static final double DEFAULT_DETERMINANT_VALUE = 0f;
    public static final int NO_SWITCH_VALUE = 0;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int NEGATIVE_ONE = -1;
    public static final double ZERO_DOUBLE = 0.0;
    public static final double NEGATIVE_ZERO_DOUBLE = -0.0;

    public static final String POSITIVE = "+";
    public static final String NEGATIVE = "-";
    public static final String DIVIDE_BY_ZERO_ERROR = "Cannot divide by zero";
    public static final String VECTORS_SIZE_ERROR = "Vectors must be of equal size";
    public static final String SWAPPED_ROW = "Row %s <---> Row %s\n";
    public static final String DIVIDED_ROW_BY = "Divided Row %s by %s\n";
    public static final String ADD_ROW_BY_PIVOT = "Row %s %s %s*Row %s\n";
    public static final String TRANSPOSED_MATRIX = "Transposed matrix\n";
    public static final String CANNOT_INVERSE_MATRIX = "Cannot inverse matrix";
    public static final String INVERTIBLE_MATRIX = "Invertible matrix:";
    public static final String NEGATIVE_ZERO = "-0";
    public static final String MINUS = "-";
    public static final String PARENT_VIEW_MUST_BE_RELATIVE_LAYOUT = "Parent view must be relative" +
            "layout";
    public static final int VECTOR_MAX_NUMBER_OF_CHARACTERS_IN_LINE = 50;
    public static final String VECTOR_VARIABLE = "X";
    public static final String EMPTY_STRING = "";
    public static final String SMART_ASS = "Why are you a smartass?";
    public static final String MATRIX_MUST_BE_FULL = "Matrix must be full";
    public static final String PARAMETER_MUST_IMPLEMENT_CALLABLE = "Parameter must implement " +
            "callable interface";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong please try again";

    public static final String START_MATRIX = "Inputted matrix:";

    public static final int SWAP = 1;
    public static final int ADDITION = 2;
    public static final int MULTIPLICATION = 3;
    public static final int TRANSPOSE = 4;
    public static final int CHANGE_ROW_BY_PIVOT = 5;

    public static final String INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS = "Invalid action for with the inputted parameters";
    public static final String ROW_NUMBER_MUST_BE_POSITIVE = "Inputted Row number must be positive";

    public static final String DESCRIBE_TRANSPOSE_MATRIX = "Transposed the matrix";
    public static final String DESCRIBE_SWAP_ACTION_PATTERN = "Swapped row %s with row %s";
    public static final String DESCRIBE_ADDITION_ACTION_PATTERN = "Added row %s to row %s";
    public static final String DESCRIBE_MULTIPLICATION_ACTION_PATTERN = "Multiplied row %s by %s";
    public static final String DESCRIBE_CHANGE_ROW_BY_PIVOT_PATTERN = "Multiplied row %s by %s"
            + System.lineSeparator() + "and added it to row %s";

    public static final int DETERMINANT = 1;
    public static final int GAUSS_JORDEN = 2;
    public static final int INVERSE_MATRIX = 3;

    public static final String OPERATION = "Operation: ";

    public static final String STEP = "Step";
    public static final String SNAP_SHOT_SEPARATOR = System.lineSeparator() +
            "------------------------------------------------------" + System.lineSeparator();

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
