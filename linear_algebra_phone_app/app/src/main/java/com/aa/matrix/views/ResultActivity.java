package com.aa.matrix.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aa.matrix.R;
import com.aa.matrix.controllers.ResultActivityController;
import com.aa.matrix.models.Calculation;

/**
 * @author Adam Akiva
 */
public class ResultActivity extends AppCompatActivity {

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

    /**
     * Method to hide the progress bar from the user (when the calculation is done)
     */
    public void hideProgressBar() {
        if (pbLoadingResult.getVisibility() == View.VISIBLE) {
            pbLoadingResult.setVisibility(View.GONE);
        }
    }

    /**
     * Method to display the result of the calculation to the user
     * @param result Calculation Object with the steps of the calculation and the result
     */
    public void displayResult(Calculation result) {
        tvDisplayResult.setText(result.toString());
    }
}
