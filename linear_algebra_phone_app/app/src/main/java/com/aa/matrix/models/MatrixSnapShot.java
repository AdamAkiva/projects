package com.aa.matrix.models;

import com.aa.matrix.etc.InvalidParameterException;

import java.util.Locale;

import static com.aa.matrix.views.BaseActivity.ADDITION;
import static com.aa.matrix.views.BaseActivity.CHANGE_ROW_BY_PIVOT;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_ADDITION_ACTION_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_CHANGE_ROW_BY_PIVOT_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_MULTIPLICATION_ACTION_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_SWAP_ACTION_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_TRANSPOSE_MATRIX;
import static com.aa.matrix.views.BaseActivity.INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS;
import static com.aa.matrix.views.BaseActivity.MULTIPLICATION;
import static com.aa.matrix.views.BaseActivity.SWAP;
import static com.aa.matrix.views.BaseActivity.TRANSPOSE;

public class MatrixSnapShot {

    private final double[][] matrix;
    private final Pivot pivot;
    private final MatrixAction action;
    private final String describeAction;

    public MatrixSnapShot(final Matrix matrix, final MatrixAction action) throws InvalidParameterException {
        this.matrix = matrix.getMatrix();
        this.pivot = matrix.getPivot();
        this.action = action;
        describeAction = actionToString();
    }

    private String actionToString() throws InvalidParameterException {
        switch (action.getAction()) {
            case TRANSPOSE:
                return DESCRIBE_TRANSPOSE_MATRIX;
            case SWAP:
                return String.format(Locale.US, DESCRIBE_SWAP_ACTION_PATTERN,
                        String.valueOf(action.getFirstRow()), String.valueOf(action.getSecondRow()));
            case ADDITION:
                return String.format(Locale.US, DESCRIBE_ADDITION_ACTION_PATTERN,
                        String.valueOf(action.getFirstRow()), String.valueOf(action.getSecondRow()));
            case MULTIPLICATION:
                return String.format(Locale.US, DESCRIBE_MULTIPLICATION_ACTION_PATTERN,
                        String.valueOf(action.getFirstRow()), Vector.roundDoubleValue(action.getActionValue()));
            case CHANGE_ROW_BY_PIVOT:
                return String.format(Locale.US, DESCRIBE_CHANGE_ROW_BY_PIVOT_PATTERN,
                        String.valueOf(action.getFirstRow()), Vector.roundDoubleValue(action.getActionValue()),
                                String.valueOf(action.getSecondRow()));
        }
        throw new InvalidParameterException(INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS);
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public MatrixAction getAction() {
        return action;
    }

    public String getDescribeAction() {
        return describeAction;
    }
}
