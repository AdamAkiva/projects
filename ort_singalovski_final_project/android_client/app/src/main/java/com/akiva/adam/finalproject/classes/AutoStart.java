package com.akiva.adam.finalproject.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.akiva.adam.finalproject.notifications.NotificationService;

/**
 * A class used to handle a reboot system broadcast
 */
public class AutoStart extends BroadcastReceiver {

    /**
     * On reboot, start NotificationService
     *
     * @param context Main application context
     * @param value   The action that specified this broadcast to start
     */
    @Override
    public void onReceive(Context context, Intent value) {
        if (value.getAction() != null && value.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent = new Intent(context, NotificationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }
}
