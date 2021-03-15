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

/**
 * @author Adam Akiva
 * Class used for the FreeColumnDialogView
 */
public class FreeColumnDialog extends Dialog {

    private final LayoutInflater inflater;
    private LinearLayout llDialogView;

    /**
     * @param parent Activity which started the dialog
     */
    public FreeColumnDialog(Activity parent) {
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
        // onStop remove all the added views to free memory and prevent leaks
        super.onStop();
        for (int i = 0; i < llDialogView.getChildCount(); i++) {
            View v = llDialogView.getChildAt(i);
            if (!(v instanceof ViewGroup)) {
                llDialogView.removeView(v);
            }
        }
    }

    /**
     * Method used to return a ViewGroup of an xml file
     * @return inflated ViewGroup on free_column_button_row.xml
     */
    public ViewGroup addButtonsLayout() {
        return (ViewGroup) inflater.inflate(R.layout.free_column_button_row,
                (ViewGroup) findViewById(R.id.llDialogView));
    }

    /**
     * Method used to return a ViewGroup of an xml file
     * @return inflated ViewGroup on free_column_check_box_row.xml
     */
    public ViewGroup addFillEmptyInputsWithZeroesCheckBox() {
        return (ViewGroup) inflater.inflate(R.layout.free_column_check_box_row,
                (ViewGroup) findViewById(R.id.llDialogView));
    }

    /**
     * Method used to return a ViewGroup of an xml file
     * @param rows Integer holding the amount of times the ViewGroup should be inflated
     */
    public void addRowsLayout(int rows) {
        for (int i = 0; i < rows; i++) {
            inflater.inflate(R.layout.free_column_input_row,
                    (ViewGroup) findViewById(R.id.llDialogView));
        }
    }

    /**
     * Method used to get the root view of the activity which started the dialog
     * @return Root view of the caller activity
     */
    public ViewGroup getRootView() {
        return (ViewGroup) findViewById(R.id.rlMainActivity);
    }
}
