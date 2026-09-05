package com.example.roamablenew.sos.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.roamablenew.sos.EmergencyContactForm
import com.example.roamablenew.sos.EmergencyContactsScreen
import com.example.roamablenew.sos.SMSScreen

@Composable
fun AppNavigationGraph(context : Context) {
    // Initialises necessary variables
    var userEmail = "mmelvis627@outlook.com"
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
        composable(
            route = "EmergencyContactsActivity/{userEmail}",
            arguments = listOf(
                navArgument("userEmail") {type = NavType.StringType}
            )
        ) {
            backStackEntry ->
            val args = backStackEntry.arguments
            EmergencyContactsScreen(
                navController = navController,
                userEmail = args?.getString("userEmail") ?: ""
            )
        }
        composable(
            route = "EmergencyContactForm/{id}/{contactName}/{telephoneNum}/{userEmail}",
            arguments = listOf(
                navArgument("id") {type = NavType.IntType},
                navArgument("contactName") {type = NavType.StringType},
                navArgument("telephoneNum") {type = NavType.StringType},
                navArgument("userEmail") {type = NavType.StringType}
            )
        ) {
            backStackEntry ->
            val args = backStackEntry.arguments
            EmergencyContactForm(
                navController = navController,
                id = args?.getInt("id") ?: 0,
                contactName = args?.getString("contactName") ?: "",
                telephoneNum = args?.getString("telephoneNum") ?: "",
                userEmail = args?.getString("userEmail") ?: ""
            )
        }
    }
}