package com.example.roamablenew.sos.model

import kotlinx.serialization.Serializable

// Creates a data model for the 'instruments' table
@Serializable
data class EmergencyContact(
    val id: Int,
    val contactName: String,
    val telephoneNum: String,
    val userEmail : String
)
