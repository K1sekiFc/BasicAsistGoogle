package com.example.basicasistgoogle.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basicasistgoogle.presentation.navigation.container.MainContainer
import com.example.basicasistgoogle.presentation.screen.mainContainer.profile.ProfileScreen
import com.example.basicasistgoogle.presentation.navigation.routes.NavRoutes
import com.example.basicasistgoogle.presentation.screen.login.LoginScreen
import com.example.basicasistgoogle.presentation.screen.welcome.WelcomeScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoutes.Welcome) {
        composable<NavRoutes.Welcome> {
            WelcomeScreen(
                navigateToLogin = {
                    navController.navigate(NavRoutes.Login)
                    { launchSingleTop = true }
                }
            )
        }
        composable<NavRoutes.Login> {
            LoginScreen(
                navigateToRegister = { navController.navigate(NavRoutes.Register) },
                navigateBack = { navController.popBackStack() },
                navigateToMain = {
                    navController.navigate(NavRoutes.Main) {
                        popUpTo(NavRoutes.Welcome) {
                            inclusive = true
                        }
                    }
                }
            )
        }
//        composable<NavRoutes.Register> {
//            RegisterScreen(
//                navigateBack = { navController.popBackStack() },
//
//                )
//        }
//        composable<NavRoutes.Ubication> {
//            UbicationScreen()
//        }
        composable<NavRoutes.Main> {
            MainContainer()
        }
//        composable<NavRoutes.Profile> {
//            ProfileScreen(
//                navigateBack = { navController.popBackStack() }
//            )
//        }

    }
}