package com.example.animbro.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.animbro.anime.screens.DetailScreen
import com.example.animbro.anime.screens.HomeScreen
import com.example.animbro.anime.screens.SearchScreen
import com.example.animbro.anime.screens.UserListScreen
import com.example.animbro.anime.services.AnimeListViewModel
import com.example.animbro.anime.services.DetailViewModel
import com.example.animbro.anime.services.HomeViewModel
import com.example.animbro.anime.services.SearchViewModel
import com.example.animbro.auth.screens.ForgotPasswordScreen
import com.example.animbro.auth.screens.LoginScreen
import com.example.animbro.auth.screens.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Check if user is logged in
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = if (currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(500)) },
        exitTransition = { fadeOut(animationSpec = tween(500)) },
        popEnterTransition = { fadeIn(animationSpec = tween(500)) },
        popExitTransition = { fadeOut(animationSpec = tween(500)) }
    ) {
        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Screens with Bottom Navigation
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        currentRoute = Screen.Home.route
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    HomeScreen(
                        viewModel = viewModel,
                        screenPadding = 20.dp,
                        onAnimeClick = { animeId ->
                            navController.navigate(Screen.AnimeDetails.createRoute(animeId))
                        },
                        onMoreClick = { section ->
                            // TODO: Navigate to section list screen
                        },
                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        }
                    )
                }
            }
        }

        composable(Screen.Search.route) {
            val viewModel: SearchViewModel = hiltViewModel()

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        currentRoute = Screen.Search.route
                    )
                }
            ) { paddingValues ->
                SearchScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues),
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.AnimeList.route) {
            val viewModel: AnimeListViewModel = hiltViewModel()

            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        currentRoute = Screen.AnimeList.route
                    )
                }
            ) { paddingValues ->
                UserListScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues),
                    onAnimeClick = { animeId ->
                        navController.navigate(Screen.AnimeDetails.createRoute(animeId))
                    }
                )
            }
        }

        composable(
            route = Screen.AnimeDetails.route,
            arguments = listOf(
                navArgument("animeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getInt("animeId") ?: -1
            val viewModel: DetailViewModel = hiltViewModel()

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                DetailScreen(
                    viewModel = viewModel,
                    onAnimeClick = { newAnimeId ->
                        navController.navigate(Screen.AnimeDetails.createRoute(newAnimeId))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
