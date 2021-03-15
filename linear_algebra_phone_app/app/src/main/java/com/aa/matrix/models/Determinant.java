package com.aa.matrix.models;

import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * @author Adam Akiva
 * Class used to calculate a Determinat value of the matrix held in the model
 */
public class Determinant extends BaseModel implements Callable<Calculation> {

    private final Matrix matrix;
    private final Calculation result;

    private static final String TWO_BY_TWO_MATRIX = "Used the shortcut way" + System.lineSeparator()
            + "To calculate 2x2 determinant:" + System.lineSeparator() + "(%s * %s) - (%s * %s) = %s"
            + System.lineSeparator();
    private static final String DETERMINANT = "Determinant =";
    private static final String MULTIPLE_DIAGONAL_LINE = "Multiple the main diagonal line values:" +
            System.lineSeparator();
    private static final String ZERO_VECTOR = "Row %d is a zero row" + System.lineSeparator() +
            "Therefore " + DETERMINANT + "0" + System.lineSeparator();
    private static final String MULTIPLY_BY_NEGATIVE_ONE = "-1 (from row swaps) ";

    public Determinant() {
        this.matrix = BaseModel.getInstance().getMatrixObject();
        result = this.matrix.getResult();
        result.put(BASE_MATRIX, matrix.getMatrix());
    }

    @Override
    public Calculation call() {
        if (matrix.getRows() * matrix.getCols() == 4) {
            twoByTwoMatrix();
            return result;
        }
        int row = matrix.matrixToLowerEchelonForm();
        if (row != -1) {
            result.setResult(String.format(Locale.US, ZERO_VECTOR, row));
            return result;
        }
        calculateDeterminantValueByDiagonalLineMultiplication();
        return result;
    }

    /**
     * Method to calculate the determinant of 2X2 matrix in the shortcut way
     */
    private void twoByTwoMatrix() {
        double[][] m = matrix.getMatrix();
        double value = (m[0][0] * (m[1][1])) - ((m[0][1] * (m[1][0])));
        result.setResult(String.format(Locale.US, TWO_BY_TWO_MATRIX, doubleToString(m[0][0]), doubleToString(m[1][1]),
                doubleToString(m[0][1]), doubleToString(m[1][0]), doubleToString(value)));
    }

    /**
     * Method to set the result of the calculation by multiplying the main diagonal values after
     * the matrix is in made to be in lower echelon form
     */
    private void calculateDeterminantValueByDiagonalLineMultiplication() {
        double res;
        int rows = matrix.getRows();
        double[][] m = matrix.getMatrix();
        StringBuilder sb = new StringBuilder();
        sb.append(DETERMINANT).append(" ");
        // Check if the number of swaps is not even, if so swap the mark of the result
        if (matrix.getRowsSwapCounter() % 2 != 0) {
            res = NEGATIVE_ONE;
            sb.append(MULTIPLY_BY_NEGATIVE_ONE);
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
