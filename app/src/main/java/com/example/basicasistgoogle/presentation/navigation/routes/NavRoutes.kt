package com.example.basicasistgoogle.presentation.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class NavRoutes : NavKey {
    @Serializable
    object Welcome
    @Serializable
    object Register
    @Serializable
    object Login : NavRoutes()
    @Serializable
    object Main: NavRoutes()
    @Serializable
    object  Ubication: NavRoutes()
    @Serializable
    object  Setting: NavRoutes()

}

