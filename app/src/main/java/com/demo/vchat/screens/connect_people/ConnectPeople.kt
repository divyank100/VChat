package com.demo.vchat.screens.connect_people

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.demo.vchat.R
import com.demo.vchat.di.PrefHelperEntryPoint
import com.demo.vchat.models.connectUser.ConnectUserRequest
import com.demo.vchat.models.get_connections.UserConnectionResponse
import com.demo.vchat.models.login.User
import com.demo.vchat.models.useridRequest.UserIdRequest
import com.demo.vchat.nav_graph.VChatNavigationItem
import com.demo.vchat.util.ApiState
import com.demo.vchat.util.AppConstants
import com.demo.vchat.util.PrefHelper
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
            ),context
        )
    }

    LaunchedEffect(connectUserState) {
        when (connectUserState) {
            is ApiState.Success -> {
                delay(500)
                viewModel.getAllUsers(
                    userIdRequest = UserIdRequest(
                        prefHelper.getString(AppConstants.userId) ?: ""
                    ),context
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
                        user.connectionStatus = "Pending"
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
        val colors = MaterialTheme.colorScheme

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = colors.surface),
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp, 10.dp, 16.dp, 16.dp),
                shape = RoundedCornerShape(5.dp),
                query = searchQuery,
                colors = SearchBarDefaults.colors(
                    containerColor = colorScheme.surface
                ),
                onQueryChange = {
                    searchQuery = it
                },
                placeholder = {
                    Text(
                        text = "Search by name",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = colors.onTertiary.copy(alpha = 0.6f)
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
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = "Search icon",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        tint = colors.onTertiary
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
                        color = colors.onSurface
                    )
                } else {
                    ConnectedUsers(searchResults, navHostController, viewModel, prefHelper,context)
                }
            }

            if (userConnectionState is ApiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.surface.copy(alpha = 0.7f))
                        .clickable(enabled = false) {}
                        .then(Modifier.zIndex(10f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colors.primary,
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
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    modifier = Modifier.clickable {
                        navHostController.navigate(VChatNavigationItem.RequestScreen.route)
                    },
                    text = "Requests",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 16.sp,
                    color = colors.primary,
                    textDecoration = TextDecoration.Underline
                )
            }

            ConnectedUsers(searchResults, navHostController, viewModel, prefHelper, context)
        }
    }

}

@Composable
private fun ConnectedUsers(
    users: List<User>,
    navHostController: NavHostController,
    viewModel: ConnectPeopleViewModel,
    prefHelper: PrefHelper,
    context: Context,

    ) {

    LazyColumn(
        modifier = Modifier.padding(0.dp,15.dp,0.dp,5.dp)
    ) {
        items(users) { user ->
            UserCard(user, viewModel, prefHelper,context)
        }
    }
}

@Composable
fun UserCard(
    user: User,
    viewModel: ConnectPeopleViewModel,
    prefHelper: PrefHelper,
    context: Context
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(colors.surface)
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
                    color = colors.onSurface,
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
                            ), context = context
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        disabledContainerColor = colors.secondary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 5.dp,
                    ),
                ) {
                    Text(
                        text = user.connectionStatus,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = if (user.connectionStatus == "Connect")
                            colors.onPrimary
                        else
                            colors.onSecondary
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 80.dp, top = 5.dp, bottom = 5.dp),
            color = colors.tertiary.copy(alpha = 0.5f)
        )
    }
}