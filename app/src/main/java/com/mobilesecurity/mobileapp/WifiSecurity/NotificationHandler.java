package com.mobilesecurity.mobileapp.WifiSecurity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;
import android.util.Log;
import com.mobilesecurity.mobileapp.R;

public class NotificationHandler {
    public static final int PERMISSION_NOTIFICATION_ID = 0;
    public static final int LOCATION_NOTIFICATION_ID = 1;

    Context context = null;
    NotificationManager notificationManager = null;

    NotificationHandler(Context ctx) {
        this.context = ctx;
        notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Disables all currently displayed notifications by PrivacyPolice. This allows us to remove
     * stale networks.
     */
    public void disableNotifications() {
        notificationManager.cancelAll();
    }

    /**
     * Asks the user whether it is certain that a network should be currently available
     * @param SSID The name of the network
     * @param BSSID The MAC address of the access point that triggered this (only used when we will block the AP)
     */
    public void askNetworkPermission(String SSID, String BSSID) {
        Log.d("WifiSecurity", "Asking permission for " + SSID + " (" + BSSID + ")");
        // Intent that will be used when the user allows the network
        Intent addIntent = new Intent(context, PermissionChangeReceiver.class);
        addIntent.putExtra("SSID", SSID).putExtra("BSSID", BSSID).putExtra("enable", true);
        PendingIntent addPendingIntent = PendingIntent.getBroadcast(context, 0, addIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Intent that will be used when the user blocks the network
        Intent disableIntent = new Intent(context, PermissionChangeReceiver.class);
        disableIntent.putExtra("SSID", SSID).putExtra("BSSID", BSSID).putExtra("enable", false);
        PendingIntent disablePendingIntent = PendingIntent.getBroadcast(context, 1, disableIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // Intent that will be used when the user's OS does not support notification actions
        Intent activityIntent = new Intent(context, AskPermissionActivity.class);
        activityIntent.putExtra("SSID", SSID).putExtra("BSSID", BSSID);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 2, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Build the notification dynamically, based on the network name
        Resources res = context.getResources();
        String headerString = String.format(res.getString(R.string.permission_header), SSID);
        String permissionString = String.format(res.getString(R.string.ask_permission), SSID);
        String yes = res.getString(R.string.yes);
        String no = res.getString(R.string.no);

        // NotificationCompat makes sure that the notification will also work on Android <4.0
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX) // To force it to be first in list (and thus, expand)
                .setContentTitle(headerString)
                .setContentText(permissionString)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(permissionString))
                .setContentIntent(activityPendingIntent)
                .addAction(android.R.drawable.ic_delete, no, disablePendingIntent)
                .addAction(android.R.drawable.ic_input_add, yes, addPendingIntent);
        notificationManager.notify(PERMISSION_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void cancelPermissionRequest() {
        notificationManager.cancel(PERMISSION_NOTIFICATION_ID);
    }

    public void askLocationPermission() {
        PreferencesStorage prefs = new PreferencesStorage(context);
        if (!prefs.getLocationNoticeEnabled()) {
            Log.d("WifiSecurity", "Location nagging is disabled. Not showing notification");
            return;
        }

        Log.d("WifiSecurity", "Asking location permission");
        Resources res = context.getResources();
        Intent locationNoticeIntent = new Intent(context, LocationNoticeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, locationNoticeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(res.getString(R.string.location_permission_header))
                .setContentText(res.getString(R.string.location_permission_explanation))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(res.getString(R.string.location_permission_explanation)))
                .setContentIntent(pendingIntent);
        notificationManager.notify(LOCATION_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void cancelLocationPermissionRequest() {
        notificationManager.cancel(LOCATION_NOTIFICATION_ID);
    }
}