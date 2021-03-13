package com.aa.matrix.views;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aa.matrix.R;
import com.aa.matrix.controllers.DisplayResultActivityController;
import com.aa.matrix.models.CalculationResult;

import static com.aa.matrix.controllers.BaseController.OPERATION;

public class DisplayResultActivityView extends BaseActivity {

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

        pbLoadingResult = findViewById(R.id.pbLoadingResult);
        tvDisplayResult = findViewById(R.id.tvResult);

        new DisplayResultActivityController(this, opn);
    }

    public void hideProgressBar() {
        if (pbLoadingResult.getVisibility() == View.VISIBLE) {
            pbLoadingResult.setVisibility(View.GONE);
        }
    }

    public void displayDeterminantResult(CalculationResult result) {
        String textToDisplay = result.toString();
        tvDisplayResult.setText(textToDisplay);
    }

    public void displayGaussJordanResult(CalculationResult result) {
        String textToDisplay = result.toString();
        tvDisplayResult.setText(textToDisplay);
    }

    public void displayInverseMatrixResult(CalculationResult result) {
//        String textToDisplay = result.toString();
//        tvDisplayResult.setText(textToDisplay);
    }

    @Override
    public ViewGroup getRootView() {
        return (ViewGroup) findViewById(R.id.llDisplayResults);
    }
}
