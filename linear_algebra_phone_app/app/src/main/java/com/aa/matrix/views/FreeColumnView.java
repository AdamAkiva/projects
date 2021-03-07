package com.aa.matrix.views;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aa.matrix.R;
import com.aa.matrix.models.Matrix;

import static android.view.View.GONE;
import static com.aa.matrix.views.BaseActivity.BUTTON_TEXT_SIZE;
import static com.aa.matrix.views.BaseActivity.EDIT_TEXT_HEIGHT;
import static com.aa.matrix.views.BaseActivity.EDIT_TEXT_MAX_LENGTH;
import static com.aa.matrix.views.BaseActivity.EDIT_TEXT_SIZE;
import static com.aa.matrix.views.BaseActivity.EDIT_TEXT_START_MARGIN;
import static com.aa.matrix.views.BaseActivity.EDIT_TEXT_WIDTH;

public class FreeColumnView {

    private final LinearLayout view;
    private final Context context;

    private final int columns;

    private static int id = 1;

    private static final String TAG = FreeColumnView.class.getName();

    public FreeColumnView(final LinearLayout view, final Context context, final int columns) {
        this.view = view;
        this.context = context;
        this.columns = columns;
    }

    public void build(final View.OnClickListener onClickListener) {
        if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (EditText et : createEditTextArray()) {
                    view.addView(et, createEditTextParams());
                }
                view.addView(createButtonRow(onClickListener), createButtonParams());
            }
        });
    }

    public void hide() {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(GONE);
        }
    }

    private LinearLayout.LayoutParams createEditTextParams() {
        final LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                Math.round(BaseActivity.dpToPixels(EDIT_TEXT_WIDTH, context)),
                Math.round(BaseActivity.dpToPixels(EDIT_TEXT_HEIGHT, context)));
        editTextParams.leftMargin = EDIT_TEXT_START_MARGIN;
        return editTextParams;
    }

    private EditText[] createEditTextArray() {
        final EditText[] editTexts = new EditText[columns];
        for (int i = 0; id < columns + 1; id++, i++) {
            final EditText editText = new EditText(context);
            editText.setId(id);
            editText.setHint(context.getResources().getString(R.string.number));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            editText.setTextSize(EDIT_TEXT_SIZE);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(EDIT_TEXT_MAX_LENGTH)});
            if (id != columns) {
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            } else {
                editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
            editTexts[i] = editText;
        }
        return editTexts;
    }

    private LinearLayout.LayoutParams createButtonParams() {
        LinearLayout.LayoutParams llButtonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llButtonParams.leftMargin = Math.round(BaseActivity.dpToPixels(12, context));
        llButtonParams.topMargin = Math.round(BaseActivity.dpToPixels(20, context));
        return llButtonParams;
    }

    private View createButtonRow(View.OnClickListener onClickListener) {
        final Button btnSubmit = new Button(context, null, android.R.style.Widget_DeviceDefault_Button_Borderless);
        btnSubmit.setText(context.getResources().getText(R.string.submit));
        btnSubmit.setTextSize(BUTTON_TEXT_SIZE);
        btnSubmit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        btnSubmit.setAllCaps(false);
        btnSubmit.setOnClickListener(onClickListener);
        return btnSubmit;
    }
}
