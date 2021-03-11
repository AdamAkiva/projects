package com.aa.matrix.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.aa.matrix.R;

public class FreeColumnDialogView extends Dialog {

    private LinearLayout llDialogView;

    private final LayoutInflater inflater;

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
            inflater.inflate(R.layout.free_column_input_row,
                    (ViewGroup) findViewById(R.id.llDialogView));
        }
    }
}
