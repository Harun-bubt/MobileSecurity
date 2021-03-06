package com.mobilesecurity.mobileapp.CallBlocker.data.mapper

import com.mobilesecurity.mobileapp.CallBlocker.data.entity.ContactEntity
import com.mobilesecurity.mobileapp.CallBlocker.data.entity.DeviceContactsEntity
import com.mobilesecurity.commons.util.removeAllWhiteSpaces
import com.mobilesecurity.datasource.local.entity.ContactDbEntity

internal object ContactEntityMapper {

    internal fun mapToContactEntityFromDbContactEntity(dbDbEntity: ContactDbEntity): ContactEntity =
        dbDbEntity.let {
            ContactEntity(it.name, it.phoneNumber)
        }

    internal fun mapToDbContactEntityFromContactEntity(contactEntity: ContactEntity): ContactDbEntity =
        contactEntity.let {
            ContactDbEntity(it.name, it.number)
        }

    internal fun mapToDeviceContactEntityFromContactEntity(contactEntity: ContactEntity): DeviceContactsEntity =
        contactEntity.let {
            DeviceContactsEntity(
                it.name,
                it.number
            )
        }

    internal fun mapToContactEntityFromDeviceContactEntity(deviceContactsEntity: DeviceContactsEntity): ContactEntity =
        deviceContactsEntity.let {
            ContactEntity(it.name, it.phoneNumber.removeAllWhiteSpaces())
        }
}