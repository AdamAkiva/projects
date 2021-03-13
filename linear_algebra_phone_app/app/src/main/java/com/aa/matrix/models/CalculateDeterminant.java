package com.aa.matrix.models;

import java.util.Locale;
import java.util.concurrent.Callable;

public class CalculateDeterminant extends BaseModel implements Callable<CalculationResult> {

    private static final String TWO_BY_TWO_MATRIX = "Used the shortcut way" + System.lineSeparator()
            + "To calculate 2x2 determinant:" + System.lineSeparator() + "(%s * %s) - (%s * %s) = %s"
            + System.lineSeparator();
    private static final String DETERMINANT = "Determinant =";
    private static final String SWAPPED_VECTORS = "Swapped row %d with row %d" + System.lineSeparator();
    private static final String RESTORE_VECTOR = "Restored row %d" + System.lineSeparator();
    private static final String MULTIPLE_VECTOR = "Multiple row %d by %s" + System.lineSeparator();
    private static final String ADDED_VECTOR = "Added row %d to row %d" + System.lineSeparator();
    private static final String SUBTRACT_VECTOR = "Subtracted row %d from row %d" + System.lineSeparator();
    private static final String MULTIPLE_DIAGONAL_LINE = "Multiple the main diagonal line values:" +
            System.lineSeparator();
    private static final String ZERO_VECTOR = "Row %d is a zero row" + System.lineSeparator() +
            "Therefore " + DETERMINANT + "0";
    private final double[][] m;
    private final int rows;
    private final int cols;
    private final CalculationResult result;
    private int swapCount;

    public CalculateDeterminant(double[][] m, int rows, int cols) {
        this.m = m;
        this.rows = rows;
        this.cols = cols;
        this.swapCount = 0;
        result = new CalculationResult(rows, cols);
        result.put(BASE_MATRIX, m);
    }

    @Override
    public CalculationResult call() {
        int mSize = rows * cols;
        if (mSize == 4) {
            // If mat is size 2x2
            twoByTwoMatrix();
        } else {
            for (int i = 0; i < rows - 1; i++) {
                for (int j = i + 1; j < rows; j++) {
                    // Checks if there is a zero vector (didn't bother to check for a duplicate vector,
                    // since a duplicate vector in the next iteration will be a zero vector)
                    if (checkForAZeroVector() != -1) {
                        return result;
                    }
                    // Checks if all values below diagonal line are 0
                    if (checkIfCalculationsAreDone()) {
                        return result;
                    }
                    // If the pivot is 0 find a vector to swap with
                    if (round(m[i][i]) == ZERO) {
                        // If all the pivots are 0 for this iteration skip to the next iteration
                        // otherwise swap with the farthest vector and continue iteration
                        if (!findFarthestVectorToSwapWith(i)) {
                            continue;
                        }
                    }
                    // Gets here if either the pivot is not 0 or there was a vector swap with
                    double[] r = Vector.hardCopyVector(m[i]);
                    if (round(r[i]) != ZERO) {
                        changeVectorPivotToOne(i);
                    }
                    double mValue = findMultiplicationValue(i, j);
                    if (round(mValue) != ZERO
                            && round(mValue) != ONE) {
                        multipleVectorByValue(i, mValue);
                    }
                    addOrSubtractVectors(i, j);
                    m[i] = r;
                    result.put(String.format(Locale.US, RESTORE_VECTOR, i + 1), m);
                }
            }
            calculateDeterminantValueByDiagonalLineMultiplication();
        }
        return result;
    }

    private void twoByTwoMatrix() {
        double value = (m[0][0] * (m[1][1])) - ((m[0][1] * (m[1][0])));
        result.setResult(String.format(Locale.US, TWO_BY_TWO_MATRIX, doubleToString(m[0][0]), doubleToString(m[1][1]),
                doubleToString(m[0][1]), doubleToString(m[1][0]), doubleToString(value)));
    }

    private int checkForAZeroVector() {
        for (int i = 0; i < rows; i++) {
            boolean zeroVector = true;
            for (int j = 0; j < cols; j++) {
                if (round(m[i][j]) != ZERO) {
                    zeroVector = false;
                    break;
                }
            }
            if (zeroVector) {
                result.setResult(String.format(Locale.US, ZERO_VECTOR, i + 1));
                return i;
            }
        }
        return -1;
    }

    private boolean checkIfCalculationsAreDone() {
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < i; j++) {
                if (round(m[i][j]) != ZERO) {
                    return false;
                }
            }
        }
        calculateDeterminantValueByDiagonalLineMultiplication();
        return true;
    }

    private boolean findFarthestVectorToSwapWith(int row) {
        for (int i = row + 1; i < rows; i++) {
            if (round(m[rows - i][i - 1]) != ZERO) {
                swapVectors(row, rows - i);
                return true;
            }
        }
        return false;
    }

    private void swapVectors(int rIndex, int nIndex) {
        double[] t;
        t = m[rIndex];
        m[rIndex] = m[nIndex];
        m[nIndex] = t;
        swapCount++;
        result.put(String.format(Locale.US, SWAPPED_VECTORS, rIndex + 1, nIndex + 1), m);
    }

    private void changeVectorPivotToOne(int index) {
        double[] r = m[index];
        double multipleBy = round(ONE / r[index]);
        for (int i = index; i < r.length; i++) {
            r[i] = round(r[i] * multipleBy);
        }
        result.put(String.format(Locale.US, MULTIPLE_VECTOR, index + 1,
                doubleToString(multipleBy)), m);
    }

    private double findMultiplicationValue(int rIndex, int nIndex) {
        double[] r = m[rIndex];
        double[] n = m[nIndex];
        return round(ONE / (r[rIndex] / (n[rIndex])));
    }

    private void multipleVectorByValue(int index, double value) {
        double[] r = m[index];
        for (int i = index; i < r.length; i++) {
            r[i] = round(r[i] * (value));
        }
        result.put(String.format(Locale.US, MULTIPLE_VECTOR, index + 1,
                doubleToString(value)), m);
    }

    private void addOrSubtractVectors(int rIndex, int nIndex) {
        double[] r = m[rIndex];
        double[] n = m[nIndex];
        if (r[rIndex] + (n[rIndex]) == ZERO) {
            for (int i = rIndex; i < r.length; i++) {
                n[i] = round(n[i] + r[i]);
            }
            result.put(String.format(Locale.US, ADDED_VECTOR, rIndex + 1, nIndex + 1), m);
        } else {
            for (int i = rIndex; i < r.length; i++) {
                n[i] = round(n[i] - r[i]);
            }
            result.put(String.format(Locale.US, SUBTRACT_VECTOR, rIndex + 1, nIndex + 1), m);
        }
    }

    private void calculateDeterminantValueByDiagonalLineMultiplication() {
        double res;
        StringBuilder sb = new StringBuilder();
        sb.append(DETERMINANT).append(" ");
        // Check if the number of swaps is not even, if so swap the mark of the result
        if (swapCount % 2 != 0) {
            res = NEGATIVE_ONE;
            sb.append("-1 (from row swaps) ");
        } else {
            res = ONE;
        }
        for (int i = 0; i < rows; i++) {
            res = round(res * (m[i][i]));
            if (i == rows - 1) {
                sb.append(doubleToString(m[i][i])).append(" = ");
            } else {
                sb.append(doubleToString(m[i][i])).append(" * ");
            }
        }
        result.setResult(MULTIPLE_DIAGONAL_LINE + sb.toString() + doubleToString(res) +
                System.lineSeparator());
    }
}
