package com.aa.matrix.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Adam Akiva
 * Singleton class used to hold the matrix information to pass between activities as well
 * as some constant and methods to use in multiple other model package classes
 */
public class BaseModel {

    public static final String BASE_MATRIX = "Inputted matrix:" + System.lineSeparator();
    public static final String BASE_MATRICES = "Inputted matrix and a" + System.lineSeparator() +
            "Matching identity matrix:" + System.lineSeparator();

    // The amount of decimal number displayed to the user, change to increase precision but it won't
    // well on the user interface.
    private static final int DECIMAL_NUMBERS = 2;
    // How close a decimal number can be to be rounded up to an integer value, increase to lower
    // precision, decrease to increase precision
    private static final double PRECISION = 0.05;

    // Constants used in other model package classes
    public static final double NEGATIVE_ONE = -1.00;
    public static final double ZERO = 0.00;
    public static final double ONE = 1.00;

    // Format pattern for a double value
    private static final String PATTERN = "0.00";

    private static BaseModel instance;
    private Matrix matrix;

    public static BaseModel getInstance() {
        if (instance == null) {
            instance = new BaseModel();
        }
        return instance;
    }

    /**
     * Method used to round a double value
     * @param value double value to round
     * @return rounded double value based on the constants declared above
     */
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

    /**
     * Method used to parse double to String
     * @param value double value to format to String
     * @return String formatted by the Pattern value declared above
     */
    public static String doubleToString(double value) {
        // Display to the user the matrix with two decimal places,
        // to stay consistent.
        DecimalFormat dc = new DecimalFormat(PATTERN);
        return dc.format(value);
    }

    /**
     * @param calculationType See Calculation class for options
     * @param matrix double[][] representing a matrix
     * @param rows integer holding the amount of rows
     * @param cols integer holding the amount of cols
     */
    public void setMatrixObject(int calculationType, double[][] matrix, int rows, int cols) {
        this.matrix = new Matrix(calculationType, matrix, rows, cols);
    }

    /**
     * @param calculationType See Calculation class for options
     * @param matrix double[][] representing a matrix
     * @param freeColumn double[] representing the free column array
     * @param rows integer holding the amount of rows
     * @param cols integer holding the amount of cols
     */
    public void setMatrixObject(int calculationType, double[][] matrix, double[] freeColumn, int rows, int cols) {
        this.matrix = new Matrix(calculationType, attachFreeVectorToMatrix(matrix, freeColumn, rows, cols),
                rows, cols + 1);
    }

    public Matrix getMatrixObject() {
        return matrix;
    }

    /**
     * Method to attach the freeColumn vector the matrix if the desired calculation is Gauss Jordan
     * @param m double[][] matrix
     * @param v double[] freeColumn array
     * @param rows integer holding the amount of rows
     * @param cols integer holding the amount of cols
     * @return double[][] with v parameter as the last column of m
     */
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
