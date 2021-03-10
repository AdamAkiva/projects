package com.aa.matrix.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.aa.matrix.views.BaseActivity.OPERATION;
import static com.aa.matrix.views.BaseActivity.ZERO;

public abstract class BaseController {

    private static final int MAXIMUM_THREAD_NUMBER = 5;
    private static final ExecutorService service = Executors.newFixedThreadPool(MAXIMUM_THREAD_NUMBER);

    public ExecutorService getService() {
        return service;
    }

    public View.OnFocusChangeListener buildHideKeyboardListener(final View view, final Activity activity) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(view, activity);
                }
            }
        };
    }

    private void hideKeyboard(View view, Context activityContext) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activityContext.getSystemService(Activity
                .INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), ZERO);
        }
    }

    protected void goToResultActivity(Context context, Class<?> activityToStart, int opn) {
        Intent intent = new Intent(context, activityToStart);
        intent.putExtra(OPERATION, opn);
        context.startActivity(intent);
    }
}
