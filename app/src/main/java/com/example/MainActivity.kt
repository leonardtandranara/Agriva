package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.repository.AgrivaRepository
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AgrivaViewModel
import com.example.ui.viewmodel.AgrivaViewModelFactory
import com.example.ui.viewmodel.AppScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize DB and Repository layers
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AgrivaRepository(
            marketDao = database.marketDao(),
            walletDao = database.walletDao(),
            weatherDao = database.weatherDao()
        )

        // 2. Set up Factory injected ViewModel
        val viewModelFactory = AgrivaViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[AgrivaViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    // Crossfade animate the entry transitis between login and admin summary
                    Crossfade(
                        targetState = currentScreen,
                        label = "screenCrossfade"
                    ) { screen ->
                        when (screen) {
                            AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                            AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
