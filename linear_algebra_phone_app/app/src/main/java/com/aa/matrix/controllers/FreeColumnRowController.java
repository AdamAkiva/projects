package com.aa.matrix.controllers;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aa.matrix.R;
import com.aa.matrix.models.MainModel;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.FreeColumnView;
import com.aa.matrix.views.MainActivityView;

import static com.aa.matrix.views.BaseActivity.GAUSS_JORDEN;

public class FreeColumnRowController {

    private final MainModel model = MainModel.getInstance();

    private final MainActivityView view;
    private final int columns;
    private final FreeColumnView freeColumnView;

    private static final String TAG = FreeColumnView.class.getName();

    public FreeColumnRowController(final MainActivityView view, final int columns) {
        this.view = view;
        this.columns = columns;
        freeColumnView = new FreeColumnView((LinearLayout) view.findViewById(R.id.llFreeColumnRow), view, columns);
    }

    private View.OnClickListener createSubmitAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Vector vector = new Vector(new double[columns]);
                LinearLayout parent = view.findViewById(R.id.llFreeColumnRow);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (parent.getChildAt(i) instanceof EditText) {
                        final String variableValue = ((EditText) parent.getChildAt(i)).getText().toString();
                        if (variableValue.length() <= 0) {
                            return; // needs some way to display to user the variable can't be empty
                        } else {
                            vector.getVectorAsArray()[i] = Double.parseDouble(variableValue);
                        }
                    }
                }
                model.setFreeColumn(vector);
                view.goToResultActivity(GAUSS_JORDEN);
            }
        };
    }

    public void build() {
        freeColumnView.build(createSubmitAction());
    }

    public void hide() {
        freeColumnView.hide();
    }
}
