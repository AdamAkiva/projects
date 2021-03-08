package com.aa.matrix.models;

import com.aa.matrix.etc.DivideByZeroException;
import com.aa.matrix.etc.InvalidParameterException;

import java.util.concurrent.Callable;

import static com.aa.matrix.views.BaseActivity.DEFAULT_DETERMINANT_VALUE;
import static com.aa.matrix.views.BaseActivity.NEGATIVE_ZERO_DOUBLE;
import static com.aa.matrix.views.BaseActivity.ONE;
import static com.aa.matrix.views.BaseActivity.ZERO;
import static com.aa.matrix.views.BaseActivity.ZERO_DOUBLE;

public class Determinant implements Callable<String[]> {

    private final Matrix matrix;

    public Determinant(final Matrix matrix) {
        this.matrix = new Matrix(matrix);
    }

    @Override
    public String[] call() throws Pivot.NotFoundException, DivideByZeroException, InvalidParameterException {
        if (checkForZeroVectorInMatrix()) {
            return new Vector(new double[] {DEFAULT_DETERMINANT_VALUE}).getVectorAsStringArray();
        } else {
            rankMatrix();
            double res = ONE;
            for (int i = 0; i < matrix.getDiagonalLineSize(); i++) {
                res *= matrix.getMatrix()[i][i];
            }
            if (res ==  ZERO_DOUBLE ||
                    res == NEGATIVE_ZERO_DOUBLE) {
                return new Vector(new double[] {ZERO_DOUBLE}).getVectorAsStringArray();
            }
            return new Vector(new double[] {res * matrix.getDeterminantOuterValues()}).getVectorAsStringArray();
        }
    }

    private void rankMatrix() throws DivideByZeroException, Pivot.NotFoundException, InvalidParameterException {
        matrix.orderMatrix(ZERO, ZERO);
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            matrix.setPivot(Pivot.findPivot(matrix.getMatrix(), i));
            if (matrix.getPivot().getValue() != ONE) {
                matrix.divideMatrixRowByValue(matrix.getPivot().getRow(),
                        matrix.getPivot().getValue());
                matrix.getPivot().setValue(ONE);
            }
            matrix.changeMatrixByPivot(matrix.getPivot());
        }
    }

    private boolean checkForZeroVectorInMatrix() throws InvalidParameterException {
        final Matrix temp = new Matrix(matrix.getMatrix(), matrix.getRowsCount(),
                matrix.getColumnCount());
        for (int i = 0; i < temp.getRowsCount(); i++) {
            if (Vector.checkIfZeroVector(new Vector(temp.getMatrix()[i]))) {
                return true;
            }
        }
        matrix.transposeMatrix();
        for (int i = 0; i < temp.getRowsCount(); i++) {
            if (Vector.checkIfZeroVector(new Vector(temp.getMatrix()[i]))) {
                return true;
            }
        }
        return false;
    }
}
