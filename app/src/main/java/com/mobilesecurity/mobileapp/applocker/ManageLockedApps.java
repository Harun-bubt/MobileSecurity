package com.mobilesecurity.mobileapp.applocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;


import com.mobilesecurity.mobileapp.R;
import java.util.List;

import static com.mobilesecurity.mobileapp.applocker.ColorManager.PACKAGE_NAME;


public class ManageLockedApps {

    public static void resetLockedApps(Context context){
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        PackageManager packageManager = context.getPackageManager();
        preferences=context.getSharedPreferences("chosen_apps", context.MODE_PRIVATE);
        editor = preferences.edit();
        final List<ApplicationInfo> appsList = context.getPackageManager().getInstalledApplications(0);
        for (int i=0;i<appsList.toArray().length;i++){
            if (!appsList.get(i).packageName.equals(PACKAGE_NAME)){
                if (!appsList.get(i).packageName.contains("launcher3")) {
                    if (!appsList.get(i).packageName.contains("launcher")) {
                        if (!appsList.get(i).packageName.contains("trebuchet")) {
                            if (null != packageManager.getLaunchIntentForPackage(appsList.get(i).packageName)) {
                                editor.putBoolean(appsList.get(i).packageName, false).apply();
                            }
                        }
                    }
                }
            }
        }
        Toast.makeText(context,context.getString(R.string.all_apps_unlocked),Toast.LENGTH_LONG).show();
    }
    public static void lockAllApps(Context context){
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        PackageManager packageManager = context.getPackageManager();
        preferences=context.getSharedPreferences("chosen_apps", context.MODE_PRIVATE);
        editor = preferences.edit();
        final List<ApplicationInfo> appsList = context.getPackageManager().getInstalledApplications(0);
        for (int i=0;i<appsList.toArray().length;i++){
            if (!appsList.get(i).packageName.equals(PACKAGE_NAME)){
                if (!appsList.get(i).packageName.contains("launcher3")) {
                    if (!appsList.get(i).packageName.contains("launcher")) {
                        if (!appsList.get(i).packageName.contains("trebuchet")) {
                            if (null != packageManager.getLaunchIntentForPackage(appsList.get(i).packageName)) {
                                editor.putBoolean(appsList.get(i).packageName, true).apply();
                            }
                        }
                    }
                }
            }
        }
        Toast.makeText(context,context.getString(R.string.all_apps_locked),Toast.LENGTH_LONG).show();
    }
}
