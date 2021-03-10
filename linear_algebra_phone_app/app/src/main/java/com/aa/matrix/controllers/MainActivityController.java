package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aa.matrix.R;
import com.aa.matrix.models.InputMatrixAdapter;
import com.aa.matrix.etc.InvalidParameterException;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.views.DisplayResultActivityView;
import com.aa.matrix.views.ErrorTextView;
import com.aa.matrix.views.MainActivityView;

import java.util.Arrays;

import static com.aa.matrix.views.BaseActivity.CANNOT_INVERSE_MATRIX;
import static com.aa.matrix.views.BaseActivity.DETERMINANT;
import static com.aa.matrix.views.BaseActivity.INVERSE_MATRIX;
import static com.aa.matrix.views.BaseActivity.MATRIX_MUST_BE_FULL;
import static com.aa.matrix.views.BaseActivity.SMART_ASS;
import static com.aa.matrix.views.BaseActivity.SOMETHING_WENT_WRONG;

public class MainActivityController extends BaseController {

    private final BaseModel model = BaseModel.getInstance();

    private final MainActivityView view;

    private final Context context;

    private final RelativeLayout rlMainActivity;
    private final EditText etMatrixRows;
    private final EditText etMatrixColumns;
    private final LinearLayout llMatrixLayout;

    private TextView errorField;

    private InputMatrixAdapter adapter;
    private String[] matrixValues;

    private int rows;
    private int columns;

    private static final String TAG = MainActivityView.class.getName();

    public MainActivityController(MainActivityView view) {
        this.view = view;
        this.context = view;
        rlMainActivity = ((Activity) this.context).findViewById(R.id.rlMainActivity);
        etMatrixRows = ((Activity) this.context).findViewById(R.id.etMatrixRows);
        etMatrixColumns = ((Activity) this.context).findViewById(R.id.etMatrixColumns);
        llMatrixLayout = ((Activity) this.context).findViewById(R.id.llMatrixLayout);
    }

    public View.OnClickListener buildOnSubmitMatrixSizeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkIfMatrixCanBeBuilt()) {
                        hideError(errorField);
                        buildMatrixView();
                        displayButtons();
                    }
                } catch (InvalidParameterException ignored) {}
            }
        };
    }

    public CompoundButton.OnCheckedChangeListener buildOnCheckedChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (adapter == null) {
                        createCustomAdapter();
                    }
                    adapter.fillEmptyCellsWithZeroes();
                } else {
                    adapter.emptyCellsWithZeroes();
                }
            }
        };
    }

    public View.OnClickListener buildOnChosenMatrixOperationAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getService().execute(new Runnable() {
                    @Override
                    public void run() {
                        hideError(errorField);
                        try {
                            if (checkIfMatrixIsFull()) {
                                model.setMatrix(Matrix.convertStringArrayToMatrix(matrixValues, rows, columns));
                                int btnId = v.getId();
                                if (btnId == R.id.btnDeterminant) {
                                    goToResultActivity(view, DisplayResultActivityView.class, DETERMINANT);
                                } else if (btnId == R.id.btnGauss) {
                                    buildGaussJordenFreeColumnView();
                                } else if (btnId == R.id.btnInverse) {
                                    goToResultActivity(view, DisplayResultActivityView.class, INVERSE_MATRIX);
                                }
                            } else {
                                errorField = ErrorTextView.displayErrorTextView(view, MATRIX_MUST_BE_FULL,
                                        rlMainActivity, llMatrixLayout.getId());
                            }
                        } catch (final InvalidParameterException | NumberFormatException e) {
                            Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
                            try {
                                if (e instanceof InvalidParameterException) {
                                    try {
                                        errorField = ErrorTextView.displayErrorTextView(view, CANNOT_INVERSE_MATRIX,
                                                rlMainActivity, llMatrixLayout.getId());
                                    } catch (final InvalidParameterException ignored) {}
                                } else if (e instanceof NumberFormatException) {
                                    errorField = ErrorTextView.displayErrorTextView(view, SMART_ASS, rlMainActivity,
                                            llMatrixLayout.getId());
                                } else {
                                    try {
                                        errorField = ErrorTextView.displayErrorTextView(view, SOMETHING_WENT_WRONG,
                                                rlMainActivity, llMatrixLayout.getId());
                                    } catch (final InvalidParameterException ignored) {}
                                }
                            } catch (final InvalidParameterException ignored) {}
                        }
                    }
                });
            }
        };
    }

    private boolean checkIfMatrixCanBeBuilt() throws InvalidParameterException {
        int rows = Integer.parseInt(etMatrixRows.getText().toString());
        int cols = Integer.parseInt(etMatrixColumns.getText().toString());
        String errorMessage = "";
        if (rows <= 5 && rows > 0) {
            if (cols <= 5 && cols > 0) {
                return true;
            } else {
                errorMessage += "Rows must be between 1 to 5";
            }
        } else {
            errorMessage += "Columns must be between 1 to 5";
        }
        errorField = ErrorTextView.displayErrorTextView(view, errorMessage, rlMainActivity,
                llMatrixLayout.getId());
        return false;
    }

    private void getRowAndColumnsValues() throws NumberFormatException {
        rows = Integer.parseInt(etMatrixRows.getText().toString());
        columns = Integer.parseInt(etMatrixColumns.getText().toString());
        matrixValues = new String[rows * columns];
        Arrays.fill(matrixValues, "");
    }

    private InputMatrixAdapter createCustomAdapter() {
        adapter = new InputMatrixAdapter(matrixValues);
        return adapter;
    }

    private GridLayoutManager createCustomAdapterLayoutManager() {
        return new GridLayoutManager(context, columns, LinearLayoutManager.VERTICAL, false);
    }

    private void buildMatrixView() {
        getRowAndColumnsValues();
        view.buildMatrixView(createCustomAdapter(), createCustomAdapterLayoutManager());
    }

    private void displayButtons() {
        view.displayButtons();
    }

    private void hideError(TextView errorField) {
        view.hideError(errorField);
    }

    private boolean checkIfMatrixIsFull() throws NumberFormatException {
        for (final String value : matrixValues) {
            if (value.length() == 0 || !checkIfStringIsANumber(value)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfStringIsANumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
            return false;
        }
    }

    private void buildGaussJordenFreeColumnView() {
        this.view.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final FreeColumnDialogController controller = new FreeColumnDialogController(view, rows);
                controller.build();
            }
        });
    }
}
