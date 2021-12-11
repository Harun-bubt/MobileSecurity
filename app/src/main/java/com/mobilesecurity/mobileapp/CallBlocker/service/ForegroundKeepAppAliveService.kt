package com.mobilesecurity.mobileapp.CallBlocker.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mobilesecurity.mobileapp.CallBlocker.NotificationProvider
import com.mobilesecurity.mobileapp.CallBlocker.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundKeepAppAliveService : Service() {


    @Inject
    internal lateinit var notificationProvider: NotificationProvider

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not supported");
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationProvider.showKeepAppAliveForegroundNotification(
            this,
            this,
            Intent(this, MainActivity::class.java)
        )
        return super.onStartCommand(intent, flags, startId)
    }


}