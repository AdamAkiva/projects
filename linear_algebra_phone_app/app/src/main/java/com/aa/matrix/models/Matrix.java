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
    private double[][] identityMatrix;
    // Used if the calculation type is determinant, checks in the end if the number of swaps
    // was non-even and if so multiply the result by -1;
    private int rowsSwapCounter = 0;

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

    public static double[][] convertStringArrayTo2DArray(String[] matrixValuesString, int rows, int cols) {
        final double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Double.parseDouble(matrixValuesString[j + (i * cols)]);
            }
        }
        return matrix;
    }

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

    // Lower echelon means all elements below the main diagonal line are zeroes.
    public int matrixToLowerEchelonForm() {
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

    // Reduced lower echelon means the matrix is a diagonal matrix
    public int matrixToReducedRowEchelonForm() {
        for (int i = rows - 1; i > 0; i--) {
            if (!checkForZeroVector(i)) {
                rankByPivot(i, true);
            }
        }
        return checkForNoSolution();
    }

    public boolean checkForZeroVector(int rIndex) {
        for (int i = 0; calculationType != Calculation.GAUSS_JORDAN ? i < cols : i < cols - 1; i++) {
            if (round(matrix[rIndex][i]) != ZERO) {
                return false;
            }
        }
        return true;
    }

    private int checkForNoSolution() {
        for (int i = 0; i < rows; i++) {
            if (checkForZeroVector(i)) {
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

    private void subtractVectors(double[] v1, double[] v2, double cValue) {
        for (int i = 0; i < cols; i++) {
            if (round(v1[i]) != ZERO) {
                double roundedMultiplication = round(v1[i] * cValue);
                // Needs another rounding if there's negative number + positive number
                v2[i] = round(v2[i] + roundedMultiplication);
            }
        }
    }

    private double findMultiplicationValue(double[] compareRow, int pIndex) {
        return round(compareRow[pIndex] * NEGATIVE_ONE);
    }

    private void findMinAbsoluteValuePivotToSwapWith(int rIndex, double pValue) {
        int rSwap = -1;
        double minValue = Math.abs(pValue);
        for (int i = rIndex + 1; i < rows; i++) {
            if (Math.abs(matrix[i][rIndex]) < minValue && Math.abs(matrix[i][rIndex]) != ZERO) {
                rSwap = i;
                minValue = Math.abs(matrix[i][rIndex]);
            }
        }
        if (rSwap != -1) {
            rowsSwapCounter++;
            swapRows(rIndex, rSwap);
        }
    }

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
