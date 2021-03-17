package com.akiva.adam.finalproject.uiElements;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akiva.adam.finalproject.classes.MySpanString;
import com.akiva.adam.finalproject.R;

/**
 * A custom made Dialog class which calculates the size
 * according to the length of the text
 */
public class MyDialog extends Dialog {

    private Context context;

    private float scale;
    private int width;

    private int titleHeight;
    private int bodyHeight;
    private int totalHeight;

    private RelativeLayout rlMyDialog;

    private static final int WIDTH = 250;
    private static final int DEFAULT_BUTTON_SIZE = 36;
    // Static variable used for logging
    public static final String TAG = MyDialog.class.getName();

    /**
     * A constructor for MyDialog class
     *
     * @param context Non-null current activity context
     */
    public MyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.two_buttons_dialog);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        scale = context.getResources().getDisplayMetrics().density;
        width = (int) (WIDTH * scale);
        rlMyDialog = (RelativeLayout) findViewById(R.id.rlMyDialog);
    }

    /**
     * A method to set a title for the custom dialog
     *
     * @param title      String with the title text
     * @param textSizeDp integer indicating the text size
     * @return Current custom dialog object
     */
    public MyDialog setTitle(String title, int textSizeDp) {
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(new MySpanString(title).setUnderLine().setBold());
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeDp);
        tvTitle.setPadding(30, 0, 30, 0);
        int numOfCharactersInRow = (int) Math.ceil(width / (textSizeDp * scale));
        int numOfCharactersInColumn = (int) Math.ceil((double) title.length() / numOfCharactersInRow);
        titleHeight = textSizeDp * numOfCharactersInColumn;
        return this;
    }

    /**
     * A method to set a body for the custom dialog
     *
     * @param body       String with the main text
     * @param textSizeDp integer indicating the text size
     * @return Current custom dialog object
     */
    public MyDialog setBody(String body, int textSizeDp) {
        TextView tvBody = findViewById(R.id.tvBody);
        tvBody.setText(body);
        tvBody.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeDp);
        tvBody.setPadding(30, 0, 30, 0);
        int numOfCharactersInRow = (int) Math.ceil(width / (textSizeDp * scale));
        int numOfCharactersInColumn = (int) Math.ceil((double) body.length() / numOfCharactersInRow);
        bodyHeight = textSizeDp * numOfCharactersInColumn;
        return this;
    }

    /**
     * A method to set a positive button for the custom dialog
     *
     * @param text            String with the button text
     * @param onClickListener Listener indicating the on click event
     * @return Current custom dialog object
     */
    public MyDialog setPositiveButton(String text, View.OnClickListener onClickListener) {
        Button btnPositive = findViewById(R.id.btnPositive);
        btnPositive.setText(new MySpanString(text).setBold());
        btnPositive.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * A method to set a negative button for the custom dialog
     *
     * @param text            String with the button text
     * @param onClickListener Listener indicating the on click event
     * @return Current custom dialog object
     */
    public MyDialog setNegativeButton(String text, View.OnClickListener onClickListener) {
        Button btnNegative = findViewById(R.id.btnNegative);
        btnNegative.setText(new MySpanString(text).setBold());
        btnNegative.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * A method to set a progress bar on the custom dialog
     *
     * @return Current custom dialog object
     */
    public MyDialog setProgressBar() {
        ProgressBar pbLoading = findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        pbLoading.setProgressDrawable(context.getResources().getDrawable(R.drawable.custom_progress_bar));
        return this;
    }

    /**
     * A method to display the custom to dialog while also calculating and assigning the width and height of the
     * custom dialog
     */
    @Override
    public void show() {
        totalHeight = (int) ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BUTTON_SIZE, context
                .getResources().getDisplayMetrics()) + titleHeight + bodyHeight) * scale);
        rlMyDialog.getLayoutParams().width = width;
        rlMyDialog.getLayoutParams().height = totalHeight;
        super.show();
    }
}