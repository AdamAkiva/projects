package com.aa.matrix.models;

import java.util.Locale;

import static com.aa.matrix.views.BaseActivity.ONE;
import static com.aa.matrix.views.BaseActivity.ZERO;

public class Pivot {

    private double value;
    private final int row;
    private final int column;

    public Pivot(double value, int row, int column) {
        this.value = value;
        this.row = row;
        this.column = column;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public static Pivot findPivot(double[][] matrix, int rowNumber) throws Pivot.NotFoundException {
        for (int i = 0; i < matrix[ZERO].length; i++) {
            if (matrix[rowNumber][i] != ZERO) {
                return new Pivot(matrix[rowNumber][i], rowNumber, i);
            }
        }
        throw new Pivot.NotFoundException();
    }

    public static Pivot findPivotReversed(double[][] matrix, int rowNumber) throws Pivot.NotFoundException {
        int i = matrix[ZERO].length - ONE;
        while (i != 0) {
            if (matrix[rowNumber][i] == ONE) {
                return new Pivot(matrix[rowNumber][i], rowNumber, i);
            }
            i--;
        }
        throw new Pivot.NotFoundException();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Value: %f\nPosition: (%d, %d)", value, row, column);
    }

    public static class NotFoundException extends Exception {

        private static final String MESSAGE = "classes.Pivot not found in this line";

        public NotFoundException() {
            super(MESSAGE);
        }
    }
}
