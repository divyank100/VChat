package com.example.vchat.nav_graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vchat.screens.landing.LandingScreen
import com.example.vchat.screens.login.LoginScreen
import com.example.vchat.screens.signup.SignupScreen

@Composable
fun VChatNavigation(navHostController: NavHostController) {

    NavHost(navHostController, startDestination = VChatNavigationItem.LandingScreen.route) {
        composable(VChatNavigationItem.LandingScreen.route) {
            LandingScreen(navHostController)
        }
        composable(VChatNavigationItem.LoginScreen.route) {
            LoginScreen(navHostController)
        }
        composable(VChatNavigationItem.SignupScreen.route) {
            SignupScreen(navHostController)
        }
    }

}