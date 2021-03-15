package com.aa.matrix.controllers;

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

/**
 * @author Adam Akiva
 * Class used to manage the ResultActivity view
 */
public class ResultActivityController extends BaseController {

    private final ResultActivity view;

    // Shows a loading circle for the calculation time
    private final ProgressBar pbLoadingResult;

    // See Calculation object for options
    private final int matrixOperation;

    /**
     * @param view ResultActivity object
     */
    public ResultActivityController(ResultActivity view) {
        this.view = view;
        pbLoadingResult = view.findViewById(R.id.pbLoadingResult);
        this.matrixOperation = BaseModel.getInstance().getMatrixObject().getCalculationType();
        try {
            displayResult();
        } catch (InvalidParameterException | ExecutionException | InterruptedException ignored) {}
    }

    /**
     * Method to display the result of the calculation to the user
     * @throws ExecutionException If the thread threw an exception (besides interrupted)
     * @throws InterruptedException If the thread was interrupted mid-run
     * @throws InvalidParameterException If the user injected stuff that should not be inputted
     */
    public void displayResult() throws ExecutionException, InterruptedException,
            InvalidParameterException {
        Calculation result = startCalculation();
        view.hideProgressBar();
        if (matrixOperation == Calculation.DETERMINANT ||
                matrixOperation == Calculation.GAUSS_JORDAN ||
                matrixOperation == Calculation.INVERSE_MATRIX) {
            view.displayResult(result);
        } else {
            throw new InvalidParameterException("Should not happen");
        }
    }

    /**
     * Method to start the correct thread for the calculation depending on button pressed
     * @throws ExecutionException If the thread threw an exception (besides interrupted)
     * @throws InterruptedException If the thread was interrupted mid-run
     * @throws InvalidParameterException If the user injected stuff that should not be inputted
     */
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
