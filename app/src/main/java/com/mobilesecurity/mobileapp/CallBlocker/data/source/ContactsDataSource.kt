package com.mobilesecurity.mobileapp.CallBlocker.data.source

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.mobilesecurity.mobileapp.CallBlocker.data.entity.DeviceContactsEntity
import com.mobilesecurity.commons.SystemPermissionUtil
import me.xdrop.fuzzywuzzy.FuzzySearch

interface ContactsDataSource {
    suspend fun getAllContactsFromDevice(): List<DeviceContactsEntity>

    suspend fun searchContact(
        charSequence: CharSequence,
        allContactList: List<DeviceContactsEntity>
    ): List<DeviceContactsEntity>
}

class ContactsDataSourceImpl(
    private val context: Context,
    private val permissionUtil: SystemPermissionUtil
) : ContactsDataSource {

    override suspend fun getAllContactsFromDevice(): List<DeviceContactsEntity> {
        if (permissionUtil.checkPermissions(
                context, getListOfRequiredPermissions()
            ).let {
                permissionUtil.getMissingPermissionsArray(it)
            }.isEmpty()
        ) {
            val contactsList = mutableListOf<DeviceContactsEntity>()
            val cr: ContentResolver = context.contentResolver
            val cur: Cursor? = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
            )
            if ((cur?.count ?: 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                    )
                    val name: String? = cur.getString(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                        )
                    )
                    if (cur.getInt(
                            cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                            )
                        ) > 0
                    ) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        while (pCur != null && pCur.moveToNext()) {
                            val phoneNo: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            contactsList.add(
                                DeviceContactsEntity(
                                    name,
                                    phoneNo
                                )
                            )
                        }
                        pCur?.close()
                    }
                }
            }
            cur?.close()
            println("contact list is $contactsList")
            return contactsList
        } else {
            return listOf()
        }
    }

    override suspend fun searchContact(
        charSequence: CharSequence,
        allContactList: List<DeviceContactsEntity>
    ): List<DeviceContactsEntity> {
        return FuzzySearch.extractAll(charSequence.toString(), allContactList) { item ->
            item?.name ?: "" + item?.phoneNumber ?: ""
        }.filter {
            it.score >= 80
        }.map {
            it.referent
        }
    }

    private fun getListOfRequiredPermissions(): List<String> {
        return mutableListOf(Manifest.permission.READ_CONTACTS)
    }

}