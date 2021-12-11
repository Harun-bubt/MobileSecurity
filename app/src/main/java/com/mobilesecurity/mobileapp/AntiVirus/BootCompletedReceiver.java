package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {

        if(!StaticTools.isServiceRunning(context,MonitorShieldService.class))
        {
            Intent myIntent = new Intent(context, MonitorShieldService.class);
            context.startService(myIntent);
        }
        else
        {
             }
    }
}