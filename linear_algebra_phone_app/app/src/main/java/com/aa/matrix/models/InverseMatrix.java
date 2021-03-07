package com.aa.matrix.models;

import com.aa.matrix.etc.DivideByZeroException;
import com.aa.matrix.etc.InvalidParameterException;

import java.util.concurrent.*;

import static com.aa.matrix.views.BaseActivity.ADD_ROW_BY_PIVOT;
import static com.aa.matrix.views.BaseActivity.INVERTIBLE_MATRIX;
import static com.aa.matrix.views.BaseActivity.NEGATIVE;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ONE;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ZERO_DOUBLE;
import static com.aa.matrix.views.BaseActivity.ONE;
import static com.aa.matrix.views.BaseActivity.POSITIVE;
import static com.aa.matrix.views.BaseActivity.SWAPPED_ROW;
import static com.aa.matrix.views.BaseActivity.ZERO;
import static com.aa.matrix.views.BaseActivity.ZERO_DOUBLE;

public class InverseMatrix implements Callable<Matrix> {

    private final Matrix matrix;
    private final Matrix unitMatrix;

    private final StringBuilder stepByStep;

    public InverseMatrix(final Matrix matrix) {
        this.matrix = new Matrix(matrix);
        this.unitMatrix = createMatchingUnitMatrix();
        this.stepByStep = new StringBuilder();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    private Matrix createMatchingUnitMatrix() {
        final double[][] unitMatrix =
                new double[matrix.getRowsCount()][matrix.getColumnCount()];
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            unitMatrix[i][i] = ONE;
        }
        return new Matrix(unitMatrix, matrix.getRowsCount(), matrix.getColumnCount());
    }

    private void orderMatrix(Matrix unitMatrix, int rowNumber, int columnNumber) {
        int skipLinesAtTheEnd = ZERO;
        final int[] swapArray = new int[matrix.getRowsCount() - rowNumber];
        for (int i = rowNumber; i < matrix.getRowsCount() - rowNumber; i++) {
            if (matrix.getMatrix()[i][columnNumber] == ZERO) {
                for (int j = matrix.getRowsCount() - skipLinesAtTheEnd - ONE; j > i; j--) {
                    if (matrix.getMatrix()[j][columnNumber] != ZERO) {
                        swapArray[i] = j;
                        skipLinesAtTheEnd++;
                        break;
                    }
                }
            }
        }
        if (skipLinesAtTheEnd > ZERO) {
            for (int i = 0; i < swapArray.length; i++) {
                if (swapArray[i] != ZERO) {
                    final double[] tempArr1 = matrix.getMatrix()[swapArray[i]];
                    final double[] tempArr2 = unitMatrix.getMatrix()[swapArray[i]];
                    matrix.getMatrix()[swapArray[i]] = matrix.getMatrix()[i];
                    matrix.getMatrix()[i] = tempArr1;
                    unitMatrix.getMatrix()[swapArray[i]] = unitMatrix.getMatrix()[i];
                    unitMatrix.getMatrix()[i] = tempArr2;
                    stepByStep.append(String.format(SWAPPED_ROW, String.valueOf(i + ONE),
                            String.valueOf(swapArray[i] + ONE)));
                    stepByStep.append(toString());
                }
            }
        }
    }

    private void changeMatrixByPivot(Pivot pivot, Matrix unitMatrix) {
        final int pivotRow = pivot.getRow();
        final int pivotColumn = pivot.getColumn();
        final double pivotValue = pivot.getValue();
        for (int i = pivotRow + ONE; i < matrix.getRowsCount(); i++) {
            final double multiplicationNumber =
                    matrix.getMatrix()[i][pivotColumn] * NEGATIVE_ONE / pivotValue;
            if (multiplicationNumber != ZERO_DOUBLE &&
                    multiplicationNumber != NEGATIVE_ZERO_DOUBLE) {
                for (int j = pivotColumn; j < matrix.getColumnCount(); j++) {
                    if (matrix.getMatrix()[pivotRow][j] != ZERO) {
                        matrix.getMatrix()[i][j] =
                                matrix.getMatrix()[pivotRow][j] * multiplicationNumber +
                                        matrix.getMatrix()[i][j];
                    }
                }
                for (int j = pivotColumn; j < matrix.getColumnCount(); j++) {
                    if (unitMatrix.getMatrix()[pivotRow][j] != ZERO) {
                        unitMatrix.getMatrix()[i][j] =
                                unitMatrix.getMatrix()[pivotRow][j] * multiplicationNumber
                                        + unitMatrix.getMatrix()[i][j];
                    }
                }
                if (! Vector.roundDoubleValue(multiplicationNumber).contains(NEGATIVE)) {
                    stepByStep.append(String.format(ADD_ROW_BY_PIVOT, String.valueOf(i + ONE),
                            POSITIVE, Vector.roundDoubleValue(Math.abs(multiplicationNumber)),
                            String.valueOf(pivotRow + ONE)));
                } else {
                    stepByStep.append(String.format(ADD_ROW_BY_PIVOT, String.valueOf(i),
                            NEGATIVE, Vector.roundDoubleValue(Math.abs(multiplicationNumber)),
                            String.valueOf(pivotRow)));
                }
                stepByStep.append(toString());
            }
        }
    }

