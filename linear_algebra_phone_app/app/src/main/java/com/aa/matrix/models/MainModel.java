package com.aa.matrix.models;

import com.aa.matrix.views.BaseActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainModel {

    private Matrix matrix;
    // private Vector freeColumn;

    private static MainModel instance;

    public static MainModel getInstance() {
        if (instance == null) {
            instance = new MainModel();
        }
        return instance;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

//    public void setFreeColumn(Vector freeColumn) {
//        this.freeColumn = freeColumn;
//    }

    public Matrix getMatrix() {
        return matrix;
    }

//    public Vector getFreeColumn() {
//        return freeColumn;
//    }
}
