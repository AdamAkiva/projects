package com.aa.matrix.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aa.matrix.etc.InvalidParameterException;

import static com.aa.matrix.views.BaseActivity.ERROR_TEXT_VIEW_BOTTOM_MARGIN;
import static com.aa.matrix.views.BaseActivity.ERROR_TEXT_VIEW_HEIGHT;
import static com.aa.matrix.views.BaseActivity.ERROR_TEXT_VIEW_TEXT_SIZE;
import static com.aa.matrix.views.BaseActivity.ERROR_TEXT_VIEW_TOP_MARGIN;
import static com.aa.matrix.views.BaseActivity.ERROR_TEXT_VIEW_WIDTH;
import static com.aa.matrix.views.BaseActivity.PARENT_VIEW_MUST_BE_RELATIVE_LAYOUT;

public abstract class ErrorTextView {

    private static int mId = 1;

    public static TextView displayErrorTextView(final Context context, final String text,
                                                final ViewGroup parent, final int id)
            throws InvalidParameterException {
        final TextView tv = createErrorTextView(context, text);
        if (parent instanceof RelativeLayout) {
            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    ERROR_TEXT_VIEW_WIDTH, context.getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    ERROR_TEXT_VIEW_HEIGHT, context.getResources().getDisplayMetrics()));
            params.topMargin = ERROR_TEXT_VIEW_TOP_MARGIN;
            params.bottomMargin = ERROR_TEXT_VIEW_BOTTOM_MARGIN;
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.BELOW, id);
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parent.addView(tv, params);
                }
            });
            return tv;
        } else {
        throw new InvalidParameterException(PARENT_VIEW_MUST_BE_RELATIVE_LAYOUT);
        }
    }

    private static TextView createErrorTextView(final Context context, final String text) {
        final TextView textView = new TextView(context);
        textView.setTextSize(ERROR_TEXT_VIEW_TEXT_SIZE);
        textView.setText(text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(Color.RED);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setId(mId++);
        return textView;
    }

    public static void removeErrorTextView(final Context context, final ViewGroup parent, final View v) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).getId() != View.NO_ID && v.getId() != View.NO_ID) {
                if (parent.getChildAt(i).getId() == v.getId()) {
                    final int index = i;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parent.removeViewAt(index);
                        }
                    });
                    return;
                }
            }
        }
    }
}
