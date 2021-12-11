package com.mobilesecurity.mobileapp.CallBlocker.domain.model

import com.mobilesecurity.mobileapp.CallBlocker.data.entity.CallType

data class CallLogModel(
    val contactName: String,
    val contactNumber: String,
    val callType: CallType,
    val timeStampInMillis: String,
    val callDuration: String,
    val isNumberBlocked: Boolean
)