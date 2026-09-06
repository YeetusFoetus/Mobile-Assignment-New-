package com.example.roamablenew.sos.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.roamablenew.sos.EmergencyContactForm
import com.example.roamablenew.sos.EmergencyContactsScreen
import com.example.roamablenew.sos.SMSScreen
import com.example.roamablenew.viewmodel.UserViewModel

@Composable
fun SOSNavigationGraph(context : Context, loginSession : UserViewModel) {
    // Initialises necessary variables
    var userEmail by remember { mutableStateOf(loginSession.getEmail()) }
    var navController = rememberNavController()

    // Defines the navigation graph for the app
    NavHost(
        navController = navController,
        startDestination = "SMSActivity"
    ) {
        // Defines the routes of the app
        composable("SMSActivity") {
            // Calls the composable 'BeginScreen' function
            SMSScreen(navController = navController, userEmail = userEmail,
                context = context)
        }
        composable("EmergencyContactsActivity") {
            EmergencyContactsScreen(
                navController = navController,
                userEmail = userEmail
            )
        }
        composable(
            route = "EmergencyContactForm/{id}/{contactName}/{telephoneNum}",
            arguments = listOf(
                navArgument("id") {type = NavType.IntType},
                navArgument("contactName") {type = NavType.StringType},
                navArgument("telephoneNum") {type = NavType.StringType},
            )
        ) {
            backStackEntry ->
            val args = backStackEntry.arguments
            EmergencyContactForm(
                navController = navController,
                id = args?.getInt("id") ?: 0,
                contactName = args?.getString("contactName") ?: "",
                telephoneNum = args?.getString("telephoneNum") ?: "",
                userEmail = userEmail
            )
        }
    }
}