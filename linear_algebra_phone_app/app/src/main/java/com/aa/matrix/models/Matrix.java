package com.aa.matrix.models;

public class Matrix extends BaseModel {

    private final double[][] matrix;
    private final int rows;
    private final int cols;

    public Matrix(final double[][] matrix, final int rowsCount, final int columnCount) {
        this.matrix = matrix;
        this.rows = rowsCount;
        this.cols = columnCount;
    }

    public static Matrix convertStringArrayToMatrix(String[] matrixValuesString, int rows, int cols) {
        final double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = Double.parseDouble(matrixValuesString[j + (i * cols)]);
            }
        }
        return new Matrix(matrix, rows, cols);
    }

    public double[][] getMatrixAs2DArray() {
        return matrix;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
