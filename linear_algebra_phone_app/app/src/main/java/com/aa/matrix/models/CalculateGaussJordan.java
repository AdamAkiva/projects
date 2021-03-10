package com.aa.matrix.models;

import java.util.concurrent.Callable;

// Do this
public class CalculateGaussJordan implements Callable<CalculationResult> {

    private final double[][] m;
    private final int rows;
    private final int cols;
    private final double[] v;

    public CalculateGaussJordan(double[][] m, int rows, int cols, double[] v) {
        this.m = m;
        this.rows = rows;
        this.cols = cols;
        this.v = v;
    }

    @Override
    public CalculationResult call() throws Exception {
        return null;
    }
}
