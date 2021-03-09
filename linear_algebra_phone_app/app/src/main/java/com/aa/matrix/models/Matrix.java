package com.aa.matrix.models;

public class Matrix {

    private final double[][] matrix;
    private final int rowsCount;
    private final int columnCount;

    public Matrix(final double[][] matrix, final int rowsCount, final int columnCount) {
        this.matrix = matrix;
        this.rowsCount = rowsCount;
        this.columnCount = columnCount;
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

    public static Matrix convertStringArrayToMatrix(String[] matrixValuesString, int rows, int columns) {
        final double[][] matrix = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = Double.parseDouble(matrixValuesString[j + (i * rows)]);
            }
        }
        return new Matrix(matrix, rows, columns);
    }
}
