package com.akiva.adam.finalproject.uiElements;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akiva.adam.finalproject.R;

/**
 * A custom made alert toast
 */
public class AlertToast extends Toast {

    private static int screen_width;
    private static int screen_height;

    private static final int WIDTH = 200;
    private static final int TEXT_SIZE_DP = 15;
    public static final String TAG = AlertToast.class.getName();

    /**
     * A constructor for AlertToast class
     *
     * @param activity Current activity
     */
    AlertToast(Activity activity) {
        super(activity);
    }

    /**
     * A method used to mimic the normal usage of a toast
     *
     * @param activity    Current activity
     * @param text        String containing text to be displayed to the user
     * @param toastLength Integer indicating the length the toast will stay
     * @return AlertToast object
     */
    public static AlertToast makeText(Activity activity, String text, int toastLength) {

        getScreenSize(activity);

        float scale;
        int width;
        int numOfCharactersInRow;
        int numOfCharactersInColumn;
        int height;

        AlertToast alertToast = new AlertToast(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.alert_toast_layout, (ViewGroup) activity
                .findViewById(R.id.rlAlertToast));

        TextView textView = layout.findViewById(R.id.tvAlertToastText);
        textView.setGravity(Gravity.CENTER);
        scale = ((Context) activity).getResources().getDisplayMetrics().density;
        width = (int) (WIDTH * scale);
        textView.setText(text);
        textView.setPadding(20, 0, 20, 0);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_DP);
        numOfCharactersInRow = (int) Math.ceil(width / (TEXT_SIZE_DP * scale));
        numOfCharactersInColumn = (int) Math.ceil((double) text.length() / numOfCharactersInRow);
        height = (int) (TEXT_SIZE_DP * numOfCharactersInColumn * scale);
        textView.getLayoutParams().height = height;

        alertToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, screen_height / 7);
        alertToast.setDuration(toastLength);
        alertToast.setView(layout);
        return alertToast;
    }

    /**
     * A method for inner use to get the device's screen size in pixels
     *
     * @param activity Calling activity
     */
    private static void getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;
    }

}
