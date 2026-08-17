package com.example.roamablenew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.roamablenew.ui.theme.RomablenewTheme
import com.example.roamablenew.ui.screens.MainMenu

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RomablenewTheme {
                MainMenu()
            }
        }
    }
}