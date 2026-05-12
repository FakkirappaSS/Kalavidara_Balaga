package com.example.kalavidarabalaga.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object ArtistDashboard : Screen("artist_dashboard")
    object ClientSearch : Screen("client_search")
    object AdminPanel : Screen("admin_panel")
}
