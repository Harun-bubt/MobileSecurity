package com.mobilesecurity.mobileapp.WifiSecurity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WakelockHandler extends BroadcastReceiver {
    private static WakelockHandler instance = null;
    private Context context = null;
    private AlarmManager alarmManager = null;

    public static WakelockHandler getInstance(Context ctx) {
        if (instance == null) {
            instance = new WakelockHandler(ctx);
        }
        return instance;
    }

    public WakelockHandler(Context ctx) {
        this.context = ctx;
        this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        ensureAwake();
    }

    public void scheduleAlarms() {
        Intent wakeupIntent = new Intent(context, WakelockHandler.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, this.hashCode(), wakeupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    public void ensureAwake() {
        Log.v("WifiSecurity", "Ensuring we're still awake");
        Intent wakeupIntent = new Intent(context, WakelockHandler.class);
        // Check if the alarm is still scheduled
        if (PendingIntent.getBroadcast(context, this.hashCode(), wakeupIntent, PendingIntent.FLAG_NO_CREATE) == null) {
            Log.w("WifiSecurity", "Re-scheduling alarms, since our alarm manager didn't seem to be running");
            scheduleAlarms();
        }
    }

    public WakelockHandler() {
        // Only used for the BroadcastReceiver aspect of this class
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("WifiSecurity", "Waking up because of alarm");
        WifiManager wifiManager =  (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
    }
}