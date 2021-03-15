package com.aa.matrix.models;

import java.util.Locale;

import static com.aa.matrix.models.BaseModel.NEGATIVE_ONE;
import static com.aa.matrix.models.BaseModel.ONE;
import static com.aa.matrix.models.BaseModel.ZERO;
import static com.aa.matrix.models.BaseModel.doubleToString;
import static com.aa.matrix.models.BaseModel.round;
import static com.aa.matrix.models.Calculation.RESTORED_VECTOR;

public class Matrix {

    private final int calculationType;
    private final double[][] matrix;
    private final int rows;
    private final int cols;
    private final Calculation result;
    // Used only if the calculation type is Inverse Matrix
    private double[][] identityMatrix;
    // Used only if the calculation type is Determinant, checks in the end if the number of swaps
    // was non-even and if so multiply the result by -1;
    private int rowsSwapCounter = 0;

    /**
     * @param calculationType See Calculation Object for options
     * @param matrix double[][] representing a matrix
     * @param rows Integer holding the number of rows of the matrix
     * @param cols Integer holding the number of cols of the matrix
     */
    public Matrix(final int calculationType, final double[][] matrix, final int rows, final int cols) {
        this.calculationType = calculationType;
        if (calculationType == Calculation.INVERSE_MATRIX) {
            identityMatrix = buildIdentifyMatrix(rows, cols);
        }
        this.matrix = matrix;
        this.rows = rows;
        this.cols = cols;
        this.result = new Calculation(this);
    }

