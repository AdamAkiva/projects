package com.aa.matrix.views;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public static final int ZERO = 0;

    public static final CharSequence INPUT_VALUES = "Please input rows and columns values";
    public static final String ROWS_INVALID = "Between 1 to 5";
    public static final String COLS_INVALID = "Between 1 to 5";
    public static final CharSequence CANNOT_INVERSE_MATRIX = "Cannot inverse matrix";
    public static final String EMPTY_STRING = "";
    public static final CharSequence SMART_ASS = "Why are you a smartass?";
    public static final CharSequence MATRIX_MUST_BE_FULL = "Matrix must be full";
    public static final CharSequence SOMETHING_WENT_WRONG = "Something went wrong please try again";

    public static final int DETERMINANT = 1;
    public static final int GAUSS_JORDAN = 2;
    public static final int INVERSE_MATRIX = 3;

    public static final String OPERATION = "Operation: ";
}
