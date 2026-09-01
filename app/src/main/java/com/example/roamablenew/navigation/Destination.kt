package com.example.roamablenew.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    MAP(
        route = "map",
        label = "Map",
        icon = Icons.Default.Place,
        contentDescription = "Map Screen"
    ),
    HELPLINE(
        route = "helpline",
        label = "Helpline",
        icon = Icons.Default.Phone,
        contentDescription = "Helpline Screen"
    ),
    SOS(
        route = "sos",
        label = "SOS",
        icon = Icons.Default.Warning,
        contentDescription = "Emergency SOS Screen"
    ),
    PROFILE(
        route = "profile",
        label = "Profile",
        icon = Icons.Default.Person,
        contentDescription = "Profile Screen"
    ),
    LOGIN(
        route = "login",
        label = "Login",
        icon = Icons.Default.Person,
        contentDescription = "Login Screen"
    ),
    REGISTER(
        route = "register",
        label = "Register",
        icon = Icons.Default.Person,
        contentDescription = "Register Screen"
    )
}