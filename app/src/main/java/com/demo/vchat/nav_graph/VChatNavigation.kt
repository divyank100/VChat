package com.demo.vchat.nav_graph

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
import com.demo.vchat.di.PrefHelperEntryPoint
import com.demo.vchat.screens.SplashScreen
import com.demo.vchat.screens.connected_all_chats.AllChats
import com.demo.vchat.screens.connect_people.ConnectPeople
import com.demo.vchat.screens.forgot_password.ForgotPasswordScreen
import com.demo.vchat.screens.landing.LandingScreen
import com.demo.vchat.screens.login.LoginScreen
import com.demo.vchat.screens.profile.ProfileScreen
import com.demo.vchat.screens.requests.RequestScreen
import com.demo.vchat.screens.signup.SignupScreen
import com.demo.vchat.util.AppConstants
import dagger.hilt.android.EntryPointAccessors

@Composable
fun VChatNavigation(navHostController: NavHostController,isDarkTheme:Boolean=false) {
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
            composable(VChatNavigationItem.ChatScreen.route + "/{userId}"+"/{userName}"+"/{userStatus}",
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
                    navArgument(
                        name = "userStatus",
                        builder = {
                            type = NavType.BoolType
                        }
                    ),
                )
            ) {
                val userId=it.arguments?.getString("userId")
                val userName=it.arguments?.getString("userName")
                val userStatus=it.arguments?.getBoolean("userStatus")
                ChatScreen(navHostController,userId,userName,userStatus ?: false)
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