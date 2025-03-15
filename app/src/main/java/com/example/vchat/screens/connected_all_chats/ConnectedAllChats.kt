package com.example.vchat.screens.connected_all_chats

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.vchat.MainActivity
import com.example.vchat.R
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.get_connections.UserConnectionResponse
import com.example.vchat.models.login.User
import com.example.vchat.models.useridRequest.UserIdRequest
import com.example.vchat.nav_graph.VChatNavigationItem
import com.example.vchat.service.ZegoCloudService
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.example.vchat.util.SocketHandler
import com.google.gson.Gson
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllChats(navHostController: NavHostController) {

    val viewModel: ConnectedChatsViewModel = hiltViewModel()
    val userConnectionState by viewModel.connectPeopleResponse.collectAsState()
    val context = LocalContext.current as MainActivity
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()


    var isActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<User>()) }
    var searchResults by remember { mutableStateOf(listOf<User>()) }
    val gson = Gson()
    val json: String? = prefHelper.getString(AppConstants.userData)
    val user: User? = gson.fromJson(json, User::class.java)

    LaunchedEffect(Unit) {
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        context.initZegoInviteService(AppConstants.APP_ID, AppConstants.APP_SIGN, user?.userName!!, user.userName)
        viewModel.getConnections(
            userIdRequest = UserIdRequest(
                user.id
            )
        )
    }

    LaunchedEffect(userConnectionState) {
        when (userConnectionState) {
            is ApiState.Success -> {
                val data =
                    (userConnectionState as ApiState.Success<UserConnectionResponse>).data.data
                userList = data
                searchResults = userList
            }

            is ApiState.Error -> {
                val errorMessage = (userConnectionState as ApiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(searchQuery) {
        delay(400)
        searchResults = if (searchQuery.isEmpty()) userList else userList.filter {
            it.userName.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White),
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Connect People",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Image(
                    modifier = Modifier.clickable {
                        navHostController.navigate(VChatNavigationItem.ConnectPeople.route)
                    },
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "connect_people_icon"
                )
            }
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp, 0.dp, 16.dp, 16.dp),
                shape = RoundedCornerShape(5.dp),
                query = searchQuery,
//                shadowElevation = 2.dp,
                colors = SearchBarDefaults.colors(
                    containerColor = colorResource(R.color.off_white)
                ),
                onQueryChange = {
                    searchQuery = it
                },
                placeholder = {
                    Text(
                        text = "Search by name",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = Color.LightGray
                    )
                },
                onSearch = {
                    println("SEARCHING---- ${searchQuery}")
                    isActive = false
                },
                active = isActive,
                onActiveChange = { newActiveState ->
                    isActive = newActiveState
                    if (!newActiveState) {
                        searchQuery = ""
                        searchResults = userList
                    }
                    println("Active State Changed: $newActiveState")
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search icon",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                    )
                }

            ) {
                if (searchQuery.isEmpty()) {
                    Text("No users found")
                } else {
                    ConnectedUsers(searchResults, navHostController)
                }
            }
            if (userConnectionState is ApiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .wrapContentSize(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.blue),
                        strokeWidth = 4.dp
                    )
                }
            }
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                text = "Chats",
                fontFamily = FontFamily(Font(R.font.inter)),
                fontSize = 22.sp,
                color = colorResource(R.color.blue),
                fontWeight = FontWeight.SemiBold,
            )
            ConnectedUsers(searchResults, navHostController)

        }
    }
}

@Composable
private fun ConnectedUsers(users: List<User>, navHostController: NavHostController) {
    LazyColumn(
        modifier = Modifier.padding(0.dp,15.dp,0.dp,5.dp)
    ) {
        items(users) { user ->
            UserCard(user, navHostController)
        }
    }
}

@Composable
fun UserCard(user: User, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(Color.White)
            .clickable {
                navHostController.navigate(VChatNavigationItem.ChatScreen.route + "/${user.id}" + "/${user.userName}")
            },
    ) {

        Row(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.img_profile),
                contentDescription = "Profile img",
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .size(65.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp, 10.dp),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f),
                    text = user.userName,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = if (user.userStatus) "Online" else "Offline",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(10.dp)
        )
    }


}

//@Composable
//@Preview(showSystemUi = true, showBackground = true)
//fun ChatScreen() {
//    AllChats()
//}