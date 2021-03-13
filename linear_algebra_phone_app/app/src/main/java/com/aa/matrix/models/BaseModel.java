package com.aa.matrix.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BaseModel {

    protected static final String BASE_MATRIX = "Inputted matrix:" + System.lineSeparator();
    protected static final int DECIMAL_NUMBERS = 2;
    protected static final double NEGATIVE_ONE = -1.00;
    protected static final double ZERO = 0.00;
    protected static final double ONE = 1.00;
    // How close a decimal number can be to be rounded up to an integer value
    private static final double PRECISION = 0.05;

    private static BaseModel instance;
    private Matrix matrix;
    private Vector freeColumn;

    public static BaseModel getInstance() {
        if (instance == null) {
            instance = new BaseModel();
        }
        return instance;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Vector getFreeColumn() {
        return freeColumn;
    }

    public void setFreeColumn(Vector freeColumn) {
        this.freeColumn = freeColumn;
    }

    protected double round(double value) {
        // If the |value| is PRECISION close to an integer value round it,
        // Else return the number with 2 decimal places rounded half up.
        // BigDecimal.valueOf used to prevent a return value of -0.00
        if (value + PRECISION > Math.ceil(value)) {
            return BigDecimal.valueOf(Math.ceil(value)).doubleValue();
        } else if (value - PRECISION < Math.floor(value)) {
            return BigDecimal.valueOf(Math.floor(value)).doubleValue();
        } else {
            BigDecimal bd = BigDecimal.valueOf(value);
            bd = bd.setScale(DECIMAL_NUMBERS, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
    }

    protected String doubleToString(double value) {
        // Display to the user the matrix with two decimal places,
        // to stay consistent.
        DecimalFormat dc = new DecimalFormat("0.00");
        return dc.format(value);
    }
}
