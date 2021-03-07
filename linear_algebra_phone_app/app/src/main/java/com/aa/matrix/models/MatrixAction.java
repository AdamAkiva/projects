package com.aa.matrix.models;

import com.aa.matrix.etc.InvalidParameterException;

import static com.aa.matrix.views.BaseActivity.ADDITION;
import static com.aa.matrix.views.BaseActivity.CHANGE_ROW_BY_PIVOT;
import static com.aa.matrix.views.BaseActivity.INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS;
import static com.aa.matrix.views.BaseActivity.MULTIPLICATION;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ONE;
import static com.aa.matrix.views.BaseActivity.ROW_NUMBER_MUST_BE_POSITIVE;
import static com.aa.matrix.views.BaseActivity.SWAP;
import static com.aa.matrix.views.BaseActivity.TRANSPOSE;
import static com.aa.matrix.views.BaseActivity.ZERO_DOUBLE;

public class MatrixAction {

    private final int action;
    private final double actionValue;
    private final int firstRow;
    private final int secondRow;

    public MatrixAction(int action) throws  InvalidParameterException{
        if (action == TRANSPOSE) {
            this.action = action;
        } else {
            throw new InvalidParameterException(INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS);
        }
        this.actionValue = ZERO_DOUBLE;
        this.firstRow = NEGATIVE_ONE;
        this.secondRow = NEGATIVE_ONE;
    }

    public MatrixAction(int action, double actionValue, int firstRow)
            throws InvalidParameterException {
        if (action == MULTIPLICATION) {
            this.action = action;
        } else {
            throw new InvalidParameterException(INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS);
        }
        this.actionValue = actionValue;
        if (firstRow > 0) {
            this.firstRow = firstRow;
        } else {
            throw new InvalidParameterException(ROW_NUMBER_MUST_BE_POSITIVE);
        }
        this.secondRow = NEGATIVE_ONE;
    }

    public MatrixAction(int action, int firstRow, int secondRow)
            throws InvalidParameterException{
        if (action == SWAP || action == ADDITION) {
            this.action = action;
        } else {
            throw new InvalidParameterException(INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS);
        }
        this.actionValue = ZERO_DOUBLE;
        if (firstRow > 0) {
            this.firstRow = firstRow;
        } else {
            throw new InvalidParameterException(ROW_NUMBER_MUST_BE_POSITIVE);
        }
        if (secondRow > 0) {
            this.secondRow = secondRow;
        } else {
            throw new InvalidParameterException(ROW_NUMBER_MUST_BE_POSITIVE);
        }
    }

    public MatrixAction(int action, double actionValue, int firstRow, int secondRow)
        throws InvalidParameterException{
        if (action == CHANGE_ROW_BY_PIVOT) {
            this.action = action;
        } else {
            throw new InvalidParameterException(INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS);
        }
        this.actionValue = actionValue;
        if (firstRow > 0) {
            this.firstRow = firstRow;
        } else {
            throw new InvalidParameterException(ROW_NUMBER_MUST_BE_POSITIVE);
        }
        if (secondRow > 0) {
            this.secondRow = secondRow;
        } else {
            throw new InvalidParameterException(ROW_NUMBER_MUST_BE_POSITIVE);
        }
    }

    public int getAction() {
        return action;
    }

    public double getActionValue() {
        return actionValue;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getSecondRow() {
        return secondRow;
    }
}
