package com.aa.matrix.models;

import com.aa.matrix.etc.DivideByZeroException;
import com.aa.matrix.etc.InvalidParameterException;

import java.util.concurrent.Callable;

import static com.aa.matrix.views.BaseActivity.NO_SWITCH_VALUE;
import static com.aa.matrix.views.BaseActivity.ONE;
import static com.aa.matrix.views.BaseActivity.ZERO;

public class GaussJorden implements Callable<String[]> {

    private final Matrix matrix;
    private final Vector freeColumn;

    public GaussJorden(final Matrix matrix,final Vector freeColumn) {
        this.matrix = new Matrix(matrix);
        this.freeColumn = new Vector(freeColumn);
    }

    @Override
    public String[] call() throws Pivot.NotFoundException,
            DivideByZeroException, InvalidParameterException {
        canonicalRanking();
        if (matrix.getSwapArray() != null) {
            for (int i = 0; i < matrix.getSwapArray().length; i++) {
                if (matrix.getSwapArray()[i] != NO_SWITCH_VALUE) {
                    final double tempNum = freeColumn.getVectorAsArray()[matrix.getSwapArray()[i]];
                    freeColumn.getVectorAsArray()[matrix.getSwapArray()[i]] =
                            freeColumn.getVectorAsArray()[i];
                    freeColumn.getVectorAsArray()[i] = tempNum;
                }
            }
        }
        return freeColumn.getVectorAsStringArray();
    }

    private void rankMatrix() throws Pivot.NotFoundException,
            DivideByZeroException, InvalidParameterException {
        final int[] swapArray = matrix.orderMatrix(ZERO, ZERO);
        if (swapArray != null) {
            freeColumn.orderVector(swapArray);
        }
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            matrix.setPivot(Pivot.findPivot(matrix.getMatrix(), i));
            if (matrix.getPivot().getValue() != ONE) {
                matrix.divideMatrixRowByValue(matrix.getPivot().getRow(),
                        matrix.getPivot().getValue());
                freeColumn.divideVectorRowByValue(matrix.getPivot().getRow(),
                        matrix.getPivot().getValue());
                matrix.getPivot().setValue(ONE);
            }
            final double[] multiplicationNumbers = matrix.changeMatrixByPivot(matrix.getPivot());
            freeColumn.changeVectorRowByPivot(matrix.getPivot(), multiplicationNumbers);
        }
    }

    private void canonicalRanking() throws Pivot.NotFoundException,
            DivideByZeroException, InvalidParameterException {
        rankMatrix();
        for (int i = matrix.getRowsCount() - ONE; i > 0; i--) {
            matrix.setPivot(Pivot.findPivotReversed(matrix.getMatrix(), i));
            final double[] multiplicationNumbers =
                    matrix.changeRowReversedByPivot(matrix.getPivot());
            freeColumn.changeVectorRowByPivotReversed(matrix.getPivot(),
                    multiplicationNumbers);
        }
    }
}
