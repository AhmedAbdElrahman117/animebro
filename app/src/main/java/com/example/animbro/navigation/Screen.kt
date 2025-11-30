package com.example.animbro.navigation

sealed class Screen(val route: String) {
    // Auth Screens
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")

    // Main App Screens
    object Home : Screen("home")
    object Search : Screen("search")
    object AnimeList : Screen("anime_list")

    // Detail Screen with parameter
    object AnimeDetails : Screen("anime_details/{animeId}") {
        fun createRoute(animeId: Int) = "anime_details/$animeId"
    }
}
