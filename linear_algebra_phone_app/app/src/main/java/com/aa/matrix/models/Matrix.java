package com.aa.matrix.models;

import com.aa.matrix.etc.DivideByZeroException;
import com.aa.matrix.etc.InvalidParameterException;

import java.util.ArrayList;

import static com.aa.matrix.views.BaseActivity.CHANGE_ROW_BY_PIVOT;
import static com.aa.matrix.views.BaseActivity.DIVIDE_BY_ZERO_ERROR;
import static com.aa.matrix.views.BaseActivity.MULTIPLICATION;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ONE;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ZERO_DOUBLE;
import static com.aa.matrix.views.BaseActivity.ONE;
import static com.aa.matrix.views.BaseActivity.SWAP;
import static com.aa.matrix.views.BaseActivity.TRANSPOSE;
import static com.aa.matrix.views.BaseActivity.VECTORS_SIZE_ERROR;
import static com.aa.matrix.views.BaseActivity.ZERO;
import static com.aa.matrix.views.BaseActivity.ZERO_DOUBLE;

public class Matrix {

    private double[][] matrix;
    private final int rowsCount;
    private final int columnCount;
    private final int diagonalLineSize;
    private Pivot pivot;

    private int[] swapArray;
    private int determinantOuterValues;

    private final ArrayList<MatrixSnapShot> matrixSnapShots;
    private int snapShotIndex;

    public Matrix(final double[][] matrix, final int rowsCount, final int columnCount) {
        this.matrix = matrix;
        this.rowsCount = rowsCount;
        this.columnCount = columnCount;
        this.diagonalLineSize = rowsCount;
        this.determinantOuterValues = ONE;
        matrixSnapShots = new ArrayList<MatrixSnapShot>();
        snapShotIndex = ZERO;
    }

    public Matrix(final Vector[] vectors, final int vectorsNumber) throws
            InvalidParameterException {
        matrix = new double[vectorsNumber][];
        for (int i = 0; i < vectorsNumber; i++) {
            this.matrix[i] = vectors[i].getVectorAsArray();
        }
        this.rowsCount = vectorsNumber;
        this.columnCount = vectors[ZERO].getVectorAsArray().length;
        int i;
        for (i = 0; i < vectors.length; i++) {
            if (!vectors[i].equalSize(vectors[i + ONE])) {
                throw new InvalidParameterException(VECTORS_SIZE_ERROR);
            }
        }
        this.diagonalLineSize = rowsCount;
        this.determinantOuterValues = ONE;
        matrixSnapShots = new ArrayList<MatrixSnapShot>();
        snapShotIndex = ZERO;
    }

    public Matrix(final Matrix matrix) {
        this.matrix = new double[matrix.getRowsCount()][matrix.getColumnCount()];
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            for (int j = 0; j < matrix.getColumnCount(); j++) {
                this.matrix[i][j] = matrix.getMatrix()[i][j];
            }
        }
        this.rowsCount = matrix.getRowsCount();
        this.columnCount = matrix.getColumnCount();
        this.diagonalLineSize = matrix.getRowsCount();
        this.determinantOuterValues = ONE;
        if (pivot != null) {
            this.pivot = matrix.getPivot();
        }
        if (swapArray != null) {
            this.swapArray = matrix.getSwapArray();
        }
        this.matrixSnapShots = matrix.getMatrixSnapShots();

