package com.aa.matrix.controllers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aa.matrix.models.MainModel;
import com.aa.matrix.models.Vector;
import com.aa.matrix.views.FreeColumnView;

import java.util.concurrent.ExecutionException;

public class FreeColumnRowController {

    private final MainModel model = MainModel.getInstance();

    private final LinearLayout view;
    private final String[] matrixValues;
    private final int rows;
    private final int columns;
    private final FreeColumnView freeColumnView;

    private static final String TAG = FreeColumnView.class.getName();

    public FreeColumnRowController(final LinearLayout view, final Context context,
                                   final String[] matrixValues, final int rows, final int columns) {
        this.view = view;
        this.matrixValues = matrixValues;
        this.rows = rows;
        this.columns = columns;
        freeColumnView = new FreeColumnView(view, context, columns);
    }

    private View.OnClickListener createSubmitAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Vector vector = new Vector(new double[columns]);
                for (int i = 0; i < view.getChildCount(); i++) {
                    if (view.getChildAt(i) instanceof EditText) {
                        final String variableValue = ((EditText) view.getChildAt(i)).getText().toString();
                        if (variableValue.length() <= 0) {
                            return; // needs some way to display to user the variable can't be empty
                        } else {
                            vector.getVectorAsArray()[i] = Double.parseDouble(variableValue);
                        }
                    }
                }
                try {
                    Vector resultVector = model.calculateGaussJorden(matrixValues, rows, columns, vector);
                    // need to create new activity to display matrix snapshots + result
                } catch (ExecutionException | InterruptedException e) {
                    Log.d(TAG, e.getMessage() != null ? e.getMessage() : "null");
                }
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
