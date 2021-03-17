package com.akiva.adam.finalproject.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.activities.ImageListActivity;
import com.akiva.adam.finalproject.activities.MainActivity;
import com.akiva.adam.finalproject.activities.MyActivity;
import com.akiva.adam.finalproject.activities.SettingsActivity;
import com.akiva.adam.finalproject.activities.UpdateProfileActivity;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.akiva.adam.finalproject.uiElements.MyDialog;

import java.net.ConnectException;

import javax.inject.Inject;

import static com.akiva.adam.finalproject.activities.MyActivity.DEFAULT_DIALOG_BODY_TEXT_SIZE;
import static com.akiva.adam.finalproject.activities.MyActivity.DEFAULT_DIALOG_TITLE_TEXT_SIZE;
import static com.akiva.adam.finalproject.activities.MyActivity.TOAST_MESSAGE;

/**
 * A custom made toolbar which is displayed in all the activities
 * which require the user to be logged in
 */
public class MyToolbar extends Toolbar {

    // The current logged in user instance
    @Inject
    public MyUser user;
    private IUser mUser;

    private Context context;
    private LayoutInflater inflater;
    private View v;

    // Controls for the toolbar
    private Toolbar toolbar;
    private ImageView ivLogout;
    private ImageView ivSettings;
    private ImageView ivHome;
    private ImageView ivUpdateProfile;

    private LinearLayout llSort;
    private TextView tvSortByNew;

    private static final String TAG = MyToolbar.class.getName();


    // Reasons for these constructors:
    // https://stackoverflow.com/questions/9195713/do-i-need-all-three-constructors-for-an-android-custom-view
    public MyToolbar(Context context) {
        super(context);
    }

    public MyToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor for MyToolbar class
     *
     * @param context  Current activity context
     * @param rootView View to attach the toolbar to
     */
    public MyToolbar(final Context context, ViewGroup rootView) throws IllegalArgumentException {
        super(context);

        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) context.getApplicationContext()).getDatabaseComponent().inject(this);

        mUser = user;

        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            v = inflater.inflate(R.layout.main_toolbar, rootView, true);
        } else {
            throw new IllegalArgumentException("Context is not valid");
        }

        toolbar = v.findViewById(R.id.toolBar);
        ivLogout = v.findViewById(R.id.ivLogout);
        ivSettings = v.findViewById(R.id.ivSettings);
        ivHome = v.findViewById(R.id.ivHome);
        ivUpdateProfile = v.findViewById(R.id.ivUpdateProfile);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();
        params.addRule(RelativeLayout.ABOVE, R.id.toolBar);

        llSort = v.findViewById(R.id.llSort);
        tvSortByNew = v.findViewById(R.id.tvSortByNew);

        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
        }

        ivLogout.setOnClickListener((View v) -> {
            final MyDialog dialog = new MyDialog(context);
            dialog.setTitle(context.getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(context
                    .getString(R.string.logoutInfo), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(context
                    .getString(R.string.yes), (View v2) -> {
                dialog.dismiss();
                try {
                    mUser.requestLogout();
                    Bundle bundle = new Bundle();
                    bundle.putString(TOAST_MESSAGE, context.getString(R.string.logoutSuccessful));
                    goToActivity(MainActivity.class, bundle);
                } catch (ConnectException e) {
                    MyActivity.displayError(TAG, e, (Activity) context, context.getString(R.string
                            .noInternetConnection), null);
                }
            }).setNegativeButton(context.getString(R.string.no), (View v2) -> dialog.dismiss()).show();
        });

        // Activate the buttons to go to specific activities except the one the user is currently
        // in
        if (!(context instanceof SettingsActivity)) {
            ivSettings.setOnClickListener((View v) -> goToActivity(SettingsActivity.class, null));
        }

        if (!(context instanceof ImageListActivity)) {
            ivHome.setOnClickListener((View v) -> goToActivity(ImageListActivity.class, null));
        }

        if (!(context instanceof UpdateProfileActivity)) {
            ivUpdateProfile.setOnClickListener((View v) -> goToActivity(UpdateProfileActivity.class, null));
        }

    }

    public MyToolbar setSort(final MyAdapter adapter) {
        tvSortByNew.setOnClickListener((View v) -> adapter.sortImagesByNew());
        llSort.setVisibility(VISIBLE);
        return this;
    }

    public MyToolbar setCustomLogoutButtonEvent(View.OnClickListener listener) {
        ivLogout.setOnClickListener(listener);
        return this;
    }

    public MyToolbar setCustomSettingsButtonEvent(View.OnClickListener listener) {
        ivSettings.setOnClickListener(listener);
        return this;
    }

    public MyToolbar setCustomHomeButtonEvent(View.OnClickListener listener) {
        ivHome.setOnClickListener(listener);
        return this;
    }

    public MyToolbar setCustomUpdateProfileEvent(View.OnClickListener listener) {
        ivUpdateProfile.setOnClickListener(listener);
        return this;
    }

    private void goToActivity(@NonNull Class activity, @Nullable Bundle bundle) {
        Intent intent = new Intent(context, activity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
