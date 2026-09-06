package com.example.roamablenew.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactEntity(

    @SerialName("uuid")
    val id: String? = null,

    val name: String,

    val category: String,

    @SerialName("phone_number")
    val phoneNumber: String,

    val address: String = "",

    val notes: String = "",

    @SerialName("is_active")
    val isActive: Boolean = true
)

fun ContactEntity.toContact(): Contact {

    return Contact(
        id = id ?: "",
        name = name,
        category = ContactCategory.valueOf(category),
        phoneNumber = phoneNumber,
        address = address,
        notes = notes,
        isActive = isActive
    )
}

fun Contact.toEntity(): ContactEntity {

    return ContactEntity(
        id = id.ifBlank { null },
        name = name,
        category = category.name,
        phoneNumber = phoneNumber,
        address = address,
        notes = notes,
        isActive = isActive
    )
}