package com.example.vchat.nav_graph

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vchat.screens.SplashScreen
import com.example.vchat.screens.all_chats.AllChats
import com.example.vchat.screens.forgot_password.ForgotPasswordScreen
import com.example.vchat.screens.landing.LandingScreen
import com.example.vchat.screens.login.LoginScreen
import com.example.vchat.screens.signup.SignupScreen

@Composable
fun VChatNavigation(navHostController: NavHostController) {
    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav(navHostController)) {
                BottomNavBar(navHostController)
            }
        }
    ) { innerPadding ->

        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navHostController,
            startDestination = VChatNavigationItem.LandingScreen.route
        ) {
            composable(VChatNavigationItem.SplashScreen.route) {
                SplashScreen(navHostController)
            }
            composable(VChatNavigationItem.LandingScreen.route) {
                LandingScreen(navHostController)
            }
            composable(VChatNavigationItem.LoginScreen.route) {
                LoginScreen(navHostController)
            }
            composable(VChatNavigationItem.SignupScreen.route) {
                SignupScreen(navHostController)
            }
            composable(VChatNavigationItem.ForgotPasswordScreen.route) {
                ForgotPasswordScreen(navHostController)
            }
            composable(VChatNavigationItem.AllChats.route) {
                AllChats(navHostController)
            }
            composable(VChatNavigationItem.More.route) {
                AllChats(navHostController)
            }
            composable(VChatNavigationItem.AddPeople.route) {
                AllChats(navHostController)
            }
        }
    }

}

@Composable
fun shouldShowBottomNav(navController: NavHostController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return currentRoute in listOf(
        VChatNavigationItem.AllChats.route,
        VChatNavigationItem.AddPeople.route,
        VChatNavigationItem.More.route
    )
}