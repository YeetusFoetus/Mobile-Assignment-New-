package com.example.roamablenew.sos.model.userInputs

import kotlinx.serialization.Serializable

@Serializable
data class EmergencyContact(
    val contactName : String,
    val telephoneNum : String,
    val userEmail : String
)