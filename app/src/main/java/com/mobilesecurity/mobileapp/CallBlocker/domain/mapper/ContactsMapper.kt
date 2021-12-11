package com.mobilesecurity.mobileapp.CallBlocker.domain.mapper

import com.mobilesecurity.mobileapp.CallBlocker.data.entity.ContactEntity
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModel
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModelType


internal object ContactsModelMapper {
    internal fun mapToContactsModelFromContactEntity(
        contactDbEntity: ContactEntity,
        contactModelType: ContactModelType
    ): ContactModel =
        contactDbEntity.let {
            ContactModel(it.name, it.number, contactModelType)
        }

    internal fun mapToContactEntityFromContactsModel(contactModel: ContactModel): ContactEntity =
        contactModel.let {
            ContactEntity(it.name, it.phoneNumber)
        }
}