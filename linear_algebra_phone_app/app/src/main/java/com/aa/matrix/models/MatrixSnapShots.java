package com.aa.matrix.models;

import com.aa.matrix.etc.InvalidParameterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.aa.matrix.views.BaseActivity.ADDITION;
import static com.aa.matrix.views.BaseActivity.CHANGE_ROW_BY_PIVOT;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_ADDITION_ACTION_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_CHANGE_ROW_BY_PIVOT_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_MULTIPLICATION_ACTION_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_SWAP_ACTION_PATTERN;
import static com.aa.matrix.views.BaseActivity.DESCRIBE_TRANSPOSE_MATRIX;
import static com.aa.matrix.views.BaseActivity.INVALID_ACTION_FOR_FOR_INPUTTED_PARAMETERS;
import static com.aa.matrix.views.BaseActivity.MULTIPLICATION;
import static com.aa.matrix.views.BaseActivity.START_MATRIX;
import static com.aa.matrix.views.BaseActivity.SWAP;
import static com.aa.matrix.views.BaseActivity.TRANSPOSE;

public class MatrixSnapShots {

    private final Map<String, double[][]> snapShots;
    private final int rows;
    private final int columns;

    public MatrixSnapShots(Matrix startMatrix) {
        snapShots = new LinkedHashMap<>();
        this.rows = startMatrix.getRowsCount();
        this.columns = startMatrix.getColumnCount();
        snapShots.put(START_MATRIX, deepCopyMatrixAs2DArray(startMatrix));
    }

    private String actionToString(MatrixAction action) throws InvalidParameterException {
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

    private double[][] deepCopyMatrixAs2DArray(Matrix matrix) {
        double[][] copy = new double[matrix.getRowsCount()][matrix.getColumnCount()];
        for (int i = 0; i < matrix.getMatrix().length; i++) {
            copy[i] = Arrays.copyOf(matrix.getMatrix()[i], matrix.getMatrix()[i].length);
        }
        return copy;
    }

    public void put(MatrixAction action, Matrix matrix) throws InvalidParameterException {
        snapShots.put(actionToString(action), deepCopyMatrixAs2DArray(matrix));
    }

    public Map<String, double[][]> getSnapShots() {
        return snapShots;
    }

    private String snapShotToPrintableString(Map.Entry<String, double[][]> snapShot) {
        String str = "";
        String newLine = System.lineSeparator();
        str += snapShot.getKey() + newLine + matrixToPrintableString(snapShot.getValue()) + newLine + newLine;
        return str;
    }

    private String matrixToPrintableString(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(Vector.roundDoubleValue(matrix[i][j])).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, double[][]> snapShot: snapShots.entrySet()) {
            sb.append(snapShotToPrintableString(snapShot));
        }
        return sb.toString();
    }
}
