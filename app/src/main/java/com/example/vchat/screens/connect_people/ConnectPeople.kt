package com.example.vchat.screens.connect_people

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.vchat.R
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.connectUser.ConnectUserRequest
import com.example.vchat.models.get_connections.UserConnectionResponse
import com.example.vchat.models.login.User
import com.example.vchat.models.useridRequest.UserIdRequest
import com.example.vchat.nav_graph.VChatNavigationItem
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.example.vchat.util.PrefHelper
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectPeople(navHostController: NavHostController) {
    val viewModel: ConnectPeopleViewModel = hiltViewModel()
    val userConnectionState by viewModel.connectPeopleResponse.collectAsState()


    val context = LocalContext.current
    var isActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<User>()) }
    var searchResults by remember { mutableStateOf(listOf<User>()) }
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()
    val connectUserState by viewModel.connectUserResponse.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllUsers(
            userIdRequest = UserIdRequest(
                prefHelper.getString(AppConstants.userId) ?: ""
            )
        )
    }

    LaunchedEffect(connectUserState) {
        when (connectUserState) {
            is ApiState.Success -> {
                delay(500)
                viewModel.getAllUsers(
                    userIdRequest = UserIdRequest(
                        prefHelper.getString(AppConstants.userId) ?: ""
                    )
                )
            }

            is ApiState.Error -> {
                val errorMessage = (connectUserState as ApiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(userConnectionState) {
        when (userConnectionState) {
            is ApiState.Success -> {
                val data =
                    (userConnectionState as ApiState.Success<UserConnectionResponse>).data.data
                userList = data
                userList.forEach { user ->
                    val requestedUserPresent =
                        user.requestedConnections.find { prefHelper.getString(AppConstants.userId) == it.user }
                    val connectedUserPresent =
                        user.connections.find { prefHelper.getString(AppConstants.userId) == it.user }
                    if (requestedUserPresent != null) {
                        user.connectionStatus = "Requested"
                    } else if (connectedUserPresent != null) {
                        user.connectionStatus = "Connected"
                    } else {
                        user.connectionStatus = "Connect"
                    }
                }
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
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp, 10.dp, 16.dp, 16.dp),
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
                    isActive = false
                },
                active = isActive,
                onActiveChange = { newActiveState ->
                    isActive = newActiveState
                    if (!newActiveState) {
                        searchQuery = ""
                        searchResults = userList
                    }
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
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        text = "No users found",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = Color.Black
                    )
                } else {
                    ConnectedUsers(searchResults, navHostController, viewModel, prefHelper)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Connect People",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 22.sp,
                    color = colorResource(R.color.blue),
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    modifier = Modifier.clickable {
                        navHostController.navigate(VChatNavigationItem.RequestScreen.route)
                    },
                    text = "Requests",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 16.sp,
                    color = colorResource(R.color.blue),
                    textDecoration = TextDecoration.Underline
                )

            }
            ConnectedUsers(searchResults, navHostController, viewModel, prefHelper)
        }

    }

}

@Composable
private fun ConnectedUsers(
    users: List<User>,
    navHostController: NavHostController,
    viewModel: ConnectPeopleViewModel,
    prefHelper: PrefHelper
) {

    LazyColumn(
        modifier = Modifier.padding(0.dp,15.dp,0.dp,5.dp)
    ) {
        items(users) { user ->
            UserCard(user, viewModel, prefHelper)
        }
    }
}

@Composable
fun UserCard(
    user: User,
    viewModel: ConnectPeopleViewModel,
    prefHelper: PrefHelper
) {

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = user.userProfile, // Pass the URL here
                    placeholder = painterResource(R.drawable.img_profile),
                    error = painterResource(R.drawable.img_profile)
                ),
                contentDescription = "Profile img",
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp)
                    .size(65.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp, 5.dp),
                verticalArrangement = Arrangement.SpaceAround

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
                Button(
                    modifier = Modifier.padding(0.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = user.connectionStatus == "Connect",
                    onClick = {
                        // Api call to connect the user and change the btnText on Success response
                        viewModel.connectUser(
                            ConnectUserRequest(
                                prefHelper.getString(AppConstants.userId) ?: "",
                                user.id
                            )
                        )
//                        btnText = "Requested"
//                        btnEnable = false
//                        btnTextColor = Color.Black
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.blue),
                        disabledContainerColor = colorResource(R.color.light_blue),
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 5.dp,
                    ),
                ) {
                    Text(
                        text = user.connectionStatus,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = if (user.connectionStatus == "Connect") Color.White else Color.Black
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(10.dp)
        )
    }


}