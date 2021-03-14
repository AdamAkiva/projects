package com.aa.matrix.controllers;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.aa.matrix.R;
import com.aa.matrix.models.BaseModel;
import com.aa.matrix.models.Calculation;
import com.aa.matrix.models.Matrix;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.FreeColumnDialog;
import com.aa.matrix.views.ResultActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class FreeColumnDialogController extends BaseController {

    private final BaseModel model = BaseModel.getInstance();

    private final Activity callerActivity;

    private final FreeColumnDialog view;

    private final int rows;
    private final int cols;
    private final String[] matrixValues;

    private final TextInputLayout[] textInputLayouts;

    public FreeColumnDialogController(final Activity callerActivity, final String[] matrixValues,
                                      final int rows, final int cols) {
        this.callerActivity = callerActivity;
        // Hint for later: When you create the view the onCreate is not called, it is only called,
        // when the view is displayed to the user, therefore all the view inflating MUST be called
        // only after the show() (for dialogs, for other view other methods)
        this.view = new FreeColumnDialog(callerActivity);
        this.matrixValues = matrixValues;
        this.rows = rows;
        this.cols = cols;
        textInputLayouts = new TextInputLayout[rows];
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
                String[] freeColumnValues = new String[textInputLayouts.length];
                for (int i = 0; i < textInputLayouts.length; i++) {
                    String value = textInputLayouts[i].getEditText().getText().toString();
                    if (!value.isEmpty()) {
                        freeColumnValues[i] = value;
                    } else {
                        Snackbar.make(view.getRootView(), VECTOR_MUST_BE_FULL, Snackbar.LENGTH_LONG).show();
                    }
                }
                try {
                    model.setMatrixObject(Calculation.GAUSS_JORDAN, Matrix.convertStringArrayTo2DArray(matrixValues, rows, cols),
                            Vector.convertStringArrayTo1DArray(freeColumnValues), rows, cols);
                } catch (NumberFormatException e) {
                    Snackbar.make(view.getRootView(), SMART_ASS, Snackbar.LENGTH_LONG).show();
                }
                goToResultActivity(callerActivity, ResultActivity.class, OPERATION, Calculation.GAUSS_JORDAN);
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
        attachCheckBoxEvent((MaterialCheckBox) vg.findViewById(R.id.cbFreeColumnFillWithZeroes));
    }

    private void attachCheckBoxEvent(MaterialCheckBox cbFillWithZeroes) {
        cbFillWithZeroes.setOnCheckedChangeListener(fillEmptyInputsWithZeroesEvent());
    }

    private void addButtons() {
        ViewGroup vg = view.addButtonsLayout();
        MaterialButton btnSubmit = vg.findViewById(R.id.btnSubmit);
        MaterialButton btnCancel = vg.findViewById(R.id.btnCancel);
        attachButtonEvents(btnSubmit, btnCancel);
        attachHideKeyboardFocusChangeEvents(btnSubmit, btnCancel);
    }

    public void attachButtonEvents(MaterialButton btnSubmit, MaterialButton btnCancel) {
        btnSubmit.setOnClickListener(submitFreeColumnValuesEvent());
        btnCancel.setOnClickListener(closeDialogViewEvent());
    }

    public void attachHideKeyboardFocusChangeEvents(MaterialButton btnSubmit, MaterialButton btnCancel) {
        LinearLayout root = view.findViewById(R.id.llDialogView);
        btnSubmit.setOnFocusChangeListener(buildHideKeyboardListener(root, callerActivity));
        btnCancel.setOnFocusChangeListener(buildHideKeyboardListener(root, callerActivity));
    }

    private void populateEditTextArrays() {
        ViewGroup vg = view.findViewById(R.id.llDialogView);
        // Hint: currently xml structure looks like this:
        // <LinearLayout>
        //    <LinearLayout>
        //       <TextInputLayout>
        //          <FrameLayout> (Not written by me, however it's implements by TextInputLayout)
        //             <TextView />
        //             <TextInputEditText />  (What we need)
        //          </FrameLayout>
        //       </TextInputLayout>
        //    </LinearLayout>
        // </LinearLayout>
        // and we are looking for the TextInputEditText, so that's what the loop is for.
        // vg holds the first LinearLayout so we need to get vg.childAt(0).childAt(0).childAt(0),
        // which will give us the TextInputLayout to iterate upon.
        // if the structure changes this will longer be valid.
        for (int i = 0; i < vg.getChildCount(); i++) {
            ViewGroup parent = (ViewGroup) ((ViewGroup) vg.getChildAt(i)).getChildAt(0);
            if (parent instanceof TextInputLayout) {
                textInputLayouts[i] = (TextInputLayout) parent;
            }
        }
    }

    private void setHintsForTextInputEditTextFields() {
        for (int i = 0; i < textInputLayouts.length; i++) {
            textInputLayouts[i].setHint(view.getContext().getResources().getString(R.string.freeColumnCellHint, i + 1));
        }
    }

    private void fillEmptyInputsWithZeroes() {
        for (TextInputLayout textInputEditText : textInputLayouts) {
            if (textInputEditText.getEditText().getText().toString().equals(EMPTY_STRING)) {
                textInputEditText.getEditText().setText(String.valueOf(0));
            }
        }
    }

    private void emptyInputsWithZeroes() {
        for (TextInputLayout textInputEditText : textInputLayouts) {
            if (textInputEditText.getEditText().getText().toString().equals(String.valueOf(0))) {
                textInputEditText.getEditText().setText(EMPTY_STRING);
            }
        }
    }

    public void build() {
        view.show();
        // Must be called here and in this order not beforehand look at the explanation above.
        addRows();
        populateEditTextArrays();
        setHintsForTextInputEditTextFields();
        addCheckBox();
        addButtons();
    }
}
