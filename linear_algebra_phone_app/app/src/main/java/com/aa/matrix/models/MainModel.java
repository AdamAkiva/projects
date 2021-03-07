package com.aa.matrix.models;

import com.aa.matrix.views.BaseActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainModel {

    private Matrix matrix;

    public static MainModel instance;

    public static MainModel getInstance() {
        if (instance == null) {
            instance = new MainModel();
        }
        return instance;
    }

    public Matrix getMatrix() {
        if (matrix != null) {
            return matrix;
        }
        return null;
    }

    public ArrayList<MatrixSnapShot> getMatrixSnapShots() {
        return matrix.getMatrixSnapShots();
    }

    public String calculateDeterminant(String[] matrixValues, int rows, int columns)
            throws ExecutionException, InterruptedException {
        matrix = Matrix.convertStringArrayToMatrix(matrixValues, rows, columns);
        Determinant determinant = new Determinant(matrix);
        return BaseActivity.getService().submit(determinant).get();
    }

    public Vector calculateGaussJorden(String[] matrixValues, int rows, int columns, Vector freeVector)
            throws ExecutionException, InterruptedException {
        matrix = Matrix.convertStringArrayToMatrix(matrixValues, rows, columns);
        GaussJorden gaussJorden = new GaussJorden(matrix, freeVector);
        return BaseActivity.getService().submit(gaussJorden).get();
    }

    public Matrix calculateInverseMatrix(String[] matrixValues, int rows, int columns)
            throws ExecutionException, InterruptedException {
        matrix = Matrix.convertStringArrayToMatrix(matrixValues, rows, columns);
        InverseMatrix inverseMatrix = new InverseMatrix(matrix);
        return BaseActivity.getService().submit(inverseMatrix).get();
    }
}
