package com.aa.matrix.models;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;

public class CalculateDeterminant implements Callable<CalculationResult> {

    private final double[][] m;
    private final int rows;
    private final int cols;
    private int swapCount;

    private final CalculationResult result;

    private static final String ONE_BY_ONE_MATRIX = "Nothing to calculate." + System.lineSeparator() +
            "Determinant = %s" + System.lineSeparator();

    private static final String TWO_BY_TWO_MATRIX = "Used the shortcut way" + System.lineSeparator()
            + "To calculate 2x2 determinant:" + System.lineSeparator() + "(%s * %s) - (%s * %s) = %s"
            + System.lineSeparator();

    private static final String DETERMINANT = "Determinant =";
    private static final String BASE_MATRIX = "Inputted matrix:" + System.lineSeparator();
    private static final String SWAPPED_VECTORS = "Swapped row %d with row %d" + System.lineSeparator();
    private static final String RESTORE_VECTOR = "Restored row %d" + System.lineSeparator();
    private static final String MULTIPLE_VECTOR = "Multiple row %d by %s" + System.lineSeparator();
    private static final String ADDED_VECTOR = "Added row %d to row %d" + System.lineSeparator();
    private static final String SUBTRACT_VECTOR = "Subtracted row %d from row %d" + System.lineSeparator();
    private static final String MULTIPLE_DIAGONAL_LINE = "Multiple the main diagonal line values:" +
            System.lineSeparator();
    private static final String ZERO_VECTOR = "Row %d is a zero row" + System.lineSeparator() +
            "Therefore " + DETERMINANT + "0";

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
        if (mSize == 1) {
            // If mat is size 1x1
            oneByOneMatrix();
        } else if (mSize == 4) {
            // If mat is size 2x2
            twoByTwoMatrix();
        } else {
            for (int i = 0; i < rows - 1; i++) {
                for (int j = i + 1; j < rows; j++) {
                    // Checks if all values below diagonal line are 0
                    if (checkIfCalculationsAreDone()) {
                        return result;
                    }
                    // Checks if there is a zero vector (didn't bother to check for a duplicate vector,
                    // since a duplicate vector in the next iteration will be a zero vector)
                    if (checkForAZeroVector() != -1) {
                        return result;
                    }
                    // If the pivot is 0 find a vector to swap with
                    if (m[i][i] == 0) {
                        // If all the pivots are 0 for this iteration skip to the next iteration
                        // otherwise swap with the farthest vector and continue iteration
                        if (!findFarthestVectorToSwapWith(i)) {
                            continue;
                        }
                    }
                    // Gets here if either the pivot is not 0 or there was a vector swap with
                    double[] r = hardCopyVector(m[i]);
                    if (r[i] != 1) {
                        changeVectorPivotToOne(i);
                    }
                    double mValue = findMultiplicationValue(i, j);
                    if (mValue != 1f && mValue != -1f) {
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

    private void oneByOneMatrix() {
        result.setResult(String.format(ONE_BY_ONE_MATRIX, CalculationResult.doubleToString(m[0][0])));
    }

    private void twoByTwoMatrix() {
        double value = (m[0][0] * m[1][1]) - (m[0][1] * m[1][0]);
        result.setResult(String.format(Locale.US, TWO_BY_TWO_MATRIX, CalculationResult.doubleToString(m[0][0]),
                CalculationResult.doubleToString(m[1][1]), CalculationResult.doubleToString(m[0][1]),
                CalculationResult.doubleToString(m[1][0]), value));
    }

    private int checkForAZeroVector() {
        for (int i = 0; i < rows; i++) {
            boolean zeroVector = true;
            for (int j = 0; j < cols; j++) {
                if (m[i][j] != 0) {
                    zeroVector = false;
                    break;
                }
            }
            if (zeroVector) {
                result.setResult(String.format(Locale.US, ZERO_VECTOR, i));
                return i;
            }
        }
        return -1;
    }

    private boolean checkIfCalculationsAreDone() {
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < i; j++) {
                if (m[i][j] != 0) {
                    return false;
                }
            }
        }
        calculateDeterminantValueByDiagonalLineMultiplication();
        return true;
    }

    private boolean findFarthestVectorToSwapWith(int row) {
        for (int i = row + 1; i < rows; i++) {
            if (m[rows - i][i - 1] != 0) {
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
        double multipleBy = 1 / r[index];
        for (int i = index; i < r.length; i++) {
            r[i] *= multipleBy;
        }
        result.put(String.format(Locale.US, MULTIPLE_VECTOR, index + 1, CalculationResult.doubleToString(multipleBy)), m);
    }

    private double findMultiplicationValue(int rIndex, int nIndex) {
        double[] r = m[rIndex];
        double[] n = m[nIndex];
        return 1.0 / (r[rIndex] / n[rIndex]);
    }

    private void multipleVectorByValue(int index, double value) {
        double[] r = m[index];
        for (int i = index; i < r.length; i++) {
            r[i] *= value;
        }
        result.put(String.format(Locale.US, MULTIPLE_VECTOR, index + 1, CalculationResult.doubleToString(value)), m);
    }

    private void addOrSubtractVectors(int rIndex, int nIndex) {
        double[] r = m[rIndex];
        double[] n = m[nIndex];
        if (r[rIndex] + n[rIndex] == 0) {
            for (int i = rIndex; i < r.length; i++) {
                n[i] += r[i];
            }
            result.put(String.format(Locale.US, ADDED_VECTOR, rIndex + 1, nIndex + 1), m);
        } else {
            for (int i = rIndex; i < r.length; i++) {
                n[i] -= r[i];
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
            res = -1f;
            sb.append("-1 (from row swaps) ");
        } else {
            res = 1f;
        }
        for (int i = 0; i < rows; i++) {
            res *= m[i][i];
            if (i == rows - 1) {
                sb.append(CalculationResult.doubleToString(m[i][i])).append(" = ");
            } else {
                sb.append(CalculationResult.doubleToString(m[i][i])).append(" * ");
            }
        }
        result.setResult(MULTIPLE_DIAGONAL_LINE + sb.toString() +
                CalculationResult.doubleToString(res) + System.lineSeparator());
    }

    private double[] hardCopyVector(double[] r) {
        return Arrays.copyOf(r, r.length);
    }
}
