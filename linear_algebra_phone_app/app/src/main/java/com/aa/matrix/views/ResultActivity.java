package com.aa.matrix.views;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aa.matrix.R;
import com.aa.matrix.controllers.ResultActivityController;
import com.aa.matrix.models.Calculation;

public class ResultActivity extends BaseActivity {

    private ProgressBar pbLoadingResult;
    private TextView tvDisplayResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_result);

        pbLoadingResult = findViewById(R.id.pbLoadingResult);
        tvDisplayResult = findViewById(R.id.tvResult);

        new ResultActivityController(this);
    }

    public void hideProgressBar() {
        if (pbLoadingResult.getVisibility() == View.VISIBLE) {
            pbLoadingResult.setVisibility(View.GONE);
        }
    }

    public void displayDeterminantResult(Calculation result) {
        String textToDisplay = result.toString();
        tvDisplayResult.setText(textToDisplay);
    }

    public void displayGaussJordanResult(Calculation result) {
        String textToDisplay = result.toString();
        tvDisplayResult.setText(textToDisplay);
    }

    public void displayInverseMatrixResult(Calculation result) {
        String textToDisplay = result.toString();
        tvDisplayResult.setText(textToDisplay);
    }

    @Override
    public ViewGroup getRootView() {
        return (ViewGroup) findViewById(R.id.llDisplayResults);
    }
}
