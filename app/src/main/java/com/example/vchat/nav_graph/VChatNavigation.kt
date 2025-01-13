package com.example.vchat.nav_graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vchat.screens.LoginScreen

@Composable
fun VChatNavigation(navHostController: NavHostController) {

    NavHost(navHostController, startDestination = VChatNavigationItem.LoginScreen.route) {
        composable(VChatNavigationItem.LoginScreen.route) {
            LoginScreen(navHostController)
        }
    }

}