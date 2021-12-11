package com.mobilesecurity.mobileapp.WifiSecurity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class PermissionChangeReceiver extends BroadcastReceiver {

    private Context ctx;
    private PreferencesStorage prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        prefs = new PreferencesStorage(ctx);

        // Remove the notification that was used to make the decision
        removeNotification();

        boolean enable = intent.getBooleanExtra("enable", true);
        String SSID = intent.getStringExtra("SSID");
        String BSSID = intent.getStringExtra("BSSID");

        if (SSID == null || BSSID == null) {
            Log.e("WifiSecurity", "Could not set permission because SSID or BSSID was null!");
            return;
        }

        Log.d("WifiSecurity", "Permission change: " + SSID + " " + BSSID + " " + enable);

        if (enable) {
            prefs.addAllowedBSSIDsForLocation(SSID);
            // initiate rescan, to make sure our algorithm enables the network, and to make sure
            // that Android connects to it
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            wifiManager.startScan();
        } else
            prefs.addBlockedBSSID(SSID, BSSID);
    }

    private void removeNotification() {
        NotificationHandler notificationHandler = new NotificationHandler(ctx);
        notificationHandler.cancelPermissionRequest();
    }
}