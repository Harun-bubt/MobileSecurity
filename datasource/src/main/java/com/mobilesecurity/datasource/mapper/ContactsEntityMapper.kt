package com.mobilesecurity.datasource.mapper

import com.mobilesecurity.datasource.local.blockedcontact.BlockedContactsDbEntity
import com.mobilesecurity.datasource.local.entity.ContactDbEntity

internal object ContactEntityMapper {

    internal fun mapToContactEntityFromBlockedContactsDbEntity(dbEntity: BlockedContactsDbEntity): ContactDbEntity =
        dbEntity.let {
            ContactDbEntity(
                it.name,
                it.phoneNumber
            )
        }

    internal fun mapToBlockedContactsDbEntityFromContactEntity(contactDbEntity: ContactDbEntity): BlockedContactsDbEntity =
        contactDbEntity.let {
            BlockedContactsDbEntity(
                name = it.name,
                phoneNumber = it.phoneNumber
            )
        }
}