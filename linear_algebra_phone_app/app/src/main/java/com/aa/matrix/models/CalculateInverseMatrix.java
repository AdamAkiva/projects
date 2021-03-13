package com.aa.matrix.models;

import java.util.concurrent.Callable;

// Do this
public class CalculateInverseMatrix extends BaseModel implements Callable<CalculationResult> {

    private final double[][] m;
    private final int rows;
    private final int cols;

    public CalculateInverseMatrix(double[][] m, int rows, int cols) {
        this.m = m;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public CalculationResult call() throws Exception {
        return null;
    }
}
