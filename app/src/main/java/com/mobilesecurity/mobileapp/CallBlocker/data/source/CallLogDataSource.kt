package com.mobilesecurity.mobileapp.CallBlocker.data.source

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import com.mobilesecurity.mobileapp.CallBlocker.data.entity.CallLogEntity
import com.mobilesecurity.mobileapp.CallBlocker.data.entity.CallType
import com.mobilesecurity.commons.SystemPermissionUtil
import dagger.hilt.android.qualifiers.ApplicationContext

interface CallLogDataSource {
    fun getCallLog(): List<CallLogEntity>
}

class CallLogDataSourceImpl(
    @ApplicationContext private val context: Context,
    private val systemPermissionUtil: SystemPermissionUtil
) : CallLogDataSource {
    override fun getCallLog(): List<CallLogEntity> {
        if (systemPermissionUtil.checkPermission(context, Manifest.permission.READ_CALL_LOG)) {
            val c = context.applicationContext
            val projection = arrayOf(
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )

            val cursor = c.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                null,
                null
            )

            return cursorToMatrix(cursor)
        } else {
            return listOf()
        }
    }

    private fun cursorToMatrix(cursor: Cursor?): List<CallLogEntity> {
        val callLogList = mutableListOf<CallLogEntity>()
        cursor?.use {
            while (it.moveToNext()) {
                callLogList.add(
                    CallLogEntity(
                        it.getStringFromColumn(CallLog.Calls.CACHED_NAME),
                        it.getStringFromColumn(CallLog.Calls.NUMBER),
                        CallType.values()[it.getStringFromColumn(CallLog.Calls.TYPE).toInt() - 1],
                        it.getStringFromColumn(CallLog.Calls.DATE),
                        it.getStringFromColumn(CallLog.Calls.DURATION)
                    )
                )
            }
        }
        return callLogList
    }

    private fun Cursor.getStringFromColumn(columnName: String) =
        getString(getColumnIndex(columnName)) ?: ""
}