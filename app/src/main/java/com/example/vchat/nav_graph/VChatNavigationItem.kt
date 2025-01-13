package com.example.vchat.nav_graph

sealed class VChatNavigationItem(val route: String) {
    object SplashScreen : VChatNavigationItem("splash")
    object LoginScreen : VChatNavigationItem("login")
    object SignupScreen : VChatNavigationItem("signup")
}