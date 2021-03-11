package com.aa.matrix.models;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aa.matrix.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class InputMatrixAdapter extends RecyclerView.Adapter<InputMatrixAdapter.ViewHolder> {

    private final Context context;
    private final String[] matrixValues;
    private final int cols;

    private int rowIndex;
    private int colIndex;

    private boolean giveFocusToFirstElementOnViewCreation;

    public InputMatrixAdapter(final Context context, final String[] matrixValues, int cols) {
        this.context = context;
        this.matrixValues = matrixValues;
        this.cols = cols;
        this.rowIndex = 0;
        this.colIndex = 0;
        giveFocusToFirstElementOnViewCreation = true;
    }

    @NonNull
    @Override
    public InputMatrixAdapter.ViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, final int viewType) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.input_matrix_cell, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder vh, final int position) {
        if (giveFocusToFirstElementOnViewCreation && position == 0) {
            vh.editText.requestFocus();
            giveFocusToFirstElementOnViewCreation = false;
        }
        setHint(vh);
        vh.editText.setText(matrixValues[position]);
        vh.editText.addTextChangedListener(updateMatrixValuesListener(vh, position));
        if (vh.getAdapterPosition() != getItemCount() - 1) {
            vh.editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            vh.editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    @Override
    public int getItemCount() {
        return matrixValues.length;
    }

    private void setHint(ViewHolder vh) {
        if (colIndex == cols) {
            colIndex = 0;
            rowIndex++;
        }
        String hint = context.getResources().getString(R.string.matrixInputCellHint, rowIndex, colIndex++);
        if (vh.layout.getHint() == null || vh.layout.getHint() != hint) {
            vh.layout.setHint(hint);
        }
    }

    public void fillEmptyCellsWithZeroes() {
        for (int i = 0; i < getItemCount(); i++) {
            if (matrixValues[i].isEmpty()) {
                matrixValues[i] = String.valueOf(0);
                notifyItemChanged(i);
            }
        }
    }

    public void emptyCellsWithZeroes() {
        for (int i = 0; i < getItemCount(); i++) {
            if (matrixValues[i].equals(String.valueOf(0))) {
                matrixValues[i] = "";
                notifyItemChanged(i);
            }
        }
    }

    private TextWatcher updateMatrixValuesListener(final ViewHolder vh, final int position) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                matrixValues[position] = vh.editText.getText().toString();
            }
        };
    }

    /**
     * Static inner class used to save data in order to
     * save processing time,
     * see: https://developer.android.com/reference/android/support/v7/widget/RecyclerView
     * .ViewHolder for details
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextInputLayout layout;
        private final TextInputEditText editText;

        private ViewHolder(View v) {
            super(v);
            layout = v.findViewById(R.id.tilMatrixCell);
            editText = v.findViewById(R.id.etMatrixCell);
        }
    }
}
