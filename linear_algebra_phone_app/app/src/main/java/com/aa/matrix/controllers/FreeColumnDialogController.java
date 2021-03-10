package com.aa.matrix.controllers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aa.matrix.R;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.DisplayResultActivityView;
import com.aa.matrix.views.FreeColumnDialogView;

import static com.aa.matrix.views.BaseActivity.EMPTY_STRING;
import static com.aa.matrix.views.BaseActivity.GAUSS_JORDAN;
import static com.aa.matrix.views.BaseActivity.ZERO;

public class FreeColumnDialogController extends BaseController {

    private final BaseModel model = BaseModel.getInstance();

    private final Activity callerActivity;

    private final FreeColumnDialogView view;

    private final int rows;

    private final EditText[] editTexts;

    public FreeColumnDialogController(final Activity callerActivity, final int rows) {
        this.callerActivity = callerActivity;
        // Hint for later: When you create the view the onCreate is not called, it is only called,
        // when the view is displayed to the user, therefore all the view inflating MUST be called
        // only after the show() (for dialogs, for other view other methods)
        this.view = new FreeColumnDialogView(callerActivity);
        this.rows = rows;
        editTexts = new EditText[rows];
    }

    private CompoundButton.OnCheckedChangeListener fillEmptyInputsWithZeroesEvent() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fillEmptyInputsWithZeroes();
                } else {
                    emptyInputsWithZeroes();
                }
            }
        };
    }

    public View.OnClickListener submitFreeColumnValuesEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] freeColumnValues = new String[editTexts.length];
                for (int i = 0; i < editTexts.length; i++) {
                    freeColumnValues[i] = editTexts[i].getText().toString();
                }
                model.setFreeColumn(Vector.convertStringArrayToVector(freeColumnValues));
                goToResultActivity(callerActivity, DisplayResultActivityView.class, GAUSS_JORDAN);
            }
        };
    }

    public View.OnClickListener closeDialogViewEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.dismiss();
            }
        };
    }

    private void addRows() {
        view.addRowsLayout(rows);
    }

    private void addCheckBox() {
        ViewGroup vg = view.addFillEmptyInputsWithZeroesCheckBox();
        attachCheckBoxEvent((CheckBox) vg.findViewById(R.id.cbFreeColumnFillWithZeroes));
    }

    private void attachCheckBoxEvent(CheckBox cbFillWithZeroes) {
        cbFillWithZeroes.setOnCheckedChangeListener(fillEmptyInputsWithZeroesEvent());
    }

    private void addButtons() {
        ViewGroup vg = view.addButtonsLayout();
        Button btnSubmit = vg.findViewById(R.id.btnSubmit);
        Button btnCancel = vg.findViewById(R.id.btnCancel);
        attachButtonEvents(btnSubmit, btnCancel);
        attachHideKeyboardFocusChangeEvents(btnSubmit, btnCancel);
    }

    public void attachButtonEvents(Button btnSubmit, Button btnCancel) {
        btnSubmit.setOnClickListener(submitFreeColumnValuesEvent());
        btnCancel.setOnClickListener(closeDialogViewEvent());
    }

    public void attachHideKeyboardFocusChangeEvents(Button btnSubmit, Button btnCancel) {
        LinearLayout root = view.findViewById(R.id.llDialogView);
        btnSubmit.setOnFocusChangeListener(buildHideKeyboardListener(root, callerActivity));
        btnCancel.setOnFocusChangeListener(buildHideKeyboardListener(root, callerActivity));
    }

    private void populateEditTextArray() {
        ViewGroup vg = view.findViewById(R.id.llDialogView);
        // Hint: currently xml structure looks like this:
        // <LinearLayout>
        //    <LinearLayout>
        //       <EditText>
        //    </LinearLayout>
        // </LinearLayout>
        // and we are looking for the EditText, so that's what the loop is for.
        // if the structure changes this will longer be valid.
        for (int i = 0, counter = 0; i < vg.getChildCount(); i++) {
            ViewGroup parent = (ViewGroup) vg.getChildAt(i);
            for (int j = 0; j < parent.getChildCount(); j++) {
                if (parent.getChildAt(j) instanceof EditText) {
                    editTexts[counter++] = (EditText) parent.getChildAt(j);
                }
            }
        }
    }

    private void fillEmptyInputsWithZeroes() {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().equals(EMPTY_STRING)) {
                editText.setText(String.valueOf(ZERO));
            }
        }
    }

    private void emptyInputsWithZeroes() {
        for (EditText editText : editTexts) {
            if (editText.getText().toString().equals(String.valueOf(ZERO))) {
                editText.setText(EMPTY_STRING);
            }
        }
    }

    public void build() {
        view.show();
        // Must be called here not beforehand look at the explanation above.
        addRows();
        populateEditTextArray();
        addCheckBox();
        addButtons();
    }
}
