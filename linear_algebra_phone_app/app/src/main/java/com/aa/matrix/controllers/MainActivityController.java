package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aa.matrix.R;
import com.aa.matrix.models.InputMatrixAdapter;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.views.DisplayResultActivityView;
import com.aa.matrix.views.MainActivityView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

import static com.aa.matrix.views.BaseActivity.COLS_INVALID;
import static com.aa.matrix.views.BaseActivity.DETERMINANT;
import static com.aa.matrix.views.BaseActivity.INPUT_VALUES;
import static com.aa.matrix.views.BaseActivity.INVERSE_MATRIX;
import static com.aa.matrix.views.BaseActivity.MATRIX_MUST_BE_FULL;
import static com.aa.matrix.views.BaseActivity.ROWS_INVALID;
import static com.aa.matrix.views.BaseActivity.SMART_ASS;

public class MainActivityController extends BaseController {

    private final BaseModel model = BaseModel.getInstance();

    private final MainActivityView view;

    private final Context context;

    private final RelativeLayout rlMainActivity;
    private final TextInputEditText etMatrixRows;
    private final TextInputEditText etMatrixColumns;
    private final TextInputLayout tilMatrixRows;
    private final TextInputLayout tilMatrixColumns;
    private final LinearLayout llMatrixLayout;

    private InputMatrixAdapter adapter;
    private String[] matrixValues;

    private int rows;
    private int columns;

    private static final String TAG = MainActivityView.class.getName();

    public MainActivityController(MainActivityView view) {
        this.view = view;
        this.context = view;
        rlMainActivity = ((Activity) this.context).findViewById(R.id.rlMainActivity);
        tilMatrixRows = ((Activity) this.context).findViewById(R.id.tilMatrixRows);
        etMatrixRows = ((Activity) this.context).findViewById(R.id.etMatrixRows);
        tilMatrixColumns = ((Activity) this.context).findViewById(R.id.tilMatrixColumns);
        etMatrixColumns = ((Activity) this.context).findViewById(R.id.etMatrixColumns);
        llMatrixLayout = ((Activity) this.context).findViewById(R.id.llMatrixLayout);
    }

    public View.OnClickListener buildOnSubmitMatrixSizeListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkIfMatrixCanBeBuilt()) {
                        buildMatrixView();
                        displayButtons();
                    }
                } catch (NumberFormatException ignored) {
                    Snackbar.make(rlMainActivity, INPUT_VALUES, Snackbar.LENGTH_LONG).show();
                }
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
                                Snackbar.make(rlMainActivity, MATRIX_MUST_BE_FULL, Snackbar.LENGTH_LONG).show();
                            }
                        } catch (final NumberFormatException e) {
                            Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
                            Snackbar.make(rlMainActivity, SMART_ASS, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
    }

    private boolean checkIfMatrixCanBeBuilt() throws NumberFormatException {
        int rows = Integer.parseInt(etMatrixRows.getText().toString());
        int cols = Integer.parseInt(etMatrixColumns.getText().toString());
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
            tilMatrixColumns.setError(COLS_INVALID);
        }
        return validRows && validCols;
    }

    private void getRowAndColumnsValues() throws NumberFormatException {
        rows = Integer.parseInt(etMatrixRows.getText().toString());
        columns = Integer.parseInt(etMatrixColumns.getText().toString());
        matrixValues = new String[rows * columns];
        Arrays.fill(matrixValues, "");
    }

    private InputMatrixAdapter createCustomAdapter() {
        adapter = new InputMatrixAdapter(matrixValues, columns);
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
