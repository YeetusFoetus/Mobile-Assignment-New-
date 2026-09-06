package com.example.roamablenew.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.roamablenew.sos.navigation.SOSNavigationGraph
import com.example.roamablenew.viewmodel.UserViewModel

@Composable
fun SosScreen(userViewModel: UserViewModel) {
    SOSNavigationGraph(LocalContext.current, userViewModel)
}