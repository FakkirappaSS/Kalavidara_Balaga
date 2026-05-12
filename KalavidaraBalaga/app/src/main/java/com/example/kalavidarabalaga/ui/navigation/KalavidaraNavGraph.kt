package com.example.kalavidarabalaga.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kalavidarabalaga.domain.model.Role
import com.example.kalavidarabalaga.ui.auth.AuthViewModel
import com.example.kalavidarabalaga.ui.auth.AuthState

@Composable
fun KalavidaraNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> {
                when (state.user.role) {
                    Role.ARTIST -> navController.navigate(Screen.ArtistDashboard.route) { popUpTo(0) }
                    Role.CLIENT -> navController.navigate(Screen.ClientSearch.route) { popUpTo(0) }
                    Role.ADMIN -> navController.navigate(Screen.AdminPanel.route) { popUpTo(0) }
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(Screen.Auth.route) { popUpTo(0) }
            }
            else -> {}
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            com.example.kalavidarabalaga.ui.auth.SplashScreen()
        }
        composable(Screen.Auth.route) {
            com.example.kalavidarabalaga.ui.auth.AuthScreen(viewModel = authViewModel)
        }
        composable(Screen.ArtistDashboard.route) {
            com.example.kalavidarabalaga.ui.artist.ArtistDashboardScreen(onLogout = { authViewModel.logout() })
        }
        composable(Screen.ClientSearch.route) {
            val clientHomeViewModel: com.example.kalavidarabalaga.ui.client.ClientHomeViewModel = hiltViewModel()
            com.example.kalavidarabalaga.ui.client.ClientHomeScreen(viewModel = clientHomeViewModel, onLogout = { authViewModel.logout() })
        }
        composable(Screen.AdminPanel.route) {
            val adminViewModel: com.example.kalavidarabalaga.ui.admin.AdminViewModel = hiltViewModel()
            com.example.kalavidarabalaga.ui.admin.AdminPanelScreen(viewModel = adminViewModel, onLogout = { authViewModel.logout() })
        }
    }
}
