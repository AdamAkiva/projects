package com.aa.matrix.models;

import java.util.Locale;
import java.util.concurrent.Callable;

public class CalculateGaussJordan extends BaseModel implements Callable<CalculationResult> {

    private static final String NO_SOLUTIONS = "No solutions" + System.lineSeparator() +
            "(R%d is a non-true equation)" + System.lineSeparator();
    private static final String MULTIPLIED_ROW = "R%d = R%d * %s" + System.lineSeparator();
    private static final String ADDED_VECTORS = "R%d = R%d + (%s * R%d)" + System.lineSeparator();
    private static final String SUBTRACTED_VECTORS = "R%d = R%d - (%s * R%d)" + System.lineSeparator();
    private static final String SWAPPED_VECTORS = "R%d <-> R%d" + System.lineSeparator();
    private final int rows;
    private final int variables;
    private final String freeVarName;
    private final CalculationResult result;
    private double[][] m;
    private int cols;

    public CalculateGaussJordan(double[][] m, int rows, int cols, double[] v, String freeVarName) {
        this.m = m;
        this.rows = rows;
        this.cols = cols;
        this.freeVarName = freeVarName;
        attachFreeVectorToMatrix(v);
        result = new CalculationResult(this.rows, this.cols);
        result.put(BASE_MATRIX, this.m);
        variables = cols;
    }

    @Override
    public CalculationResult call() {
        // If after ranking to lower echelon there is a contradict equation
        if (matrixToLowerEchelonForm()) {
            return result;
        }
        // If after ranking to diagonal matrix there is a contradict equation
        if (matrixToReducedRowEchelonForm()) {
            return result;
        }
        // Build a string result to display to the user
        findResults();
        return result;
    }

    // Lower echelon means all elements below the main diagonal line are zeroes.
    private boolean matrixToLowerEchelonForm() {
        int i;
        for (i = 0; i < rows - 1; i++) {
            findMaxAbsoluteValuePivotToSwapWith(i, m[i][i]);
            multiplePivotRowByValue(i);
            rankByPivot(i, false);
        }
        multiplePivotRowByValue(i);
        return checkForNoSolution();
    }

    // Reduced lower echelon means the matrix is a diagonal matrix
    private boolean matrixToReducedRowEchelonForm() {
        for (int i = rows - 1; i > 0; i--) {
            if (!checkForZeroVector(i)) {
                rankByPivot(i, true);
            }
        }
        return checkForNoSolution();
    }

