package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aa.matrix.R;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Calculation;
import com.aa.matrix.models.InputMatrixAdapter;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.views.MainActivity;
import com.aa.matrix.views.ResultActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

/**
 * @author Adam Akiva
 * Class used to manage the MainActivity view
 */
public class MainActivityController extends BaseController {

    private final BaseModel model = BaseModel.getInstance();

    private final MainActivity view;
    private final Context context;
    private final RelativeLayout rlMainActivity;
    private final TextInputLayout tilMatrixRows;
    private final TextInputLayout tilMatrixCols;
    private InputMatrixAdapter adapter;
    private String[] matrixValues;
    private int rows;
    private int cols;

    private boolean matrixViewVisible = false;

    /**
     * @param view MainActivity object
     */
    public MainActivityController(MainActivity view) {
        this.view = view;
        this.context = view;
        rlMainActivity = ((Activity) this.context).findViewById(R.id.rlMainActivity);
        tilMatrixRows = ((Activity) this.context).findViewById(R.id.tilMatrixRows);
        tilMatrixCols = ((Activity) this.context).findViewById(R.id.tilMatrixCols);

        tilMatrixRows.getEditText().addTextChangedListener(buildOnChangeMatrixSizeListener());
        tilMatrixCols.getEditText().addTextChangedListener(buildOnChangeMatrixSizeListener());
    }

