package com.aa.matrix.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BaseModel {

    public static final String BASE_MATRIX = "Inputted matrix:" + System.lineSeparator();
    public static final String BASE_MATRICES = "Inputted matrix and a" + System.lineSeparator() +
            "Matching identity matrix:" + System.lineSeparator();

    public static final int DECIMAL_NUMBERS = 2;
    public static final double NEGATIVE_ONE = -1.00;
    public static final double ZERO = 0.00;
    public static final double ONE = 1.00;
    // How close a decimal number can be to be rounded up to an integer value
    public static final double PRECISION = 0.05;

    private static BaseModel instance;
    private Matrix matrix;

    public static BaseModel getInstance() {
        if (instance == null) {
            instance = new BaseModel();
        }
        return instance;
    }

    public static double round(double value) {
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

    public static String doubleToString(double value) {
        // Display to the user the matrix with two decimal places,
        // to stay consistent.
        DecimalFormat dc = new DecimalFormat("0.00");
        return dc.format(value);
    }

    public void setMatrixObject(int calculationType, double[][] matrix, int rows, int cols) {
        this.matrix = new Matrix(calculationType, matrix, rows, cols);
    }

    public void setMatrixObject(int calculationType, double[][] matrix, double[] freeColumn, int rows, int cols) {
        this.matrix = new Matrix(calculationType, attachFreeVectorToMatrix(matrix, freeColumn, rows, cols),
                rows, cols + 1);
    }

    public Matrix getMatrixObject() {
        return matrix;
    }

    private double[][] attachFreeVectorToMatrix(double[][] m, double[] v, int rows, int cols) {
        double[][] nM = new double[rows][cols + 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= cols; j++) {
                if (j == cols) {
                    nM[i][j] = v[i];
                } else {
                    nM[i][j] = m[i][j];
                }
            }
        }
        return nM;
    }
}