    private void findResults() {
        int numberOfSolutions = 0;
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            if (!checkForZeroVector(i)) {
                numberOfSolutions++;
            }
        }
        if (numberOfSolutions == variables) {
            buildSingleSolutionStringResult(resultString);
        } else {
            buildInfiniteSolutionsStringResult(resultString, numberOfSolutions);
        }
        result.setResult(resultString.toString());
    }

    private void buildSingleSolutionStringResult(StringBuilder resultString) {
        for (int i = 0; i < variables; i++) {
            String value = doubleToString(m[i][cols - 1]);
            resultString.append(freeVarName).append(i + 1).append(" = ").append(value).append(System.lineSeparator());
        }
    }

    private void buildInfiniteSolutionsStringResult(StringBuilder resultString, int numberOfSolutions) {
        for (int i = 0; i < numberOfSolutions; i++) {
            StringBuilder values = new StringBuilder();
            for (int j = i + 1; j < cols; j++) {
                // Free variables
                if (j != cols - 1 && m[i][j] != ZERO) {
                    double value = m[i][j] * NEGATIVE_ONE;
                    if (value > 0) {
                        values.append(" +").append(doubleToString(value)).append(freeVarName).append(j + 1);
                    } else {
                        values.append(" ").append(doubleToString(value)).append(freeVarName).append(j + 1);
                    }

                    // Free vector
                } else if (j == cols - 1 && m[i][j] != ZERO) {
                    double value = m[i][j];
                    if (value > 0) {
                        values.append(" +").append(doubleToString(m[i][j]));
                    } else {
                        values.append(" ").append(value);
                    }
                }
            }
            resultString.append(freeVarName).append(i + 1).append(" = ").append(values.toString()).append(System.lineSeparator());
        }
        for (int i = numberOfSolutions; i < variables; i++) {
            resultString.append(freeVarName).append(i + 1).append(" = Free").append(System.lineSeparator());
        }
    }

    private boolean checkForNoSolution() {
        for (int i = 0; i < rows; i++) {
            if (checkForZeroVector(i)) {
                if (round(m[i][cols - 1]) != ZERO) {
                    result.setResult(String.format(Locale.US, NO_SOLUTIONS, i + 1));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkForZeroVector(int rIndex) {
        for (int i = 0; i < cols - 1; i++) {
            if (round(m[rIndex][i]) != ZERO) {
                return false;
            }
        }
        return true;
    }

    private void attachFreeVectorToMatrix(double[] v) {
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
        cols++;
        m = nM;
    }

    private void multiplePivotRowByValue(int pIndex) {
        double pivot = m[pIndex][pIndex];
        double dValue;
        if (round(pivot) != ZERO && round(pivot) != ZERO) {
            dValue = round(ONE / pivot);
        } else {
            return;
        }
        for (int i = 0; i < cols; i++) {
            if (round(m[pIndex][i]) != ZERO) {
                m[pIndex][i] = round(m[pIndex][i] * dValue);
            }
        }
        result.put(String.format(Locale.US, MULTIPLIED_ROW, pIndex + 1, pIndex + 1,
                doubleToString(dValue)), m);
    }

    private void rankByPivot(int pIndex, boolean reversed) {
        double[] pivotRow = Vector.hardCopyVector(m[pIndex]);

        for (int i = !reversed ? pIndex + 1 : pIndex - 1; !reversed ? i < rows : i > -1; i += !reversed ? 1 : -1) {
            double[] compareRow = m[i];
            double cValue = round(findMultiplicationValue(pivotRow, compareRow, pIndex));
            if (round(cValue) == ZERO) {
                continue;
            }
            addVectors(pivotRow, compareRow, cValue);
            if (cValue > 0) {
                result.put(String.format(Locale.US, ADDED_VECTORS, i + 1, i + 1,
                        doubleToString(Math.abs(cValue)), pIndex + 1), m);
            } else {
                result.put(String.format(Locale.US, SUBTRACTED_VECTORS, i + 1, i + 1,
                        doubleToString(Math.abs(cValue)), pIndex + 1), m);
            }
        }
    }

    private void addVectors(double[] v1, double[] v2, double cValue) {
        for (int i = 0; i < cols; i++) {
            if (round(v1[i]) != ZERO) {
                double roundedMultiplication = round(v1[i] * cValue);
                // Needs another rounding if there's negative number + positive number
                v2[i] = round(v2[i] + roundedMultiplication);
            }
        }
    }

    private double findMultiplicationValue(double[] pivotRow, double[] compareRow, int pIndex) {
        double res = round(ONE / round(pivotRow[pIndex] / compareRow[pIndex]));
        return round(pivotRow[pIndex] * res) + compareRow[pIndex] == 0 ?
                res : NEGATIVE_ONE * res;
    }

    private void findMaxAbsoluteValuePivotToSwapWith(int rIndex, double cValue) {
        int rSwap = -1;
        double maxValue = Math.abs(cValue);
        for (int i = rIndex + 1; i < rows; i++) {
            if (Math.abs(m[i][rIndex]) > maxValue) {
                rSwap = i;
                maxValue = Math.abs(m[i][rIndex]);
            }
        }
        if (rSwap != -1) {
            swapRows(rIndex, rSwap);
        }
    }

    private void swapRows(int rIndex, int rSwap) {
        double[] t = m[rIndex];
        m[rIndex] = m[rSwap];
        m[rSwap] = t;
        result.put(String.format(Locale.US, SWAPPED_VECTORS, rIndex + 1, rSwap + 1), m);
    }
}