    /**
     * @return TextWatcher used to check if the matrix size was changed and therefore hide
     * the matrix view until the matrix layout is built
     */
    public TextWatcher buildOnChangeMatrixSizeListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (matrixViewVisible) {
                    view.hideMatrixView();
                    view.hideCheckBoxRow();
                    hideButtons();
                    matrixViewVisible = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    /**
     * @return View.onClickListener to display the matrix layout if all the values are valid
     */
    public View.OnClickListener buildOnSubmitMatrixSizeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (tilMatrixRows.getEditText().getText().toString().equals("1") ||
                            tilMatrixCols.getEditText().getText().toString().equals("1")) {
                        tilMatrixRows.setError(NOT_A_MATRIX);
                        tilMatrixCols.setError(NOT_A_MATRIX);
                        return;
                    }
                    if (checkIfMatrixCanBeBuilt()) {
                        matrixViewVisible = true;
                        tilMatrixRows.setError("");
                        tilMatrixCols.setError("");
                        buildMatrixView();
                        view.displayCheckBoxRow();
                        displayButtons(rows == cols);
                    }
                } catch (NumberFormatException ignored) {
                    Snackbar.make(rlMainActivity, INPUT_VALUES, Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    /**
     * @return Compound.onCheckedChangeListener which fill empty matrix cells with zeroes if
     * checked or remove zeroes if unchecked
     */
    public CompoundButton.OnCheckedChangeListener buildOnCheckedChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter.fillEmptyCellsWithZeroes();
                } else {
                    adapter.emptyCellsWithZeroes();
                }
            }
        };
    }

    /**
     * @return View.onClickListener which starts a thread or open a dialog based on which button
     * was pressed
     */
    public View.OnClickListener buildOnChosenMatrixOperationAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getService().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (checkIfMatrixIsFull()) {
                                int btnId = v.getId();
                                if (btnId == R.id.btnDeterminant) {
                                    model.setMatrixObject(Calculation.DETERMINANT,
                                            Matrix.convertStringArrayTo2DArray(matrixValues, rows, cols),
                                            rows, cols);
                                    goToResultActivity(view, ResultActivity.class);
                                } else if (btnId == R.id.btnGauss) {
                                    buildGaussJordenFreeColumnView();
                                } else if (btnId == R.id.btnInverse) {
                                    model.setMatrixObject(Calculation.INVERSE_MATRIX,
                                            Matrix.convertStringArrayTo2DArray(matrixValues, rows, cols),
                                            rows, cols);
                                    goToResultActivity(view, ResultActivity.class);
                                }
                            } else {
                                Snackbar.make(rlMainActivity, MATRIX_MUST_BE_FULL, Snackbar.LENGTH_LONG).show();
                            }
                        } catch (final NumberFormatException e) {
                            Snackbar.make(rlMainActivity, SMART_ASS, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
    }

    /**
     * @return True if the row and col values are valid, false otherwise
     * @throws NumberFormatException Should never happen since the keyboard option
     * state only number are displayed so it's only if the user does some weird injection stuff
     */
    private boolean checkIfMatrixCanBeBuilt() throws NumberFormatException {
        int rows = Integer.parseInt(tilMatrixRows.getEditText().getText().toString());
        int cols = Integer.parseInt(tilMatrixCols.getEditText().getText().toString());
        boolean validRows = false;
        boolean validCols = false;
        if (rows <= 5 && rows > 0) {
            validRows = true;
        } else {
            tilMatrixRows.setError(ROWS_INVALID);
        }
        if (cols <= 5 && cols > 0) {
            validCols = true;
        } else {
            tilMatrixCols.setError(COLS_INVALID);
        }
        return validRows && validCols;
    }

    /**
     * Method which initialize the matrix parameters if all the values are valid
     * @throws NumberFormatException Should never happen since the keyboard option
     * state only number are displayed so it's only if the user does some weird injection stuff
     */
    private void getRowAndColsValues() throws NumberFormatException {
        rows = Integer.parseInt(tilMatrixRows.getEditText().getText().toString());
        cols = Integer.parseInt(tilMatrixCols.getEditText().getText().toString());
        matrixValues = new String[rows * cols];
        Arrays.fill(matrixValues, "");
    }

    /**
     * Method to build a new InputMatrixAdapter
     * @return InputMatrixAdapter to attach to the recyclerView
     */
    private InputMatrixAdapter createCustomAdapter() {
        adapter = new InputMatrixAdapter(view, matrixValues, cols);
        return adapter;
    }

    /**
     * Method to create a new GridLayoutManager for the recyclerView
     * @return GridLayoutManager for the recyclerView
     */
    private GridLayoutManager createCustomAdapterLayoutManager() {
        return new GridLayoutManager(context, cols, LinearLayoutManager.VERTICAL, false);
    }

    /**
     * Method to build the matrix view if the row and col values are valid
     */
    private void buildMatrixView() {
        getRowAndColsValues();
        view.buildMatrixView(createCustomAdapter(), createCustomAdapterLayoutManager());
    }

    /**
     * Method which decide which buttons to show based on the matrix parameters
     * @param isSquareMatrix true if the matrix is a square matrix, false otherwise
     */
    private void displayButtons(boolean isSquareMatrix) {
        if (isSquareMatrix) {
            view.displayDeterminantButton();
            view.displayGaussJordanButton();
            view.displayInverseMatrixButton();
        } else {
            view.hideDeterminantButton();
            view.displayGaussJordanButton();
            view.hideInverseMatrixButton();
        }
    }

    /**
     * Method to hide the buttons in case the matrix size is changed
     */
    private void hideButtons() {
        view.hideDeterminantButton();
        view.hideGaussJordanButton();
        view.hideInverseMatrixButton();
    }

    /**
     * @return True if the matrix is filled with values, false otherwise
     * @throws NumberFormatException Should never happen since the keyboard option
     * state only number are displayed so it's only if the user does some weird injection stuff
     */
    private boolean checkIfMatrixIsFull() throws NumberFormatException {
        for (final String value : matrixValues) {
            if (value.length() == 0 || !checkIfStringIsANumber(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param value String for the matrix input fields
     * @return True if the text is a number, false + user message if not
     */
    private boolean checkIfStringIsANumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            Snackbar.make(rlMainActivity, SMART_ASS, Snackbar.LENGTH_LONG);
            return false;
        }
    }

    /**
     * Method to build the dialog if the button pressed was Gauss Jordan, to let the user
     * enter values for the freeColumn vector
     */
    private void buildGaussJordenFreeColumnView() {
        this.view.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final FreeColumnDialogController controller = new FreeColumnDialogController(view,
                        matrixValues, rows, cols);
                controller.build();
            }
        });
    }
}
