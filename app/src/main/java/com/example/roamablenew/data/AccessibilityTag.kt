package com.example.roamablenew.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessibilityTag(
    val id: String? = null, // omit when inserting, DB generates it
    @SerialName("location_id") val locationId: String,
    @SerialName("tag_type") val tagType: String,
    @SerialName("user_id") val userId: String? = null
)