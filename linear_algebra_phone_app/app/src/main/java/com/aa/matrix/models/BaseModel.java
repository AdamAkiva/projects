package com.aa.matrix.models;

public class BaseModel {

    private static BaseModel instance;
    private Matrix matrix;
    private Vector freeColumn;

    public static BaseModel getInstance() {
        if (instance == null) {
            instance = new BaseModel();
        }
        return instance;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Vector getFreeColumn() {
        return freeColumn;
    }

    public void setFreeColumn(Vector freeColumn) {
        this.freeColumn = freeColumn;
    }
}
