package com.akiva.adam.finalproject.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akiva.adam.finalproject.activities.MyActivity;
import com.akiva.adam.finalproject.activities.ShowImageActivity;
import com.akiva.adam.finalproject.dagger.MyApp;
import com.akiva.adam.finalproject.interfaces.IFile;
import com.akiva.adam.finalproject.R;
import com.akiva.adam.finalproject.interfaces.IUser;
import com.akiva.adam.finalproject.uiElements.AlertToast;
import com.akiva.adam.finalproject.uiElements.MyDialog;

import java.net.ConnectException;
import java.util.ArrayList;

import javax.inject.Inject;

import static com.akiva.adam.finalproject.activities.MyActivity.DEFAULT_DIALOG_BODY_TEXT_SIZE;
import static com.akiva.adam.finalproject.activities.MyActivity.DEFAULT_DIALOG_TITLE_TEXT_SIZE;
import static com.akiva.adam.finalproject.activities.MyActivity.KEY;

/**
 * A class used to set an adapter for the recycled view in the
 * ImageListActivity activity
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    // The current logged in user instance
    @Inject
    public MyUser user;
    private IUser mUser;

    private Context context;
    private Activity activity;
    private volatile ArrayList<String> keys;
    private ArrayList<String> originalKeys;
    private MyToolbar toolbar;
    private boolean returnToOrigin = false;
    private String btnText;
    private IFile chosenImage;

    private GradientDrawable gradientDrawableNew = new GradientDrawable();
    private GradientDrawable gradientDrawableDefault = new GradientDrawable();

    // Static variable used for logging
    public static final String TAG = MyAdapter.class.getName();

    /**
     * Static inner class used to save data in order to
     * save processing time,
     * see: https://developer.android.com/reference/android/support/v7/widget/RecyclerView
     * .ViewHolder for details
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView tvDate;
        Button btnShowImage;
        Button btnDeleteImage;

        private ViewHolder(View v) {
            super(v);
            this.linearLayout = (LinearLayout) v;
            tvDate = (TextView) linearLayout.findViewById(R.id.tvDate);
            btnShowImage = (Button) linearLayout.findViewById(R.id.btnShowImage);
            btnDeleteImage = (Button) linearLayout.findViewById(R.id.btnDeleteImage);
        }
    }

    /**
     * A constructor for MyAdapter class
     *
     * @param context Current activity context
     * @param keys    ArrayList containing the images keys
     * @param btnText Non-null String used for displaying the message
     *                near the column
     */
    public MyAdapter(Context context, ArrayList<String> keys, @NonNull String btnText, MyToolbar toolbar) {
        // Set the activity to be applicable to receive an injection from the Dagger2 interface
        ((MyApp) context.getApplicationContext()).getDatabaseComponent().inject(this);
        mUser = user;
        this.context = context;
        this.activity = (Activity) context;
        this.btnText = btnText;
        this.keys = new ArrayList<>(keys);
        this.originalKeys = new ArrayList<>(keys);
        this.toolbar = toolbar;
        this.toolbar.setSort(this);
        gradientDrawableNew.setColor(context.getResources().getColor(R.color.white));
        gradientDrawableNew.setCornerRadius(5);
        gradientDrawableNew.setStroke(5, context.getResources().getColor(R.color.yellow));
        gradientDrawableDefault.setColor(context.getResources().getColor(R.color.white));
        gradientDrawableDefault.setCornerRadius(0);
        gradientDrawableDefault.setStroke(0, context.getResources().getColor(R.color.white));
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout
                .rowlayout, parent, false);
        return new ViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        String fullDate = mUser.getFiles().get(keys.get(viewHolder.getAdapterPosition())).getViewDate() + ", " +
                mUser.getFiles().get(keys.get(viewHolder.getAdapterPosition())).getViewTime();
        viewHolder.tvDate.setText(fullDate);
        if (!mUser.getFiles().get(keys.get(viewHolder.getAdapterPosition())).getViewed()) {
            viewHolder.linearLayout.setBackground(gradientDrawableNew);
        } else {
            viewHolder.linearLayout.setBackground(gradientDrawableDefault);
        }
        viewHolder.btnDeleteImage.setOnClickListener((View v) -> {
            final MyDialog dialog = new MyDialog(context);
            dialog.setTitle(context.getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody(context
                    .getString(R.string.imageDeleteInfo), DEFAULT_DIALOG_BODY_TEXT_SIZE).setPositiveButton(context
                    .getString(R.string.yes), (View v2) -> {
                dialog.setProgressBar();
                try {
                    mUser.getFileListLock().lock();
                    mUser.deleteFile(mUser.getFiles().get(keys.get(viewHolder.getAdapterPosition())));
                    new Thread(() -> {
                        while (true) {
                            if (true) {
                                keys.remove(viewHolder.getAdapterPosition());
                                activity.runOnUiThread(() -> {
                                    MyAdapter.this.notifyDataSetChanged();
                                    AlertToast.makeText((Activity) context, context.getString(R.string
                                            .deleteSuccessful), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                });
                                mUser.setDeleteSuccessful(null);
                                break;
                            } else if (mUser.getDeleteSuccessful() != null && !mUser.getDeleteSuccessful()) {
                                activity.runOnUiThread(() -> {
                                    AlertToast.makeText((Activity) context, context.getString(R.string.deleteFailed),
                                            Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                });
                                mUser.setDeleteSuccessful(null);
                                break;
                            }
                        }
                    }).start();
                    mUser.getFileListLock().unlock();
                } catch (ConnectException e) {
                    dialog.dismiss();
                    mUser.getFileListLock().unlock();
                    MyActivity.displayError(TAG, e, (Activity) context, context.getString(R.string
                            .noInternetConnection), null);
                }
            }).setNegativeButton(context.getString(R.string.no), (View v2) -> dialog.dismiss()).show();
        });
        viewHolder.btnShowImage.setOnClickListener((View v) -> {
            try {
                mUser.getFileListLock().lock();
                chosenImage = mUser.getFiles().get(keys.get(viewHolder.getAdapterPosition()));
                chosenImage.getFileFromDatabase();
                final MyDialog dialog = new MyDialog(context);
                dialog.setTitle(context.getString(R.string.areYouSure), DEFAULT_DIALOG_TITLE_TEXT_SIZE).setBody
                        (context.getString(R.string.disturbingImage), DEFAULT_DIALOG_BODY_TEXT_SIZE)
                        .setPositiveButton(context.getString(R.string.yes), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.setProgressBar();
                        new Thread(() -> {
                            while (true) {
                                if (chosenImage.getDoneDownloading()) {
                                    dialog.dismiss();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(KEY, new ArrayList<String>(mUser.getFiles().keySet()).get
                                            (viewHolder.getAdapterPosition()));
                                    goToShowImageActivity(context, bundle);
                                    return;
                                }
                            }
                        }).start();
                    }
                }).setNegativeButton(context.getString(R.string.no), (View v2) -> dialog.dismiss()).show();
                mUser.getFileListLock().unlock();
            } catch (ConnectException e) {
                MyActivity.displayError(TAG, e, (Activity) context, context.getString(R.string.noInternetConnection),
                        null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    private void goToShowImageActivity(Context context, @Nullable Bundle bundle) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * A method used to sort the images by the new ones first
     */
    public void sortImagesByNew() {
        if (keys.size() != 0) {
            mUser.getFileListLock().lock();
            if (!returnToOrigin) {
                for (int i = 0; i < keys.size(); i++) {
                    if (!mUser.getFiles().get(keys.get(i)).getViewed()) {
                        keys.add(0, keys.get(i));
                        keys.remove(i + 1);
                    }
                    returnToOrigin = true;
                }
            } else {
                keys.clear();
                keys.addAll(originalKeys);
            }
            notifyDataSetChanged();
            mUser.getFileListLock().unlock();
        }
    }
}
