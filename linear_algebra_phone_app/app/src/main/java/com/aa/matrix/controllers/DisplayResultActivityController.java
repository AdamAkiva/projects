package com.aa.matrix.controllers;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.aa.matrix.R;
import com.aa.matrix.etc.InvalidParameterException;
import com.aa.matrix.models.CalculateDeterminant;
import com.aa.matrix.models.CalculateGaussJordan;
import com.aa.matrix.models.CalculateInverseMatrix;
import com.aa.matrix.models.CalculationResult;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.DisplayResultActivityView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.aa.matrix.views.BaseActivity.DETERMINANT;
import static com.aa.matrix.views.BaseActivity.GAUSS_JORDAN;
import static com.aa.matrix.views.BaseActivity.INVERSE_MATRIX;

public class DisplayResultActivityController extends BaseController {

    private final DisplayResultActivityView view;
    private final ProgressBar pbLoadingResult;

    private final Matrix matrix;
    @Nullable
    private final Vector freeColumn;
    private final int matrixOperation;

    private static final String TAG = DisplayResultActivityController.class.getName();

    public DisplayResultActivityController(DisplayResultActivityView view, int matrixOperation) {
        this.view = view;
        pbLoadingResult = view.findViewById(R.id.pbLoadingResult);
        this.matrixOperation = matrixOperation;
        this.matrix = BaseModel.getInstance().getMatrix();
        if (this.matrixOperation == GAUSS_JORDAN) {
            freeColumn = BaseModel.getInstance().getFreeColumn();
        } else {
            freeColumn = null;
        }
        try {
            displayResult();
        } catch (InvalidParameterException | ExecutionException | InterruptedException e) {
            Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
        }
    }

    public void displayResult() throws ExecutionException, InterruptedException,
            InvalidParameterException {
        CalculationResult result = startCalculation();
        view.hideProgressBar();
        if (matrixOperation == DETERMINANT) {
            view.displayDeterminantResult(result);
        } else if (matrixOperation == GAUSS_JORDAN) {
            view.displayGaussJordanResult(result);
        } else if (matrixOperation == INVERSE_MATRIX) {
            view.displayInverseMatrixResult(result);
        } else {
            throw new InvalidParameterException("Should not happen");
        }
    }

    private CalculationResult startCalculation() throws ExecutionException, InterruptedException,
            InvalidParameterException {
        Callable<CalculationResult> result;
        if (matrixOperation == DETERMINANT) {
            result = new CalculateDeterminant(matrix.getMatrix(), matrix.getRowsCount(),
                    matrix.getColumnCount());
        } else if (matrixOperation == GAUSS_JORDAN) {
            result = new CalculateGaussJordan(matrix.getMatrix(), matrix.getRowsCount(),
                    matrix.getColumnCount(), freeColumn.getVector());
        } else if (matrixOperation == INVERSE_MATRIX) {
            result = new CalculateInverseMatrix(matrix.getMatrix(), matrix.getRowsCount(),
                    matrix.getColumnCount());
        } else {
            throw new InvalidParameterException("Should not happen");
        }
        pbLoadingResult.setVisibility(View.GONE);
        return getService().submit(result).get();
    }
}