    /**
     * Static method to turn a String[][] to a double[][]
     * @param matrixValuesString String[][] holding the values
     * @param rows Integer holding the number of rows of the matrix
     * @param cols Integer holding the number of cols of the matrix
     * @return double[][] with the String[][] values
     * @throws NumberFormatException If one or more values of the String[][] was not a number
     */
    public static double[][] convertStringArrayTo2DArray(String[] matrixValuesString, int rows, int cols)
    throws NumberFormatException {
        final double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Double.parseDouble(matrixValuesString[j + (i * cols)]);
            }
        }
        return matrix;
    }

    /**
     * Method used in the calculation of Inverse Matrix to build a matching size identity matrix
     * to the given matrix
     * @param rows Integer holding the number of rows of the matrix
     * @param cols Integer holding the number of cols of the matrix
     * @return double[][] identity matrix of the same size of the given matrix
     */
    private double[][] buildIdentifyMatrix(int rows, int cols) {
        double[][] iM = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i != j) {
                    iM[i][j] = ZERO;
                } else {
                    iM[i][j] = ONE;
                }
            }
        }
        return iM;
    }

    public double[][] getIdentityMatrix() {
        return identityMatrix;
    }

    public int getCalculationType() {
        return calculationType;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Calculation getResult() {
        return result;
    }

    public int getRowsSwapCounter() {
        return rowsSwapCounter;
    }


    /**
     * Method used to turn a matrix to lower echelon form
     * @return -1 If there's a solution, any other number if there's no solution and that
     * number is the number of row that is a zero vector
     */
    public int matrixToLowerEchelonForm() {
        // Lower echelon means all elements below the main diagonal line are zeroes.
        int i;
        double[] copy = null;
        for (i = 0; i < rows - 1; i++) {
            findMinAbsoluteValuePivotToSwapWith(i, matrix[i][i]);
            if (calculationType == Calculation.DETERMINANT) {
                copy = Vector.hardCopyVector(matrix[i]);
            }
            changePivotToOne(i);
            rankByPivot(i, false);
            if (calculationType == Calculation.DETERMINANT) {
                matrix[i] = copy;
                result.put(String.format(Locale.US, RESTORED_VECTOR, i + 1), matrix);
            }
        }
        if (calculationType != Calculation.DETERMINANT) {
            changePivotToOne(i);
        }
        return checkForNoSolution();
    }

    /**
     * Method used to turn a lower echelon matrix to reduced lower echelon form
     * @return -1 If there's a solution, any other number if there's no solution and that
     * number is the number of row that is a zero vector
     */
    public int matrixToReducedRowEchelonForm() {
        // Reduced lower echelon means the matrix is a diagonal matrix
        for (int i = rows - 1; i > 0; i--) {
            if (!checkForZeroVector(i)) {
                rankByPivot(i, true);
            }
        }
        return checkForNoSolution();
    }

    /**
     * Method used to check if a matrix row is a row filled with zeroes
     * @param rIndex Integer holding the row index to check
     * @return True if all the row values are zeroes, false otherwise
     */
    public boolean checkForZeroVector(int rIndex) {
        for (int i = 0; calculationType != Calculation.GAUSS_JORDAN ? i < cols : i < cols - 1; i++) {
            if (round(matrix[rIndex][i]) != ZERO) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method used to check if the matrix has no solution for the calculation type
     * @return -1 If there's a solution, any other number if there's no solution and that
     * number is the number of row that is a zero vector
     */
    private int checkForNoSolution() {
        for (int i = 0; i < rows; i++) {
            if (checkForZeroVector(i)) {
                // If the calculation is Gauss Jordan for no solution it's not enough that there
                // is a zero vector, there needs to be a contradictory equation (e.g 0x = 3)
                if (calculationType == Calculation.GAUSS_JORDAN) {
                    if (round(matrix[i][cols - 1]) != ZERO) {
                        return i + 1;
                    }
                } else {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    /**
     * Method used to multiply a row by value that will make the pivot element be equal to 1
     * @param pIndex Integer for the index of the pivot row in the matrix
     */
    private void changePivotToOne(int pIndex) {
        double pivot = matrix[pIndex][pIndex];
        double dValue;
        if (round(pivot) != ZERO && round(pivot) != ONE) {
            dValue = round(ONE / pivot);
        } else {
            return;
        }

        if (calculationType == Calculation.INVERSE_MATRIX) {
            for (int i = 0; i < cols; i++) {
                if (round(matrix[pIndex][i]) != ZERO) {
                    matrix[pIndex][i] = round(matrix[pIndex][i] * dValue);
                }
            }
            // Copy of the above action on the Identity matrix.
            for (int i = 0; i < cols; i++) {
                if (round(identityMatrix[pIndex][i]) != ZERO) {
                    identityMatrix[pIndex][i] = round(identityMatrix[pIndex][i] * dValue);
                }
            }
            if (dValue > 0) {
                result.put(String.format(Locale.US, Calculation.MULTIPLIED_ROW, pIndex + 1, pIndex + 1,
                        doubleToString(dValue)), matrix, identityMatrix);
            } else {
                result.put(String.format(Locale.US, Calculation.MULTIPLIED_ROW_NEGATIVE, pIndex + 1, pIndex + 1,
                        doubleToString(dValue)), matrix, identityMatrix);
            }
        } else {

            for (int i = 0; i < cols; i++) {
                if (round(matrix[pIndex][i]) != ZERO) {
                    matrix[pIndex][i] = round(matrix[pIndex][i] * dValue);
                }
            }
            if (dValue > 0) {
                result.put(String.format(Locale.US, Calculation.MULTIPLIED_ROW, pIndex + 1, pIndex + 1,
                        doubleToString(dValue)), matrix);
            } else {
                result.put(String.format(Locale.US, Calculation.MULTIPLIED_ROW_NEGATIVE, pIndex + 1, pIndex + 1,
                        doubleToString(dValue)), matrix);
            }
        }
    }

    /**
     * Method used to rank all rows below pIndex in according to the pIndex row
     * @param pIndex Integer for the index of the pivot row in the matrix
     * @param reversed Boolean which says with ranking form is it, from up to down or from down to up
     *                 (used to distinguish between ranking for lower echelon form to reduced lower echelon form)
     */
    private void rankByPivot(int pIndex, boolean reversed) {
        double[] pivotRow = matrix[pIndex];
        for (int i = !reversed ? pIndex + 1 : pIndex - 1; !reversed ? i < rows : i > -1; i += !reversed ? 1 : -1) {
            double[] compareRow = matrix[i];
            double cValue = round(findMultiplicationValue(compareRow, pIndex));
            if (round(cValue) == ZERO) {
                continue;
            }
            if (calculationType == Calculation.INVERSE_MATRIX) {
                subtractVectors(pivotRow, compareRow, cValue);
                // // Copy of the above action on the Identity matrix.
                subtractVectors(Vector.hardCopyVector(identityMatrix[pIndex]), identityMatrix[i], cValue);
                result.put(String.format(Locale.US, Calculation.SUBTRACTED_VECTORS, i + 1, i + 1,
                        doubleToString(Math.abs(cValue)), pIndex + 1), matrix, identityMatrix);
            } else {
                subtractVectors(pivotRow, compareRow, cValue);
                result.put(String.format(Locale.US, Calculation.SUBTRACTED_VECTORS, i + 1, i + 1,
                        doubleToString(Math.abs(cValue)), pIndex + 1), matrix);
            }
        }
    }

    /**
     * Method to subtract two vectors, v2 = v2 - v1
     * @param v1 double[]
     * @param v2 double[] which will change
     * @param cValue double value to multiply r1 with to make it that v2[pivot] will be zero
     */
    private void subtractVectors(double[] v1, double[] v2, double cValue) {
        for (int i = 0; i < cols; i++) {
            if (round(v1[i]) != ZERO) {
                double roundedMultiplication = round(v1[i] * cValue);
                // Needs another rounding if there's negative number + positive number
                v2[i] = round(v2[i] + roundedMultiplication);
            }
        }
    }

    /**
     * Method to find the multiplication value that will make the pivot row + row below it
     * to be zero
     * @param compareRow double[] of the row below the pivot row
     * @param pIndex Integer for the index of the pivot row in the matrix
     * @return double value which will be the result of what is described above
     */
    private double findMultiplicationValue(double[] compareRow, int pIndex) {
        return round(compareRow[pIndex] * NEGATIVE_ONE);
    }

    /**
     * Method to find a row with the minimum value at rIndex index
     * @param pivotColumnIndex Integer holding the column index for the pivot
     * @param pValue double holding the pivot value (to find the minimum value to swap with)
     */
    private void findMinAbsoluteValuePivotToSwapWith(int pivotColumnIndex, double pValue) {
        int rSwap = -1;
        double minValue = Math.abs(pValue);
        for (int i = pivotColumnIndex + 1; i < rows; i++) {
            if (Math.abs(matrix[i][pivotColumnIndex]) < minValue && Math.abs(matrix[i][pivotColumnIndex]) != ZERO) {
                rSwap = i;
                minValue = Math.abs(matrix[i][pivotColumnIndex]);
            }
        }
        if (rSwap != -1) {
            rowsSwapCounter++;
            swapRows(pivotColumnIndex, rSwap);
        }
    }

    /**
     * Method to swap two rows in the matrix
     * @param rIndex Integer holding the first index for the row to swap
     * @param rSwap Integer holding the second index for the row to swap
     */
    private void swapRows(int rIndex, int rSwap) {
        double[] t;
        if (calculationType == Calculation.INVERSE_MATRIX) {
            t = matrix[rIndex];
            matrix[rIndex] = matrix[rSwap];
            matrix[rSwap] = t;
            // Copy of the above action on the Identity matrix.
            t = identityMatrix[rIndex];
            identityMatrix[rIndex] = identityMatrix[rSwap];
            identityMatrix[rSwap] = t;
            result.put(String.format(Locale.US, Calculation.SWAPPED_VECTORS, rIndex + 1, rSwap + 1), matrix, identityMatrix);
        } else {
            t = matrix[rIndex];
            matrix[rIndex] = matrix[rSwap];
            matrix[rSwap] = t;
            result.put(String.format(Locale.US, Calculation.SWAPPED_VECTORS, rIndex + 1, rSwap + 1), matrix);
        }
    }

}
