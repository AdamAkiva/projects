package com.aa.matrix.views;

import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int ERROR_TEXT_VIEW_TOP_MARGIN = 20;
    public static final int ERROR_TEXT_VIEW_BOTTOM_MARGIN = 20;
    public static final int ERROR_TEXT_VIEW_WIDTH = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int ERROR_TEXT_VIEW_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT;
    public static final float ERROR_TEXT_VIEW_TEXT_SIZE = 14f;
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
}
