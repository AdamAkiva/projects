package com.aa.matrix.models;

import java.util.concurrent.Callable;

/**
 * @author Adam Akiva
 * Class used to calculate Inverse matrix of the matrix held in the model
 */
public class InverseMatrix extends BaseModel implements Callable<Calculation> {

    private final Matrix matrix;
    private final Calculation result;

    private static final String RESULT = "Inverse Matrix:" + System.lineSeparator();
    private static final String NO_INVERSE_MATRIX = "No inverse matrix" + System.lineSeparator() +
            "(can't transform inputted matrix" + System.lineSeparator() + "to identity matrix)" +
            System.lineSeparator();

    public InverseMatrix() {
        this.matrix = BaseModel.getInstance().getMatrixObject();
        result = matrix.getResult();
        result.put(BASE_MATRICES, matrix.getMatrix(), matrix.getIdentityMatrix());
    }

    @Override
    public Calculation call() {
        int row = matrix.matrixToLowerEchelonForm();
        if (row != -1) {
            result.setResult(NO_INVERSE_MATRIX);
            return result;
        }
        row = matrix.matrixToReducedRowEchelonForm();
        if (row != -1) {
            result.setResult(NO_INVERSE_MATRIX);
            return result;
        }
        result.setResult(RESULT + System.lineSeparator(), matrix.getIdentityMatrix());
        return result;
    }
}
