package com.example.roamablenew.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String? = "",
    val email: String? = "",
    val password: String? = "",
    val disability: String? = "",
    val address: String? = ""
)
