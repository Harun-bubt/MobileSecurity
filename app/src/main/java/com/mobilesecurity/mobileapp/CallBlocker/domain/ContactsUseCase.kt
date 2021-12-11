package com.mobilesecurity.mobileapp.CallBlocker.domain

import com.mobilesecurity.mobileapp.CallBlocker.data.repository.ContactsRepository
import com.mobilesecurity.mobileapp.CallBlocker.domain.mapper.ContactsModelMapper
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.CallLogModel
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModel
import com.mobilesecurity.mobileapp.CallBlocker.domain.model.ContactModelType

interface ContactsUseCase {
    suspend fun saveBlockedContact(contactModel: ContactModel)
    suspend fun unBlockContact(contactModel: ContactModel)
    suspend fun getAllBlockedContacts(): List<ContactModel>
    suspend fun getAllSavedContacts(): List<ContactModel>
    suspend fun getCallLog(): List<CallLogModel>
}

class ContactsInteractor(private val contactsRepository: ContactsRepository) : ContactsUseCase {
    override suspend fun saveBlockedContact(contactModel: ContactModel) {
        contactsRepository.saveBlockedContact(
            ContactsModelMapper.mapToContactEntityFromContactsModel(contactModel)
        )
    }

    override suspend fun unBlockContact(contactModel: ContactModel) {
        contactsRepository.unBlockContact(
            ContactsModelMapper.mapToContactEntityFromContactsModel(contactModel)
        )
    }

    override suspend fun getAllBlockedContacts(): List<ContactModel> {
        return contactsRepository.getAllBlockedContacts()
            .map {
                ContactsModelMapper.mapToContactsModelFromContactEntity(
                    it,
                    ContactModelType.BLOCKED_CONTACT
                )
            }
    }

    override suspend fun getAllSavedContacts(): List<ContactModel> {
        val savedContacts: List<ContactModel> = contactsRepository.getAllContactsFromDevice()
            .map {
                ContactsModelMapper.mapToContactsModelFromContactEntity(
                    it,
                    ContactModelType.ALL_CONTACT
                )
            }
        val blockedContacts = getAllBlockedContacts()
        return savedContacts.map {
            var isBlocked = false
            for (blockedContact in blockedContacts) {
                if (it.phoneNumber == blockedContact.phoneNumber) {
                    isBlocked = true
                    break
                }
            }
            ContactModel(
                it.name,
                it.phoneNumber,
                it.contactModelType,
                isBlocked
            )
        }
    }

    override suspend fun getCallLog(): List<CallLogModel> {
        val blockedContacts = getAllBlockedContacts()
        return contactsRepository.getAllCallLogs().map {
            CallLogModel(
                it.contactName,
                it.contactNumber,
                it.callType,
                it.timeStampInMillis,
                it.callDuration,
                blockedContacts.find { contactModel ->
                    contactModel.phoneNumber == it.contactNumber
                } != null
            )
        }
    }


}