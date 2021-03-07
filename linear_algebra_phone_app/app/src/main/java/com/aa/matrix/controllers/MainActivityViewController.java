package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aa.matrix.R;
import com.aa.matrix.etc.CustomAdapter;
import com.aa.matrix.etc.InvalidParameterException;
import com.aa.matrix.models.MainModel;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.BaseActivity;
import com.aa.matrix.views.ErrorTextView;
import com.aa.matrix.views.MainActivityView;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.aa.matrix.views.BaseActivity.CANNOT_INVERSE_MATRIX;
import static com.aa.matrix.views.BaseActivity.MATRIX_MUST_BE_FULL;
import static com.aa.matrix.views.BaseActivity.SMART_ASS;
import static com.aa.matrix.views.BaseActivity.SOMETHING_WENT_WRONG;

public class MainActivityViewController {

    private final MainModel model = MainModel.getInstance();

    private final MainActivityView view;

    private final Context context;

    private FreeColumnRowController freeColumnController;

    private final RelativeLayout rlMainActivity;
    private final EditText etMatrixRows;
    private final EditText etMatrixColumns;
    private final LinearLayout llMatrixLayout;

    private TextView errorField;

    private CustomAdapter adapter;
    private String[] matrixValues;

    private int rows;
    private int columns;

    private static final String TAG = MainActivityView.class.getName();

    public MainActivityViewController(MainActivityView view) {
        this.view = view;
        this.context = view;
        rlMainActivity = ((Activity) this.context).findViewById(R.id.rlMainActivity);
        etMatrixRows = ((Activity) this.context).findViewById(R.id.etMatrixRows);
        etMatrixColumns = ((Activity) this.context).findViewById(R.id.etMatrixColumns);
        llMatrixLayout = ((Activity) this.context).findViewById(R.id.llMatrixLayout);
    }

    public TextView.OnEditorActionListener buildMatrixActionOnDonePress() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        hideError(errorField);
                        if (checkIfMatrixCanBeBuilt()) {
                            buildMatrixView();
                            showButtons();
                        }
                    } catch (final NumberFormatException e) {
                        Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
                    }
                }
                return false;
            }
        };
    }

    public View.OnFocusChangeListener buildMatrixActionOnFocusChange() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideError(errorField);
                try {
                    if (!hasFocus && checkIfMatrixCanBeBuilt()) {
                        buildMatrixView();
                        showButtons();
                    }
                } catch (final NumberFormatException e) {
                    Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
                }
            }
        };
    }

    public View.OnFocusChangeListener buildHideKeyboardListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    BaseActivity.hideKeyboard(rlMainActivity, view);
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
                }
            }
        };
    }

    public View.OnClickListener buildOnChosenMatrixOperationAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                BaseActivity.getService().execute(new Runnable() {
                    @Override
                    public void run() {
                        hideError(errorField);
                        view.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideResults();
                            }
                        });
                        try {
                            if (checkIfMatrixIsFull()) {
                                int btnId = v.getId();
                                if (btnId == R.id.btnDeterminant) {
                                    String determinant = model.calculateDeterminant(matrixValues, rows, columns);
                                    // need to create new activity to display matrix snapshots + result
                                } else if (btnId == R.id.btnGauss) {
                                    buildGaussJordenFreeColumnView(matrixValues, rows, columns);
                                } else if (btnId == R.id.btnReverse) {
                                    Matrix InverseMatrix = model.calculateInverseMatrix(matrixValues, rows, columns);
                                    // need to create new activity to display matrix snapshots + result
                                }
                            } else {
                                errorField = ErrorTextView.displayErrorTextView(view, MATRIX_MUST_BE_FULL,
                                        rlMainActivity, llMatrixLayout.getId());
                            }
                        } catch (final InvalidParameterException | ExecutionException | InterruptedException | NumberFormatException e) {
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

    private boolean checkIfMatrixCanBeBuilt() {
        return etMatrixRows.getText().toString().length() > 0
                && etMatrixColumns.getText().toString().length() > 0;
    }

    private void getRowAndColumnsValues() throws NumberFormatException {
        rows = Integer.parseInt(etMatrixRows.getText().toString());
        columns = Integer.parseInt(etMatrixColumns.getText().toString());
        matrixValues = new String[rows * columns];
        Arrays.fill(matrixValues, "");
    }

    private CustomAdapter createCustomAdapter() {
        adapter = new CustomAdapter(matrixValues);
        return adapter;
    }

    private GridLayoutManager createCustomAdapterLayoutManager() {
        return new GridLayoutManager(context, columns, LinearLayoutManager.VERTICAL, false);
    }

    private void buildMatrixView() {
        getRowAndColumnsValues();
        view.buildMatrixView(createCustomAdapter(), createCustomAdapterLayoutManager());
    }

    private void showButtons() {
        view.showButtons();
    }

    private void hideError(TextView errorField) {
        view.hideError(errorField);
    }

    private void hideResults() {
        view.hideResults(freeColumnController);
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

    private void buildGaussJordenFreeColumnView(String[] matrixValues, int rows, int columns) {
        LinearLayout llFreeColumnRow = ((Activity) context).findViewById(R.id.llFreeColumnRow);
        freeColumnController = new FreeColumnRowController(llFreeColumnRow,
                view, matrixValues, rows, columns);
        view.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                freeColumnController.build();
            }
        });
    }

}
