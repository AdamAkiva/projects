package com.aa.matrix.models;

public class BaseModel {

    private Matrix matrix;
     private Vector freeColumn;

    private static BaseModel instance;

    public static BaseModel getInstance() {
        if (instance == null) {
            instance = new BaseModel();
        }
        return instance;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public void setFreeColumn(Vector freeColumn) {
        this.freeColumn = freeColumn;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Vector getFreeColumn() {
        return freeColumn;
    }
}
