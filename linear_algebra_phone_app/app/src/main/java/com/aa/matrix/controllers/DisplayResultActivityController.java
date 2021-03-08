package com.aa.matrix.controllers;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.aa.matrix.R;
import com.aa.matrix.etc.InvalidParameterException;
import com.aa.matrix.models.Determinant;
import com.aa.matrix.models.GaussJorden;
import com.aa.matrix.models.InverseMatrix;
import com.aa.matrix.models.MainModel;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.BaseActivity;
import com.aa.matrix.views.DisplayResultActivityView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.aa.matrix.views.BaseActivity.DETERMINANT;
import static com.aa.matrix.views.BaseActivity.GAUSS_JORDEN;
import static com.aa.matrix.views.BaseActivity.INVERSE_MATRIX;

public class DisplayResultActivityController {

    private final MainModel model = MainModel.getInstance();

    private final DisplayResultActivityView view;
    private final ProgressBar pbLoadingResult;

    private final Matrix matrix;
    private final Vector freeColumn;
    private final int matrixOperation;

    private static final String TAG = DisplayResultActivityController.class.getName();

    public DisplayResultActivityController(DisplayResultActivityView view, int matrixOperation) {
        this.view = view;
        pbLoadingResult = (ProgressBar) view.findViewById(R.id.pbLoadingResult);
        this.matrix = model.getMatrix();
        this.freeColumn = model.getFreeColumn();
        this.matrixOperation = matrixOperation;
        try {
            displayResult();
        } catch (InvalidParameterException | ExecutionException | InterruptedException e) {
            Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
        }
    }

    public void displayResult()
            throws InvalidParameterException, ExecutionException, InterruptedException {
        String[] result = startCalculation();
        view.hideProgressBar();
        if (matrixOperation == DETERMINANT) {
            view.displayDeterminantResult(result, matrix.getMatrixSnapShots());
        } else if (matrixOperation == GAUSS_JORDEN && freeColumn != null) {
            view.displayGaussJordenResult(result, matrix.getMatrixSnapShots());
        } else if (matrixOperation == INVERSE_MATRIX) {
            view.displayInverseMatrixResult(result, matrix.getMatrixSnapShots());
        } else {
            throw new InvalidParameterException("Should not happen");
        }
    }

    private String[] startCalculation()
            throws InvalidParameterException, ExecutionException, InterruptedException {
        Callable<String[]> result;
        if (matrixOperation == DETERMINANT) {
            result = new Determinant(matrix);
        } else if (matrixOperation == GAUSS_JORDEN && freeColumn != null) {
            result = new GaussJorden(matrix, freeColumn);
        } else if (matrixOperation == INVERSE_MATRIX) {
            result = new InverseMatrix(matrix);
        } else {
            throw new InvalidParameterException("Should not happen");
        }
        pbLoadingResult.setVisibility(View.GONE);
        return BaseActivity.getService().submit(result).get();
    }
}
