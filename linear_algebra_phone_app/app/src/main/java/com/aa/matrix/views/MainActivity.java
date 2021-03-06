package com.aa.matrix.views;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aa.matrix.R;
import com.aa.matrix.controllers.MainActivityController;
import com.aa.matrix.models.InputMatrixAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;


/**
 * @author Adam Akiva
 * Class started when the application starts
 */
public class MainActivity extends AppCompatActivity {

    private RelativeLayout rlMainActivity;

    private LinearLayout llMatrixLayout;

    private LinearLayout llFillZeroes;

    private RecyclerView rvMatrix;

    private MaterialButton btnDeterminant;
    private MaterialButton btnGauss;
    private MaterialButton btnInverse;

    private MaterialCheckBox cbFillZeroes;

    private MainActivityController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlMainActivity = findViewById(R.id.rlMainActivity);
        MaterialButton btnSubmitMatrixSize = findViewById(R.id.btnSubmitMatrixSize);
        llMatrixLayout = findViewById(R.id.llMatrixLayout);
        llFillZeroes = findViewById(R.id.llFillZeroes);
        rvMatrix = findViewById(R.id.rvInputMatrix);
        btnDeterminant = findViewById(R.id.btnDeterminant);
        btnGauss = findViewById(R.id.btnGauss);
        btnInverse = findViewById(R.id.btnInverse);
        cbFillZeroes = findViewById(R.id.cbMainActivityFillWithZeroes);

        // Needs to be initialized before using if (obliviously)
        controller = new MainActivityController(this);

        btnSubmitMatrixSize.setOnClickListener(controller.buildOnSubmitMatrixSizeListener());
        btnSubmitMatrixSize.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
    }

    /**
     * Method used to display the matrix view to the user after creating the InputMatrixAdapter
     * and the GridLayoutManager
     * @param adapter MatrixInputAdapter to attach to the recyclerView
     * @param layoutManager GridLayoutManager to attach ot the recyclerView
     */
    public void buildMatrixView(InputMatrixAdapter adapter, GridLayoutManager layoutManager) {
        if (llMatrixLayout.getVisibility() == View.INVISIBLE || llMatrixLayout.getVisibility() == View.GONE) {
            llMatrixLayout.setVisibility(View.VISIBLE);
            rvMatrix.setLayoutManager(layoutManager);
            if (rvMatrix.getAdapter() != null) {
                rvMatrix.swapAdapter(adapter, true);
            } else {
                rvMatrix.setAdapter(adapter);
            }
        }
    }

    /**
     * Method to hide the matrix view in case the row or col value was changed
     */
    public void hideMatrixView() {
        if (llMatrixLayout.getVisibility() == View.VISIBLE) {
            llMatrixLayout.setVisibility(View.GONE);
            rvMatrix.setLayoutManager(null);
            rvMatrix.setAdapter(null);
        }
    }

    /**
     * Method to display the checkBox row to the user
     */
    public void displayCheckBoxRow() {
        if (llFillZeroes.getVisibility() == View.INVISIBLE || llFillZeroes.getVisibility() == View.GONE) {
            llFillZeroes.setVisibility(View.VISIBLE);
            cbFillZeroes.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
            cbFillZeroes.setOnCheckedChangeListener(controller.buildOnCheckedChangeListener());
        }
    }

    /**
     * Method to hide the checkBox row from the user
     */
    public void hideCheckBoxRow() {
        if (llFillZeroes.getVisibility() == View.VISIBLE) {
            llFillZeroes.setVisibility(View.GONE);
            cbFillZeroes.setOnFocusChangeListener(null);
            cbFillZeroes.setOnCheckedChangeListener(null);
        }
    }

    /**
     * Method to show the Determinant button to the user, attach an event listener as well as
     * a click event to the button
     */
    public void displayDeterminantButton() {
        if (btnDeterminant.getVisibility() == View.INVISIBLE || btnDeterminant.getVisibility() == View.GONE) {
            btnDeterminant.setVisibility(View.VISIBLE);
            btnDeterminant.setClickable(true);
            btnDeterminant.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
            btnDeterminant.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        }
    }

    /**
     * Method to show the Gauss Jordan button to the user, attach an event listener as well as
     * a click event to the button
     */
    public void displayGaussJordanButton() {
        if (btnGauss.getVisibility() == View.INVISIBLE || btnGauss.getVisibility() == View.GONE) {
            btnGauss.setVisibility(View.VISIBLE);
            btnGauss.setClickable(true);
            btnGauss.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
            btnGauss.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        }
    }

    /**
     * Method to show the Inverse Matrix button to the user, attach an event listener as well as
     * a click event to the button
     */
    public void displayInverseMatrixButton() {
        if (btnInverse.getVisibility() == View.INVISIBLE || btnInverse.getVisibility() == View.GONE) {
            btnInverse.setVisibility(View.VISIBLE);
            btnInverse.setClickable(true);
            btnInverse.setOnClickListener(controller.buildOnChosenMatrixOperationAction());
            btnInverse.setOnFocusChangeListener(controller.buildHideKeyboardListener(rlMainActivity, this));
        }
    }

    /**
     * Method to hide the Determinant button from the user, as well as detaching all events related
     * to said button
     */
    public void hideDeterminantButton() {
        if (btnDeterminant.getVisibility() == View.VISIBLE) {
            btnDeterminant.setVisibility(View.INVISIBLE);
            btnDeterminant.setClickable(false);
            btnDeterminant.setOnClickListener(null);
            btnDeterminant.setOnFocusChangeListener(null);
        }
    }

    /**
     * Method to hide the Gauss Jordan button from the user, as well as detaching all events related
     * to said button
     */
    public void hideGaussJordanButton() {
        if (btnGauss.getVisibility() == View.VISIBLE) {
            btnGauss.setVisibility(View.INVISIBLE);
            btnGauss.setClickable(false);
            btnGauss.setOnClickListener(null);
            btnGauss.setOnFocusChangeListener(null);
        }
    }

    /**
     * Method to hide the Inverse Matrix button from the user, as well as detaching all events related
     * to said button
     */
    public void hideInverseMatrixButton() {
        if (btnInverse.getVisibility() == View.VISIBLE) {
            btnInverse.setVisibility(View.INVISIBLE);
            btnInverse.setClickable(false);
            btnInverse.setOnClickListener(null);
            btnInverse.setOnFocusChangeListener(null);
        }
    }
}
