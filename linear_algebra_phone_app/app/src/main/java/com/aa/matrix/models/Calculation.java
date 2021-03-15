package com.aa.matrix.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calculation extends BaseModel {

    // The calculation options
    public static final int DETERMINANT = 1;
    public static final int GAUSS_JORDAN = 2;
    public static final int INVERSE_MATRIX = 3;

    // Constants used to describe the action done on the matrix
    public static final String RESTORED_VECTOR = "Restored row %d" + System.lineSeparator();
    public static final String MULTIPLIED_ROW = "R%d = R%d * %s" + System.lineSeparator();
    public static final String MULTIPLIED_ROW_NEGATIVE = "R%d = R%d * (%s)" + System.lineSeparator();
    public static final String SUBTRACTED_VECTORS = "R%d = R%d - (%s * R%d)" + System.lineSeparator();
    public static final String SWAPPED_VECTORS = "R%d <-> R%d" + System.lineSeparator();
    private static final String TEXT_VIEW_SEPARATOR = "--------------------------------------------" +
            "------------";

    private final int calculationType;
    private final int rows;
    private final int cols;
    private final List<String> actions;
    private final List<double[][]> snapShots;
    private String result;

    /**
     * @param matrix Matrix object
     */
    public Calculation(Matrix matrix) {
        this.calculationType = matrix.getCalculationType();
        this.rows = matrix.getRows();
        this.cols = matrix.getCols();
        this.result = "";
        actions = new ArrayList<>();
        snapShots = new ArrayList<>();
    }

    /**
     * Method to set the result of the calculation
     * @param result String holding the result of the calculation
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Method to set the result of the calculation + double[][] of the matrix
     * @param result String holding the result of the calculation
     * @param m double[][] to display as part of the result
     */
    public void setResult(String result, double[][] m) {
        this.result = result + matrixToPrintableString(m);
    }

    /**
     * Method to add an action describing the action done on the matrix and a snapshot of the matrix
     * after that action was done
     * @param action String describing the action done
     * @param matrix double[][] of the matrix after the action
     */
    public void put(String action, double[][] matrix) {
        actions.add(action);
        snapShots.add(deepCopyMatrix(matrix));
    }

    /**
     * Method to add an action describing the action done on the matrix, a snapshot of the matrix
     * after that action was done and a snapshot of the identity matrix after the same action
     * @param action String describing the action done
     * @param matrix double[][] of the matrix after the action
     * @param identityMatrix double[][] of the identity matrix after the action
     */
    public void put(String action, double[][] matrix, double[][] identityMatrix) {
        actions.add(action);
        snapShots.add(deepCopyMatrix(matrix));
        snapShots.add(deepCopyMatrix(identityMatrix));
    }

    /**
     * Method to make a deep copy of the matrix for the snapshots
     * @param matrix double[][] to make a deep-copy of
     * @return double[][] which is a deep-copy of the given matrix
     */
    private double[][] deepCopyMatrix(double[][] matrix) {
        double[][] copy = new double[rows][cols];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
        }
        return copy;
    }

    /**
     * Method to return a String of a given double[][]
     * @param matrix double[][] of a matrix
     * @return String to display to the user of the given matrix
     */
    private String matrixToPrintableString(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(doubleToString(matrix[i][j])).append("   ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method to turn an action and a matrix to a printable String to the user
     * @param action String action describing what action was done on the matrix
     * @param matrix double[][] snapshot of the matrix
     * @return String to display to the user
     */
    private String snapShotToPrintableString(String action, double[][] matrix) {
        String str = "";
        String newLine = System.lineSeparator();
        str += action + newLine + matrixToPrintableString(matrix) + newLine
                + TEXT_VIEW_SEPARATOR + newLine + newLine;
        return str;
    }

    /**
     * Method to turn an action and a matrix to a printable String to the user
     * @param action String action describing what action was done on the matrix
     * @param matrix double[][] snapshot of the matrix
     * @param identityMatrix double[][] snapshot of the identity matrix
     * @return String to display to the user
     */
    private String snapShotToPrintableString(String action, double[][] matrix,
                                             double[][] identityMatrix) {
        String str = "";
        String newLine = System.lineSeparator();
        str += action + newLine + matrixToPrintableString(matrix) + newLine
                + matrixToPrintableString(identityMatrix) + newLine + TEXT_VIEW_SEPARATOR +
                newLine + newLine;
        return str;
    }

    /**
     * @return String describing the entire calculation from start to finish + the result
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int actionIndex = 0, snapShotIndex = 0; actionIndex < actions.size();
             actionIndex++, snapShotIndex += calculationType != INVERSE_MATRIX ? 1 : 2) {
            if (calculationType != INVERSE_MATRIX) {
                sb.append(snapShotToPrintableString(actions.get(actionIndex),
                        snapShots.get(snapShotIndex)));
            } else if (calculationType == INVERSE_MATRIX) {
                sb.append(snapShotToPrintableString(actions.get(actionIndex),
                        snapShots.get(snapShotIndex), snapShots.get(snapShotIndex + 1)));
            }
        }
        sb.append(result);
        return sb.toString();
    }
}
