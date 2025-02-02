package com.example.vchat.screens.all_chats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vchat.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllChats(navHostController: NavHostController) {

    var isActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val userList = listOf(
        "Aarav", "Aditya", "Anika", "Aryan", "Avyaan", "Dhruv", "Ishaan", "Kabir", "Reyansh",
        "Vihaan", "Aanya", "Aarohi", "Diya", "Inaaya", "Kashvi", "Navya", "Prisha", "Saisha",
        "Samaira", "Tara", "Vivaan", "Yash", "Zayn", "Aarush", "Advik", "Anaya", "Atharv",
        "Bhavya", "Charvi", "Daksh", "Eesha", "Gaurav", "Harsh", "Ishika", "Jiya", "Kunal",
        "Laksh", "Manya", "Nirav", "Om", "Pari", "Rhea", "Shaurya", "Tanish", "Uday", "Vriti",
        "Yashika", "Zara", "Neil", "Vihaan", "Ishaan", "Atharv", "Aditya", "Aarav", "Kabir",
        "Reyansh", "Dhruv", "Avyaan", "Aryan", "Vivaan", "Yash", "Zayn", "Aarush", "Advik",
        "Anaya", "Bhavya", "Charvi", "Daksh", "Eesha", "Gaurav", "Harsh", "Ishika", "Jiya",
        "Kunal", "Laksh", "Manya", "Nirav", "Om", "Pari", "Rhea", "Shaurya", "Tanish", "Uday",
        "Vriti", "Yashika", "Zara", "Neil"
    )
    var searchResults by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(searchQuery) {
        delay(400)
        searchResults = if (searchQuery.isEmpty()) userList else userList.filter {
            it.contains(
                searchQuery,
                ignoreCase = true
            )
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                ,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Add People",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Image(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = "plus_img"
                )
            }
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp,0.dp,16.dp,16.dp),
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
                        text = "Search",
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
                    ConnectedUsers(searchResults)
                }
            }
            ConnectedUsers(searchResults)

        }
    }
}

@Composable
private fun ConnectedUsers(users: List<String>) {
    LazyColumn {
        items(users) { user ->
            UserTile(user)
        }
    }
}

@Composable
fun UserTile(user: String) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(Color.White),
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
                    text = user,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "Last seen yesterday",
                    fontFamily = FontFamily(Font(R.font.inter)),
                    color = Color.LightGray
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