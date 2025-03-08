package com.example.vchat.nav_graph

import ChatScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.screens.SplashScreen
import com.example.vchat.screens.connected_all_chats.AllChats
import com.example.vchat.screens.connect_people.ConnectPeople
import com.example.vchat.screens.forgot_password.ForgotPasswordScreen
import com.example.vchat.screens.landing.LandingScreen
import com.example.vchat.screens.login.LoginScreen
import com.example.vchat.screens.profile.ProfileScreen
import com.example.vchat.screens.requests.RequestScreen
import com.example.vchat.screens.signup.SignupScreen
import com.example.vchat.util.AppConstants
import dagger.hilt.android.EntryPointAccessors

@Composable
fun VChatNavigation(navHostController: NavHostController) {
    val context = LocalContext.current
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()
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
            startDestination = if (prefHelper.getString(AppConstants.refreshToken)
                    ?.isNotEmpty() == true
            ) VChatNavigationItem.ConnectedAllChats.route else VChatNavigationItem.LandingScreen.route
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
            composable(VChatNavigationItem.ConnectedAllChats.route) {
                AllChats(navHostController)
            }
            composable(VChatNavigationItem.ProfileScreen.route) {
                ProfileScreen(navHostController)
            }
            composable(VChatNavigationItem.ConnectPeople.route) {
                ConnectPeople(navHostController)
            }
            composable(VChatNavigationItem.ChatScreen.route + "/{userId}"+"/{userName}",
                arguments = listOf(
                    navArgument(
                        name = "userId",
                        builder = {
                            type = NavType.StringType
                        }
                    ),
                    navArgument(
                        name = "userName",
                        builder = {
                            type = NavType.StringType
                        }
                    ),
                )
            ) {
                val userId=it.arguments?.getString("userId")
                val userName=it.arguments?.getString("userName")
                ChatScreen(navHostController,userId,userName)
            }
            composable(VChatNavigationItem.RequestScreen.route) {
                RequestScreen(navHostController)
            }
        }
    }

}

@Composable
fun shouldShowBottomNav(navController: NavHostController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return currentRoute in listOf(
        VChatNavigationItem.ConnectedAllChats.route,
        VChatNavigationItem.ConnectPeople.route,
        VChatNavigationItem.ProfileScreen.route
    )
}