package com.mobilesecurity.mobileapp.WifiSecurity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class LocationAccess extends BroadcastReceiver {
    private LocationManager locationManager;

    public LocationAccess() {
        locationManager = null;
    }

    private void getLocationManager(Context context) {
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isNetworkLocationEnabled(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 23) {
           
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("WifiSecurity", "I don't seem to have the correct runtime permission!");
            return false;
        }
        getLocationManager(context);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

 
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHandler notificationHandler = new NotificationHandler(context);

        if (!isNetworkLocationEnabled(context)) {
            notificationHandler.askLocationPermission();
        } else
        
            notificationHandler.cancelLocationPermissionRequest();
    }
}