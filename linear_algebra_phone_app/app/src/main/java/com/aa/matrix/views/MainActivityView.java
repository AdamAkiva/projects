package com.aa.matrix.views;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aa.matrix.R;
import com.aa.matrix.controllers.MainActivityController;
import com.aa.matrix.etc.InputMatrixAdapter;


public class MainActivityView extends BaseActivity {

    private RelativeLayout rlMainActivity;

    private LinearLayout llMatrixLayout;
    private LinearLayout llFillZeroes;

    private EditText etMatrixColumns;
    private EditText etMatrixRows;

    private RecyclerView rvMatrix;

    private Button btnDeterminant;
    private Button btnGauss;
    private Button btnReverses;

    private CheckBox cbFillZeroes;

    private static final String TAG = MainActivityView.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlMainActivity = (RelativeLayout) findViewById(R.id.rlMainActivity);
        etMatrixColumns = (EditText) findViewById(R.id.etMatrixColumns);
        etMatrixRows = (EditText) findViewById(R.id.etMatrixRows);
        llMatrixLayout = (LinearLayout) findViewById(R.id.llMatrixLayout);
        llFillZeroes = (LinearLayout) findViewById(R.id.llFillZeroes);
        rvMatrix = (RecyclerView) findViewById(R.id.rvInputMatrix);
        btnDeterminant = (Button) findViewById(R.id.btnDeterminant);
        btnGauss = (Button) findViewById(R.id.btnGauss);
        btnReverses = (Button) findViewById(R.id.btnInverse);
        cbFillZeroes = (CheckBox) findViewById(R.id.cbFillZeroes);

        attachEvents();
    }

    public void attachEvents() {
        MainActivityController controller = new MainActivityController(this);
        rlMainActivity.setOnFocusChangeListener(controller.buildHideKeyboardListener());
        etMatrixColumns.setOnEditorActionListener(controller.buildMatrixActionOnDonePress());
        etMatrixRows.setOnFocusChangeListener(controller.buildMatrixActionOnFocusChange());
        etMatrixColumns.setOnFocusChangeListener(controller.buildMatrixActionOnFocusChange());
        cbFillZeroes.setOnCheckedChangeListener(controller.buildOnCheckedChangeListener());
        btnDeterminant.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
        btnGauss.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
        btnReverses.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
    }

    public void showButtons() {
        if (llFillZeroes.getVisibility() == View.INVISIBLE || llFillZeroes.getVisibility() == View.GONE) {
            llFillZeroes.setVisibility(View.VISIBLE);
        }
        if (btnDeterminant.getVisibility() == View.INVISIBLE || btnDeterminant.getVisibility() == View.GONE) {
            btnDeterminant.setVisibility(View.VISIBLE);
            btnDeterminant.setClickable(true);
        }
        if (btnGauss.getVisibility() == View.INVISIBLE || btnGauss.getVisibility() == View.GONE) {
            btnGauss.setVisibility(View.VISIBLE);
            btnGauss.setClickable(true);
        }
        if (btnReverses.getVisibility() == View.INVISIBLE || btnReverses.getVisibility() == View.GONE) {
            btnReverses.setVisibility(View.VISIBLE);
            btnReverses.setClickable(true);
        }
    }

    public void buildMatrixView(InputMatrixAdapter adapter, GridLayoutManager layoutManager) {
        if (llMatrixLayout.getVisibility() == View.INVISIBLE || llMatrixLayout.getVisibility() == View.GONE) {
            llMatrixLayout.setVisibility(View.VISIBLE);
        }
        rvMatrix.setLayoutManager(layoutManager);
        rvMatrix.setAdapter(adapter);
    }

    public void hideError(TextView errorField) {
        if (errorField != null) {
            if (errorField.getId() != View.NO_ID) {
                ErrorTextView.removeErrorTextView(MainActivityView.this, rlMainActivity, errorField);
            }
        }
    }

    public void goToResultActivity(int opn) {
        Intent intent = new Intent(this, DisplayResultActivityView.class);
        intent.putExtra(OPERATION, opn);
        startActivity(intent);
    }
}
