package com.example.vchat.screens.requests

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
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.vchat.models.requests.GetAllRequestResponse
import com.example.vchat.models.requests.RequestUser
import com.example.vchat.models.requests.RespondRequestResponse
import com.example.vchat.models.useridRequest.UserIdRequest
import com.example.vchat.nav_graph.VChatNavigationItem
import com.example.vchat.screens.connect_people.ConnectPeopleViewModel
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.example.vchat.util.PrefHelper
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(navHostController: NavHostController) {

    val viewModel: RequestViewModel = hiltViewModel()
    val allRequestState by viewModel.getRequestsResponse.collectAsState()
    val acceptRequestState by viewModel.acceptRequestResponse.collectAsState()
    val rejectRequestState by viewModel.rejectRequestResponse.collectAsState()


    val context = LocalContext.current
    var isActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<RequestUser>()) }
    var searchResults by remember { mutableStateOf(listOf<RequestUser>()) }
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()

    LaunchedEffect(Unit) {
        viewModel.getRequests(
            userIdRequest = UserIdRequest(
                prefHelper.getString(AppConstants.userId) ?: ""
            )
        )
    }

    LaunchedEffect(allRequestState) {
        when (allRequestState) {
            is ApiState.Success -> {
                val data =
                    (allRequestState as ApiState.Success<GetAllRequestResponse>).data.data
                userList = data
                searchResults = userList
            }

            is ApiState.Error -> {
                val msg = (allRequestState as ApiState.Error).message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            else -> {

            }
        }
    }

    LaunchedEffect(rejectRequestState) {
        when (rejectRequestState) {
            is ApiState.Success -> {
                val response = (rejectRequestState as ApiState.Success<RespondRequestResponse>).data
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                viewModel.getRequests(
                    userIdRequest = UserIdRequest(
                        prefHelper.getString(AppConstants.userId) ?: ""
                    )
                )
            }

            is ApiState.Error -> {
                val msg = (rejectRequestState as ApiState.Error).message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            else -> {

            }
        }
    }

    LaunchedEffect(acceptRequestState) {
        when (acceptRequestState) {
            is ApiState.Success -> {
                val response = (acceptRequestState as ApiState.Success<RespondRequestResponse>).data
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                viewModel.getRequests(
                    userIdRequest = UserIdRequest(
                        prefHelper.getString(AppConstants.userId) ?: ""
                    )
                )
            }

            is ApiState.Error -> {
                val msg = (acceptRequestState as ApiState.Error).message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

            else -> {

            }
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
                    Requests(searchResults, navHostController, viewModel, prefHelper)
                }
            }
            if (acceptRequestState is ApiState.Loading || rejectRequestState is ApiState.Loading) {
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
                text = "Requests",
                fontFamily = FontFamily(Font(R.font.inter)),
                fontSize = 22.sp,
                color = colorResource(R.color.blue),
                fontWeight = FontWeight.SemiBold,
            )

            Requests(searchResults, navHostController, viewModel, prefHelper)
        }

    }
}

@Composable
private fun Requests(
    users: List<RequestUser>,
    navHostController: NavHostController,
    viewModel: RequestViewModel,
    prefHelper: PrefHelper
) {

    LazyColumn(
        modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 5.dp)
    ) {
        items(users) { user ->
            UserCard(user, viewModel, prefHelper)
        }
    }
}

@Composable
fun UserCard(
    user: RequestUser,
    viewModel: RequestViewModel,
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
                    .fillMaxWidth(0.6f)
                    .fillMaxHeight()
                    .padding(20.dp, 5.dp),
//                verticalArrangement = Arrangement.SpaceAround

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
                    modifier = Modifier
                        .fillMaxWidth(),
//                        .fillMaxHeight(0.4f),
                    text = user.email,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    color = colorResource(R.color.light_grey),
                    fontSize = 14.sp
                )
            }

            Row {
                Image(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 0.dp, 0.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            viewModel.rejectConnectionRequest(
                                ConnectUserRequest(
                                    prefHelper.getString(AppConstants.userId) ?: "",
                                    user.id
                                )
                            )
                        },
                    painter = painterResource(R.drawable.ic_circle_cancel),
                    contentDescription = "check_img"
                )
                Image(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 0.dp, 0.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            viewModel.acceptConnectionRequest(
                                ConnectUserRequest(
                                    prefHelper.getString(AppConstants.userId) ?: "",
                                    user.id
                                )
                            )
                        },
                    painter = painterResource(R.drawable.ic_circle_check),
                    contentDescription = "check_img"
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(10.dp)
        )
    }


}
