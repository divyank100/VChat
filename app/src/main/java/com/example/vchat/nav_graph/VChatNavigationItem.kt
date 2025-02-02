package com.example.vchat.nav_graph

sealed class VChatNavigationItem(val route: String) {
    object SplashScreen : VChatNavigationItem("splash")
    object LandingScreen : VChatNavigationItem("land")
    object LoginScreen : VChatNavigationItem("login")
    object SignupScreen : VChatNavigationItem("signup")
    object ForgotPasswordScreen : VChatNavigationItem("forgotPassword")

    // 🟢 Bottom Navigation Items
    object AllChats : VChatNavigationItem("allChats")
    object AddPeople : VChatNavigationItem("addPeople")
    object More : VChatNavigationItem("more")

}