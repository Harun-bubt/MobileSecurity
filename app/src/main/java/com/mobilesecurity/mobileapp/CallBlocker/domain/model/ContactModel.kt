package com.mobilesecurity.mobileapp.CallBlocker.domain.model


data class ContactModel(
    val name: String? = null,
    val phoneNumber: String,
    val contactModelType: ContactModelType,
    val isContactBlocked: Boolean = false
) : Comparable<ContactModel> {
    override fun compareTo(other: ContactModel): Int {
        return if (name == other.name && phoneNumber == other.phoneNumber && contactModelType == other.contactModelType) {
            0
        } else {
            1
        }
    }
}

enum class ContactModelType {
    BLOCKED_CONTACT,
    ALL_CONTACT
}