package com.grama.wastetracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.grama.wastetracker.data.model.User
import com.grama.wastetracker.ui.navigation.GramaNavGraph
import com.grama.wastetracker.ui.navigation.Screen
import com.grama.wastetracker.ui.theme.GramaWasteTrackerTheme
import com.grama.wastetracker.ui.viewmodel.AuthViewModel
import com.grama.wastetracker.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            GramaWasteTrackerTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val authState by authViewModel.uiState.collectAsState()
                    val navController = rememberNavController()

                    val startDestination = when {
                        !authState.isLoggedIn -> Screen.Login.route
                        authState.user?.role == User.ROLE_ADMIN -> Screen.AdminDashboard.route
                        else -> Screen.Home.route
                    }

                    GramaNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
