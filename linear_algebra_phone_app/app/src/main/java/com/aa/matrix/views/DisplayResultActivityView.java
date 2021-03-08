package com.aa.matrix.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aa.matrix.R;
import com.aa.matrix.controllers.DisplayResultActivityController;
import com.aa.matrix.models.MatrixSnapShots;

import java.util.Locale;
import java.util.Map;


public class DisplayResultActivityView extends BaseActivity {

    private DisplayResultActivityController controller;

    private ProgressBar pbLoadingResult;
    private TextView tvDisplayResult;

    private int opn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_result);

        if (getIntent().getExtras() != null) {
            opn = getIntent().getExtras().getInt(OPERATION);
        }

        pbLoadingResult = (ProgressBar) findViewById(R.id.pbLoadingResult);
        tvDisplayResult = (TextView) findViewById(R.id.tvDisplayResult);

        controller = new DisplayResultActivityController(this, opn);
    }

    public void hideProgressBar() {
        if (pbLoadingResult.getVisibility() == View.VISIBLE) {
            pbLoadingResult.setVisibility(View.GONE);
        }
    }

    public void displayDeterminantResult(String[] result, MatrixSnapShots snapShots) {
        displayMatrixSnapShots(snapShots);
    }

    public void displayGaussJordenResult(String[] result, MatrixSnapShots snapShots) {
        displayMatrixSnapShots(snapShots);
    }

    public void displayInverseMatrixResult(String[] result, MatrixSnapShots snapShots) {
        displayMatrixSnapShots(snapShots);
    }

    private void displayMatrixSnapShots(MatrixSnapShots snapShots) {
        tvDisplayResult.setText(snapShots.toString());
    }
}
