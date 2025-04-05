package com.demo.vchat.nav_graph

sealed class VChatNavigationItem(val route: String) {
    object SplashScreen : VChatNavigationItem("splash")
    object LandingScreen : VChatNavigationItem("land")
    object LoginScreen : VChatNavigationItem("login")
    object SignupScreen : VChatNavigationItem("signup")
    object ForgotPasswordScreen : VChatNavigationItem("forgotPassword")
    object ChatScreen : VChatNavigationItem("chats")
    object RequestScreen : VChatNavigationItem("requests")

    // 🟢 Bottom Navigation Items
    object ConnectedAllChats : VChatNavigationItem("allChats")
    object ConnectPeople : VChatNavigationItem("connectPeople")
    object ProfileScreen : VChatNavigationItem("profileScreen")

}