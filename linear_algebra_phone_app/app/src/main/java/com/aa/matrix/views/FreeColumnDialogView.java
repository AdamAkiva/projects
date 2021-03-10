package com.aa.matrix.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.aa.matrix.R;

import java.util.Locale;

public class FreeColumnDialogView extends Dialog {

    private LinearLayout llDialogView;

    private final LayoutInflater inflater;

    private static final String TEXT_VIEW_KEY = "X";

    public FreeColumnDialogView(Activity parent) {
        super(parent);
        inflater = LayoutInflater.from(parent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.free_column_dialog);

        llDialogView = findViewById(R.id.llDialogView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < llDialogView.getChildCount(); i++) {
            View v = llDialogView.getChildAt(i);
            if (!(v instanceof ViewGroup)) {
                llDialogView.removeView(v);
            }
        }
    }

    public ViewGroup addButtonsLayout() {
        return (ViewGroup) inflater.inflate(R.layout.free_column_button_row,
                (ViewGroup) findViewById(R.id.llDialogView));
    }

    public ViewGroup addFillEmptyInputsWithZeroesCheckBox() {
        return (ViewGroup) inflater.inflate(R.layout.free_column_check_box_row,
                (ViewGroup) findViewById(R.id.llDialogView));
    }

    public void addRowsLayout(int rows) {
        for (int i = 0; i < rows; i++) {
            // As per xml files, the child at position (i) of llDialogView is the linear layout,
            // which holds the FreeColumnRow views.
            ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.free_column_input_row,
                    (ViewGroup) findViewById(R.id.llDialogView));
            for (int j = 0; j < vg.getChildCount(); j++) {
                // Use this because EditText is a subclass of TextView
                if (vg.getChildAt(j).getClass().equals(TextView.class) || vg.getChildAt(j) instanceof AppCompatTextView) {
                    ((TextView) vg.getChildAt(j)).setText(String.format(Locale.US, "%s%d:", TEXT_VIEW_KEY, i + 1));
                }
            }
        }
    }
}
