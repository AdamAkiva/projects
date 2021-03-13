package com.aa.matrix.models;

import java.util.concurrent.Callable;

// Do this
public class CalculateInverseMatrix extends BaseModel implements Callable<CalculationResult> {

    private final double[][] m;
    private final double[][] iM;
    private final int rows;
    private final int cols;

    private final CalculationResult result;

    public CalculateInverseMatrix(double[][] m, int rows, int cols) {
        this.m = m;
        this.rows = rows;
        this.cols = cols;
        this.iM = buildIdentifyMatrix();
        result = new CalculationResult(this.rows, this.cols);
        result.put(BASE_MATRIX, this.m);
    }

    @Override
    public CalculationResult call() throws Exception {
        return null;
    }

    private double[][] buildIdentifyMatrix() {
        double[][] iM = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i != j) {
                    iM[i][j] = ZERO;
                } else {
                    iM[i][j] = ONE;
                }
            }
        }
        return iM;
    }
}
