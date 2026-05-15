package com.grama.wastetracker.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.grama.wastetracker.data.model.User
import com.grama.wastetracker.ui.screens.*
import com.grama.wastetracker.ui.viewmodel.*

@Composable
fun GramaNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val authState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { user ->
                    val dest = when (user?.role) {
                        User.ROLE_ADMIN -> Screen.AdminDashboard.route
                        User.ROLE_TRACTOR -> Screen.TractorDashboard.route
                        else -> Screen.Home.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // Citizen Screens
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                homeViewModel = homeViewModel,
                authViewModel = authViewModel,
                onNavigateToBlackspot = { navController.navigate(Screen.ReportBlackspot.route) },
                onNavigateToWasteGuide = { navController.navigate(Screen.WasteGuide.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToBlackspotList = { navController.navigate(Screen.BlackspotList.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ReportBlackspot.route) {
            val blackspotViewModel: BlackspotViewModel = hiltViewModel()
            ReportBlackspotScreen(
                viewModel = blackspotViewModel,
                userId = authState.user?.uid ?: "",
                userName = authState.user?.fullName ?: "",
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(Screen.BlackspotList.route) {
            val blackspotViewModel: BlackspotViewModel = hiltViewModel()
            BlackspotListScreen(
                viewModel = blackspotViewModel,
                userId = authState.user?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WasteGuide.route) {
            val wasteGuideViewModel: WasteGuideViewModel = hiltViewModel()
            WasteGuideScreen(
                viewModel = wasteGuideViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                userId = authState.user?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                user = authState.user,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Admin Screens
        composable(Screen.AdminDashboard.route) {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminDashboardScreen(
                viewModel = adminViewModel,
                authViewModel = authViewModel,
                onNavigateToReports = { navController.navigate(Screen.AdminReports.route) },
                onNavigateToTractor = { navController.navigate(Screen.AdminTractor.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AdminReports.route) {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminReportsScreen(
                viewModel = adminViewModel,
                adminId = authState.user?.uid ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminTractor.route) {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminTractorScreen(
                viewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TractorDashboard.route) {
            val adminViewModel: AdminViewModel = hiltViewModel()
            TractorDashboardScreen(
                adminViewModel = adminViewModel,
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
