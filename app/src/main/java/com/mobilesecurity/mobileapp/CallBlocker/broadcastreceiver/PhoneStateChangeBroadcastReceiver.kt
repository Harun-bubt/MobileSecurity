package com.mobilesecurity.mobileapp.CallBlocker.broadcastreceiver

import android.content.Context
import android.content.Intent
import com.mobilesecurity.mobileapp.CallBlocker.PhoneReceiver
import com.mobilesecurity.mobileapp.CallBlocker.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhoneStateChangeBroadcastReceiver : HiltBroadcastReceiver() {

    @Inject
    internal lateinit var phoneReceiver: PhoneReceiver

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        phoneReceiver.onPhoneStateChange(context, intent, Intent(context, MainActivity::class.java))
    }
}