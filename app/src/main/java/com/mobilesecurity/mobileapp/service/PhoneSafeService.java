package com.mobilesecurity.mobileapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.mobilesecurity.mobileapp.Constant;
import com.mobilesecurity.mobileapp.receiver.SmsReceiver;

/**
 * listener sms
 */
public class PhoneSafeService extends Service {
    private SmsReceiver receiver;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // register sms receiver
        receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(Integer.MAX_VALUE);
        filter.addAction(Constant.ACTION_SMS_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister sms receiver
        unregisterReceiver(receiver);
    }
}