        // When you get a chance look why this is required instead of without an if
        if (matrix.getMatrixSnapShots().size() == ZERO) {
            this.snapShotIndex = ZERO;
        } else {
            this.snapShotIndex = matrix.getMatrixSnapShots().size() - 1;
        }
    }

    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    public Vector[] getAsVectorsArray() {
        final Vector[] vectors = new Vector[rowsCount];
        for (int i = 0; i < rowsCount; i++) {
            vectors[i] = new Vector(matrix[i]);
        }
        return vectors;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getDiagonalLineSize() {
        return diagonalLineSize;
    }

    public double getDeterminantOuterValues() {
        return determinantOuterValues;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public int[] getSwapArray() {
        return swapArray;
    }

    public ArrayList<MatrixSnapShot> getMatrixSnapShots() {
        return matrixSnapShots;
    }

    public void transposeMatrix() throws InvalidParameterException {
        final double[][] matrix = new double[columnCount][rowsCount];
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                matrix[j][i] = this.matrix[i][j];
            }
        }
        this.matrix = matrix;
        matrixSnapShots.add(snapShotIndex++, new MatrixSnapShot(this,
                new MatrixAction(TRANSPOSE)));
    }

    public void divideMatrixRowByValue(int row, double value) throws DivideByZeroException,
            InvalidParameterException {
        if (value != ZERO) {
            determinantOuterValues *= value;
            for (int i = 0; i < columnCount; i++) {
                if (matrix[row][i] != ZERO) {
                    matrix[row][i] /= value;
                }
            }
            matrixSnapShots.add(snapShotIndex++, new MatrixSnapShot(this,
                    new MatrixAction(MULTIPLICATION, value, row)));
        } else {
            throw new DivideByZeroException(DIVIDE_BY_ZERO_ERROR);
        }
    }

    public int[] orderMatrix(int rowNumber, int columnNumber) throws InvalidParameterException {
        int skipLinesAtTheEnd = ZERO;
        for (int i = rowNumber; i < rowsCount - rowNumber; i++) {
            if (matrix[i][columnNumber] == ZERO) {
                swapArray = new int[rowsCount - rowNumber];
                for (int j = rowsCount - skipLinesAtTheEnd - ONE; j > i; j--) {
                    if (matrix[j][columnNumber] != ZERO) {
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
                    double[] tempArr = matrix[swapArray[i]];
                    matrix[swapArray[i]] = matrix[i];
                    matrix[i] = tempArr;
                    determinantOuterValues *= NEGATIVE_ONE;
                    matrixSnapShots.add(snapShotIndex++, new MatrixSnapShot(this,
                            new MatrixAction(SWAP, i + ONE, swapArray[i] + ONE)));
                }
            }
        }
        return swapArray;
    }

    public double[] changeMatrixByPivot(Pivot pivot) throws InvalidParameterException {
        final int pivotRow = pivot.getRow();
        final int pivotColumn = pivot.getColumn();
        final double pivotValue = pivot.getValue();
        final double[] multiplicationNumbers = new double[rowsCount - pivotRow - ONE];
        for (int i = pivotRow + ONE, k = 0; i < rowsCount; i++, k++) {
            final double multiplicationNumber = matrix[i][pivotColumn] * NEGATIVE_ONE / pivotValue;
            multiplicationNumbers[k] = multiplicationNumber;
            if (multiplicationNumber !=  ZERO_DOUBLE &&
                    multiplicationNumber != NEGATIVE_ZERO_DOUBLE) {
                for (int j = pivotColumn; j < columnCount; j++) {
                    if (matrix[pivotRow][j] != ZERO) {
                        matrix[i][j] =
                                matrix[i][j] + matrix[pivotRow][j] * multiplicationNumber;
                    }
                }
                matrixSnapShots.add(snapShotIndex++, new MatrixSnapShot(this,
                        new MatrixAction(CHANGE_ROW_BY_PIVOT,Math.abs(multiplicationNumber),
                                i + ONE, pivotRow + ONE)));
            }
        }
        return multiplicationNumbers;
    }

    public double[] changeRowReversedByPivot(Pivot pivot) throws InvalidParameterException {
        final int pivotRow = pivot.getRow();
        final int pivotColumn = pivot.getColumn();
        final double [] multiplicationNumbers = new double[pivotRow];
        for (int i = pivotRow - ONE; i >= 0; i--) {
            if (matrix[i][pivotColumn] != ZERO_DOUBLE) {
                final double multiplicationNumber = matrix[i][pivotColumn] * NEGATIVE_ONE;
                multiplicationNumbers[i] = multiplicationNumber;
                matrix[i][pivotColumn] += multiplicationNumber;
                matrixSnapShots.add(snapShotIndex++, new MatrixSnapShot(this,
                        new MatrixAction(CHANGE_ROW_BY_PIVOT, Math.abs(multiplicationNumber),
                                i + ONE, pivotRow + ONE)));
            }
        }
        return multiplicationNumbers;
    }

    public static Matrix convertStringArrayToMatrix(String[] matrixValuesString, int rows, int columns) {
        final double[][] matrixValues = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrixValues[i][j] = Double.parseDouble(matrixValuesString[j + (i * rows)]);
            }
        }
        return new Matrix(matrixValues, rows, columns);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                str.append(String.format("[%s] ", Vector.roundDoubleValue(matrix[i][j])));
            }
            str.append("\n");
        }
        return str.toString();
    }
}
