package com.aa.matrix.views;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aa.matrix.R;
import com.aa.matrix.controllers.MainActivityController;
import com.aa.matrix.models.InputMatrixAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;


public class MainActivityView extends BaseActivity {

    private RelativeLayout rlMainActivity;

    private LinearLayout llMatrixLayout;

    private LinearLayout llFillZeroes;

    private RecyclerView rvMatrix;

    private MaterialButton btnSubmitMatrixSize;
    private MaterialButton btnDeterminant;
    private MaterialButton btnGauss;
    private MaterialButton btnReverses;

    private MaterialCheckBox cbFillZeroes;

    private MainActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlMainActivity = findViewById(R.id.rlMainActivity);
        btnSubmitMatrixSize = findViewById(R.id.btnSubmitMatrixSize);
        llMatrixLayout = findViewById(R.id.llMatrixLayout);
        llFillZeroes = findViewById(R.id.llFillZeroes);
        rvMatrix = findViewById(R.id.rvInputMatrix);
        btnDeterminant = findViewById(R.id.btnDeterminant);
        btnGauss = findViewById(R.id.btnGauss);
        btnReverses = findViewById(R.id.btnInverse);
        cbFillZeroes = findViewById(R.id.cbMainActivityFillWithZeroes);

        controller = new MainActivityController(this);

        attachButtonEvents();
        attachHideKeyboardFocusChangeEvents();
        attachCheckBoxEvent();
    }

    private void attachButtonEvents() {
        btnSubmitMatrixSize.setOnClickListener(controller.buildOnSubmitMatrixSizeListener());
        btnDeterminant.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
        btnGauss.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
        btnReverses.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
    }

    private void attachHideKeyboardFocusChangeEvents() {
        btnSubmitMatrixSize.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        cbFillZeroes.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        btnDeterminant.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        btnGauss.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        btnReverses.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
    }

    private void attachCheckBoxEvent() {
        cbFillZeroes.setOnCheckedChangeListener(controller.buildOnCheckedChangeListener());
    }

    public void displayButtons() {
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
        if (rvMatrix.getAdapter() != null) {
            rvMatrix.swapAdapter(adapter, true);
        } else {
            rvMatrix.setAdapter(adapter);
        }
    }
}
