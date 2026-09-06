package com.example.roamablenew.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.roamablenew.sos.navigation.SOSNavigationGraph

@Composable
fun SosScreen() {
    SOSNavigationGraph(LocalContext.current)
}