    private void changeRowReversedByPivot(Pivot pivot, Matrix unitMatrix) {
        final int pivotRow = pivot.getRow();
        final int pivotColumn = pivot.getColumn();
        for (int i = pivotRow - ONE; i >= 0; i--) {
            if (matrix.getMatrix()[i][pivotColumn] != ZERO_DOUBLE) {
                final double multiplicationNumber = matrix.getMatrix()[i][pivotColumn] * NEGATIVE_ONE;
                matrix.getMatrix()[i][pivotColumn] += multiplicationNumber;
                for (int j = 0; j < matrix.getColumnCount(); j++) {
                    if (unitMatrix.getMatrix()[pivotRow][j] != ZERO) {
                        unitMatrix.getMatrix()[i][j] =
                                unitMatrix.getMatrix()[pivotRow][j] * multiplicationNumber
                                        + unitMatrix.getMatrix()[i][j];
                    }
                }
                if (! Vector.roundDoubleValue(multiplicationNumber).contains(NEGATIVE)) {
                    stepByStep.append(String.format(ADD_ROW_BY_PIVOT, String.valueOf(i + ONE),
                            POSITIVE,
                            Vector.roundDoubleValue(Math.abs(multiplicationNumber)),
                            String.valueOf(pivotRow + ONE)));
                    stepByStep.append(toString());
                } else {
                    stepByStep.append(String.format(ADD_ROW_BY_PIVOT, String.valueOf(i + ONE),
                            NEGATIVE,
                            Vector.roundDoubleValue(Math.abs(multiplicationNumber)),
                            String.valueOf(pivotRow + ONE)));
                    stepByStep.append(toString());
                }
            }
        }
    }

    private void rankMatrix(Matrix unitMatrix) throws Pivot.NotFoundException,
            DivideByZeroException, InvalidParameterException {
        orderMatrix(unitMatrix, ZERO, ZERO);
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            matrix.setPivot(Pivot.findPivot(matrix.getMatrix(), i));
            if (matrix.getPivot().getValue() != ONE) {
                matrix.divideMatrixRowByValue(matrix.getPivot().getRow(),
                        matrix.getPivot().getValue());
                unitMatrix.divideMatrixRowByValue(matrix.getPivot().getRow(),
                        matrix.getPivot().getValue());
                matrix.getPivot().setValue(ONE);
            }
            changeMatrixByPivot(matrix.getPivot(), unitMatrix);
        }
    }

    private void canonicalRanking(Matrix unitMatrix) throws Pivot.NotFoundException,
            DivideByZeroException, InvalidParameterException {
        rankMatrix(unitMatrix);
        for (int i = matrix.getRowsCount() - ONE; i > 0; i--) {
            matrix.setPivot(Pivot.findPivotReversed(matrix.getMatrix(), i));
            changeRowReversedByPivot(matrix.getPivot(), unitMatrix);
        }
    }

    @Override
    public Matrix call() throws DivideByZeroException, Pivot.NotFoundException, InvalidParameterException {
        canonicalRanking(unitMatrix);
        return unitMatrix;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            for (int j = 0; j < matrix.getColumnCount(); j++) {
                str.append(String.format("[%s] ",
                        Vector.roundDoubleValue(matrix.getMatrix()[i][j])));
            }
            str.append("\n");
        }
        str.append(INVERTIBLE_MATRIX + "\n");
        for (int i = 0; i < unitMatrix.getRowsCount(); i++) {
            for (int j = 0; j < unitMatrix.getColumnCount(); j++) {
                str.append(String.format("[%s] ",
                        Vector.roundDoubleValue(unitMatrix.getMatrix()[i][j])));
            }
            str.append("\n");
        }
        return str.toString();
    }
}
