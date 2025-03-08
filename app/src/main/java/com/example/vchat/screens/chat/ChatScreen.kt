import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.vchat.R
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.message.Message
import com.example.vchat.ui.chat.ChatViewModel
import com.example.vchat.util.AppConstants
import com.example.vchat.util.Util
import dagger.hilt.android.EntryPointAccessors

@Composable
fun ChatScreen(navHostController: NavHostController, otherUserId: String?, userName: String?) {
    val viewModel: ChatViewModel = hiltViewModel()
    val messages by viewModel.messages.collectAsState()

    val context = LocalContext.current
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()

    var messageText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        viewModel.connectUserToSocket()
        if (otherUserId != null) {
            viewModel.joinRoom(prefHelper.getString(AppConstants.userId) ?: "", otherUserId)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (viewModel.roomId.isNotEmpty()) {
                    viewModel.fetchMessages()
                } else {
                    if (otherUserId != null) {
                        viewModel.joinRoom(
                            prefHelper.getString(AppConstants.userId) ?: "",
                            otherUserId
                        )
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }



    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
        println("MESG ARRAY --- $messages")
    }
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = colorResource(R.color.chat_bg))

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = Color.White)
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier.size(18.dp),
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "back_icon"
                        )
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = userName.toString(),
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(
                    modifier = Modifier.size(22.dp),
                    onClick = {

                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_call),
                        contentDescription = "call_icon"
                    )
                }
            }


            LazyColumn(
                modifier = Modifier.weight(1.0f),
                state = scrollState
            ) {
                items(messages) { message ->
                    ChatBubble(message, prefHelper.getString(AppConstants.userId) ?: "")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f),
                    value = messageText,
                    onValueChange = { messageText = it },
                    shape = RoundedCornerShape(10.dp),
                    placeholder = {
                        Text(
                            text = "Send a message...",
                            fontFamily = FontFamily(Font(R.font.inter))
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
//                        keyboardController?.hide()
                        }
                    ),
                    visualTransformation = VisualTransformation.None,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colorResource(R.color.chat_bg),
                        focusedContainerColor = colorResource(R.color.chat_bg),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(
                    onClick = {
                        viewModel.sendMessage(
                            prefHelper.getString(AppConstants.userId) ?: "",
                            messageText
                        )
                        messageText = ""
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = "Send Message",
                        tint = colorResource(R.color.blue)
                    )
                }
            }


        }
    }
}

@Composable
fun ChatBubble(message: Message, currentUserId: String) {
    val isCurrentUser = message.senderId == currentUserId
    val bubbleColor = if (isCurrentUser) {
        Color.Blue
    } else {
        Color.White
    }
    val msgTextColor = if (isCurrentUser) {
        Color.White
    } else {
        Color.Black
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isCurrentUser) 12.dp else 0.dp,
                        bottomEnd = if (isCurrentUser) 0.dp else 12.dp
                    )
                )
                .align(alignment)
                .padding(horizontal = 10.dp, vertical = 3.dp),
        ) {
            Text(
//                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                text = message.content,
                color = msgTextColor,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.inter))
            )
            Text(
                modifier = Modifier
                    .align(Alignment.End),
                text = Util.formatTimestamp(message.timestamp),
                color = msgTextColor,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.inter))
            )

        }
    }
}
