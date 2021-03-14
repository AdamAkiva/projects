package com.aa.matrix.controllers;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.aa.matrix.R;
import com.aa.matrix.etc.InvalidParameterException;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Calculation;
import com.aa.matrix.models.Determinant;
import com.aa.matrix.models.GaussJordan;
import com.aa.matrix.models.InverseMatrix;
import com.aa.matrix.views.ResultActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ResultActivityController extends BaseController {

    private static final String TAG = ResultActivityController.class.getName();
    private final ResultActivity view;
    private final ProgressBar pbLoadingResult;
    private final int matrixOperation;

    public ResultActivityController(ResultActivity view) {
        this.view = view;
        pbLoadingResult = view.findViewById(R.id.pbLoadingResult);
        this.matrixOperation = BaseModel.getInstance().getMatrixObject().getCalculationType();
        try {
            displayResult();
        } catch (InvalidParameterException | ExecutionException | InterruptedException e) {
            Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
        }
    }

    public void displayResult() throws ExecutionException, InterruptedException,
            InvalidParameterException {
        Calculation result = startCalculation();
        view.hideProgressBar();
        if (matrixOperation == Calculation.DETERMINANT) {
            view.displayDeterminantResult(result);
        } else if (matrixOperation == Calculation.GAUSS_JORDAN) {
            view.displayGaussJordanResult(result);
        } else if (matrixOperation == Calculation.INVERSE_MATRIX) {
            view.displayInverseMatrixResult(result);
        } else {
            throw new InvalidParameterException("Should not happen");
        }
    }

    private Calculation startCalculation() throws ExecutionException, InterruptedException,
            InvalidParameterException {
        Callable<Calculation> result;
        if (matrixOperation == Calculation.DETERMINANT) {
            result = new Determinant();
        } else if (matrixOperation == Calculation.GAUSS_JORDAN) {
            result = new GaussJordan("x");
        } else if (matrixOperation == Calculation.INVERSE_MATRIX) {
            result = new InverseMatrix();
        } else {
            throw new InvalidParameterException("Should not happen");
        }
        pbLoadingResult.setVisibility(View.GONE);
        return getService().submit(result).get();
    }
}
