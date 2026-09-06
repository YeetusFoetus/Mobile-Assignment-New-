package com.example.roamablenew.data

enum class ContactCategory(val displayName: String) {
    POLICE_STATION("Police Stations"),
    NGO("NGOs"),
    SOCIETY("Societies"),
    HOSPITAL("Hospitals"),
    SOCIAL_WELFARE("Social Welfare Organisations"),
    AMBULANCE("Ambulances")
}

data class Contact(
    val id: String = "",
    val name: String,
    val category: ContactCategory,
    val phoneNumber: String,
    val address: String = "",
    val notes: String = "",
    val isActive: Boolean = true
)