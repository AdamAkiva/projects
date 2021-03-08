package com.aa.matrix.etc;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aa.matrix.R;

import static com.aa.matrix.views.BaseActivity.EMPTY_STRING;
import static com.aa.matrix.views.BaseActivity.ZERO;

public class InputMatrixAdapter extends RecyclerView.Adapter<InputMatrixAdapter.ViewHolder> {

    private final String[] matrixValues;
    private boolean giveFocusToFirstElementOnViewCreation;

    /**
     * Static inner class used to save data in order to
     * save processing time,
     * see: https://developer.android.com/reference/android/support/v7/widget/RecyclerView
     * .ViewHolder for details
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText editText;

        private ViewHolder(View v) {
            super(v);
            editText = (EditText) v.findViewById(R.id.etInputMatrixCell);
        }
    }

    public InputMatrixAdapter(final String[] matrixValues) {
        this.matrixValues = matrixValues;
        this.giveFocusToFirstElementOnViewCreation = true;
    }

    @NonNull
    @Override
    public InputMatrixAdapter.ViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, final int viewType) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.input_matrix_cell, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder viewHolder, final int position) {
        if (giveFocusToFirstElementOnViewCreation && position == 0) {
            viewHolder.editText.requestFocus();
            giveFocusToFirstElementOnViewCreation = false;
        }
        viewHolder.editText.setText(matrixValues[position]);
        viewHolder.editText.addTextChangedListener(updateMatrixValuesListener(viewHolder, position));
        if (viewHolder.getAdapterPosition() != getItemCount() - 1) {
            viewHolder.editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            viewHolder.editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    @Override
    public int getItemCount() {
        return matrixValues.length;
    }

    public void fillEmptyCellsWithZeroes() {
        for (int i = 0; i < getItemCount(); i++) {
            if (matrixValues[i].equals(EMPTY_STRING)) {
                matrixValues[i] = String.valueOf(ZERO);
                notifyItemChanged(i);
            }
        }
    }

    private TextWatcher updateMatrixValuesListener(final ViewHolder vh, final int position) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                matrixValues[position] = vh.editText.getText().toString();
            }
        };
    }
}
