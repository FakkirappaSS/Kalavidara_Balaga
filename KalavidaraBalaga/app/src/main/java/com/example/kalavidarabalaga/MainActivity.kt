package com.example.kalavidarabalaga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.kalavidarabalaga.ui.navigation.KalavidaraNavGraph
import com.example.kalavidarabalaga.ui.theme.KalavidaraBalagaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalavidaraBalagaTheme {
                val navController = rememberNavController()
                KalavidaraNavGraph(navController = navController)
            }
        }
    }
}