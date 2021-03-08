package com.aa.matrix.models;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.aa.matrix.views.BaseActivity.MINUS;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ZERO;
import static com.aa.matrix.views.BaseActivity.ONE;
import static com.aa.matrix.views.BaseActivity.VECTOR_MAX_NUMBER_OF_CHARACTERS_IN_LINE;
import static com.aa.matrix.views.BaseActivity.VECTOR_VARIABLE;
import static com.aa.matrix.views.BaseActivity.ZERO;

public class Vector {

    private final double[] vector;

    public Vector(final double[] vector) {
        this.vector = vector;
    }

    public Vector(final Vector vector) {
        this.vector = new double[vector.getVectorAsArray().length];
        for (int i = 0; i < vector.getVectorAsArray().length; i++) {
            this.vector[i] = vector.getVectorAsArray()[i];
        }
    }

    public double[] getVectorAsArray() {
        return vector;
    }

    public boolean equalSize(Vector v) {
        return v.getVectorAsArray().length == this.getVectorAsArray().length;
    }

    public void divideVectorRowByValue(final int row, final double value) {
        if (value != 0) {
            vector[row] /= value;
        }
    }

    public void orderVector(final int[] swapArray) {
        for (int i = 0; i < swapArray.length; i++) {
            if (swapArray[i] != ZERO) {
                double tempNum = vector[swapArray[i]];
                vector[swapArray[i]] = vector[i];
                vector[i] = tempNum;
            }
        }
    }

    public void changeVectorRowByPivot(final Pivot pivot, final double[] multiplicationNumbers) {
        final int pivotRow = pivot.getRow();
        for (int i = pivotRow + ONE, j = 0; i < this.getVectorAsArray().length; i++, j++) {
            if (vector[pivotRow] != ZERO) {
                vector[i] = vector[i] + vector[pivotRow] * multiplicationNumbers[j];
            }
        }
    }

    public void changeVectorRowByPivotReversed(Pivot pivot,
                                             double[] multiplicationNumbers) {
        int pivotRow = pivot.getRow();
        for (int i = pivotRow - ONE; i >= 0; i--) {
            if (vector[pivotRow] != ZERO) {
                vector[i] = vector[i] + vector[pivotRow] * multiplicationNumbers[i];
            }
        }
    }

    public static String roundDoubleValue(double num) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);
        return df.format(num);
    }

    public static boolean checkIfZeroVector(Vector v) {
        if (v != null && v.getVectorAsArray().length > 0) {
            for (int i = 0; i < v.getVectorAsArray().length; i++) {
                if (v.getVectorAsArray()[i] != 0) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector) {
            if (equalSize((Vector) obj)) {
                for (int i = 0; i < getVectorAsArray().length; i++) {
                    if ((((Vector) obj).getVectorAsArray())[i] != vector[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public String[] getVectorAsStringArray() {
        String[] result = new String[vector.length];
        for (int i = 0; i < getVectorAsArray().length; i++) {
            String val = Vector.roundDoubleValue(getVectorAsArray()[i]);
            if (val.equals(NEGATIVE_ZERO)) {
                val = val.replace(MINUS, " ");
            }
            result[i] = val;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        String val;
        for (int i = 0; i < getVectorAsArray().length; i++) {
            val = Vector.roundDoubleValue(getVectorAsArray()[i]);
            if (val.equals(NEGATIVE_ZERO)) {
                val = val.replace(MINUS, " ");
            }
            if (i != getVectorAsArray().length - ONE) {
                sb.append(String.format("(%s:%s),", VECTOR_VARIABLE.concat(String.valueOf(i)), val));
                if (sb.length() > VECTOR_MAX_NUMBER_OF_CHARACTERS_IN_LINE) {
                    sb.append("\n");
                }
            } else {
                sb.append(String.format("(%s:%s)", VECTOR_VARIABLE.concat(String.valueOf(i)), val));
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}
