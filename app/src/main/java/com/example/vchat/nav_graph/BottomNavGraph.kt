package com.example.vchat.nav_graph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vchat.R


@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            "Add People",
            ImageVector.vectorResource(R.drawable.ic_people),
            VChatNavigationItem.AddPeople.route
        ),
        BottomNavItem("Chats", Icons.Default.MailOutline, VChatNavigationItem.AllChats.route),
        BottomNavItem("More", ImageVector.vectorResource(R.drawable.ic_more_horizontal), VChatNavigationItem.More.route)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray,
        tonalElevation = 10.dp,

        ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    if (item.route != VChatNavigationItem.AllChats.route) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    if (item.route == VChatNavigationItem.AllChats.route) {
                        Text(
                            color = Color.Black,
                            text = item.title,
                            fontSize = 14.sp,
//                            fontFamily = FontFamily(Font(R.font.inter))
                        )
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)