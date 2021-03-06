package com.mobilesecurity.mobileapp.CallBlocker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.mobilesecurity.commons.util.stringRes
import com.mobilesecurity.datasource.local.LocalDataSource
import com.mobilesecurity.mobileapp.R
import com.android.internal.telephony.ITelephony
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface PhoneReceiver {
    fun onPhoneStateChange(
        context: Context,
        intent: Intent,
        onNotificationClickIntent: Intent
    )
}

const val ITELEPHONY_METHOD = "getITelephony"

class PhoneReceiverImpl(
    private val localDataSource: LocalDataSource,
    private val notificationProvider: NotificationProvider
) : PhoneReceiver {

    override fun onPhoneStateChange(
        context: Context,
        intent: Intent,
        onNotificationClickIntent: Intent
    ) {
        GlobalScope.launch {
            if (TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action && intent.getStringExtra(
                    TelephonyManager.EXTRA_STATE
                ) == TelephonyManager.EXTRA_STATE_RINGING && shouldRejectPhoneCall(intent)
            ) {
                rejectCall(context, intent, onNotificationClickIntent)
            }
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_OFFHOOK
            ) {
                ALREADY_ON_CALL = true
            } else if (intent.getStringExtra(
                    TelephonyManager.EXTRA_STATE
                ) == TelephonyManager.EXTRA_STATE_IDLE
            ) {
                ALREADY_ON_CALL = false
            }
        }
    }

    private suspend fun shouldRejectPhoneCall(intent: Intent): Boolean {
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            ?: return false
        if (incomingNumber.isNotBlank()) {
            val contactData = localDataSource.getBlockedContactByNumber(incomingNumber)
            return contactData != null
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private suspend fun rejectCall(
        @NonNull context: Context,
        intent: Intent,
        onNotificationClickIntent: Intent
    ) {
        if (!ALREADY_ON_CALL) {
            val endedCallSuccessFully =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    endCallOnApi28AndAbove(context)
                } else {
                    endCallOnApiPre28(context)
                }
            if (!endedCallSuccessFully) {
                // Handle accordingly
            }
        }
        val notificationBodyIdentifier: String? =
            localDataSource.getBlockedContactByNumber(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))?.name
                ?: intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        notificationProvider.showCallBlockNotification(
            context,
            onNotificationClickIntent,
            notificationBodyIdentifier ?: context.stringRes(R.string.unknown)
        )
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun endCallOnApi28AndAbove(context: Context): Boolean {
        val telecomManager =
            context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        return try {
            telecomManager.endCall()
            true
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private fun endCallOnApiPre28(context: Context): Boolean {
        val tm =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            val m =
                tm.javaClass.getDeclaredMethod(ITELEPHONY_METHOD)
            m.isAccessible = true
            val telephony: ITelephony = m.invoke(tm) as ITelephony
            telephony.endCall()
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private var ALREADY_ON_CALL = false
    }

}