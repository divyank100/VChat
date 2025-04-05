package com.demo.vchat.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.demo.vchat.R
import com.demo.vchat.di.PrefHelperEntryPoint
import com.demo.vchat.models.login.User
import com.demo.vchat.nav_graph.VChatNavigationItem
import com.demo.vchat.util.AppConstants
import com.demo.vchat.util.SocketHandler
import com.google.gson.Gson
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()

    val gson = Gson()
    val json: String? = prefHelper.getString(AppConstants.userData)
    val user: User? = gson.fromJson(json, User::class.java)
    val isDarkThemePresent=prefHelper.getBoolean(AppConstants.userTheme)

    println("isDark Theme $isDarkThemePresent")

    var darkTheme by remember { mutableStateOf(isDarkThemePresent) }

    Scaffold { innerPadding ->
        val colorScheme = MaterialTheme.colorScheme

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp, 25.dp, 15.dp, 15.dp),
                    text = "Settings",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorScheme.surface),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = user?.userProfile,
                            placeholder = painterResource(R.drawable.img_profile),
                            error = painterResource(R.drawable.img_profile)
                        ),
                        contentDescription = "Profile img",
                        modifier = Modifier
                            .padding(10.dp, 10.dp, 0.dp, 10.dp)
                            .size(85.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterVertically)
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = user?.userName ?: "Divyank Singh",
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user?.email ?: "divyanksingh000@gmail.com",
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorScheme.surface)
                        .padding(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(10.dp),
                        text = "Dark Theme",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                    )
                    Switch(
                        onCheckedChange = {
                            darkTheme = !darkTheme
                            prefHelper.putBoolean(AppConstants.userTheme, darkTheme)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.primary,
                            checkedTrackColor = colorScheme.secondary,
                            uncheckedThumbColor = colorScheme.tertiary,
                            checkedBorderColor = colorScheme.surface,
                            uncheckedBorderColor = colorScheme.surface,
                            uncheckedTrackColor = colorScheme.secondary
                        ),
                        checked = darkTheme,
                    )
                }

                listOf(
                    "Security & Privacy",
                    "Help & Information Center",
                    "Report"
                ).forEach { text ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorScheme.surface)
                            .padding(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(10.dp),
                            text = text,
                            fontFamily = FontFamily(Font(R.font.inter)),
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            SocketHandler.leaveUserConnection(user?.id ?: "") {
                                prefHelper.clear()
                                ZegoUIKitPrebuiltCallService.unInit()
                                prefHelper.putBoolean(AppConstants.userTheme, false)
                                SocketHandler.closeConnection()
                                navHostController.navigate(VChatNavigationItem.LoginScreen.route) {
                                    popUpTo(VChatNavigationItem.ProfileScreen.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(0.dp, 7.dp),
                            fontFamily = FontFamily(Font(R.font.inter)),
                            text = "Logout",
                            color = colorScheme.onPrimary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                        Image(
                            modifier = Modifier
                                .padding(10.dp, 5.dp)
                                .size(16.dp),
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "logout_img",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }
            }
        }
    }

}

//@Composable
//@Preview(showBackground = true, showSystemUi = true)
//fun PrevProfile() {
//    ProfileScreen()
//}