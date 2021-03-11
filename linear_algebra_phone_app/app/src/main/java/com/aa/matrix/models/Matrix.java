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

    public static Matrix convertStringArrayToMatrix(String[] matrixValuesString, int rows, int cols) {
        final double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Double.parseDouble(matrixValuesString[j + (i * rows)]);
            }
        }
        return new Matrix(matrix, rows, cols);
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
}
