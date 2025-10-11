package com.example.network_call

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pocket_library.theme.NetworkCallTheme
import com.example.pocket_library.LibrarySearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetworkCallTheme {
                LibrarySearchScreen()
            }
        }
    }
}
