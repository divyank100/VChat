package com.example.vchat.screens.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vchat.R
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.message.Message
import com.example.vchat.util.AppConstants
import com.example.vchat.util.SocketHandler
import com.example.vchat.util.Util
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun ChatScreen(navHostController: NavHostController, userId: String?, userName: String?) {
    // Here will create an instance of viewmodel and observe the messages(list)
    val msgList = remember { mutableStateListOf<Message>() }

    val context = LocalContext.current
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()

    LaunchedEffect(Unit) {
        val socket = SocketHandler.getSocket()

        if (!socket.connected()) {
            SocketHandler.establishConnection()
        }

//        delay(500)

        if (socket.connected()) {
            Log.d("Socket", "Socket is connected!")
        } else {
            Log.d("Socket", "Socket is NOT connected!")
        }

        val currentUserId = prefHelper.getString(AppConstants.userId) ?: ""
        val otherUserId = userId ?: ""

        val sortedIds = listOf(
            currentUserId.toCharArray().sorted().joinToString(""),
            otherUserId.toCharArray().sorted().joinToString("")
        ).sorted()

        socket.off("message")
        socket.off("messageHistory")

        val roomId = "${sortedIds[0]}-${sortedIds[1]}"

        val json = JSONObject().apply {
//            put("roomId", roomId)
            put("userId", currentUserId)
            put("recipientId", otherUserId)
        }

        socket.emit("joinRoom", json)

        println("Message event about to start")
        delay(500)
        println("After 500 delay")

        socket.on("message") { args ->
            Log.d("Socket", "Message event triggered with args: ${args.contentToString()}")
            if (args.isNotEmpty()) {
                try {
                    val messageData = args[0]
                    Log.d("Socket", "Message data type: ${messageData?.javaClass}")

                    val messageJson = when (messageData) {
                        is JSONObject -> messageData
                        is String -> JSONObject(messageData)
                        else -> {
                            val jsonString = messageData.toString()
                            Log.d("Socket", "Raw message data: $jsonString")
                            try {
                                JSONObject(jsonString)
                            } catch (e: Exception) {
                                Log.e("Socket", "Failed to parse message: $jsonString", e)
                                return@on
                            }
                        }
                    }

                    Log.d("Socket", "Parsed message JSON: $messageJson")

                    val messageOp = Gson().fromJson(
                        messageJson.toString(),
                        Message::class.java
                    )

                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        msgList.add(messageOp)
                        Log.d("Socket", "Added message to UI: $messageOp")
                    }
                } catch (e: Exception) {
                    Log.e("Socket", "Error processing message: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }


        socket.on("message") { args ->
            Log.d("Socket", "Message event triggered with args: ${args.contentToString()}")
            if (args.isNotEmpty()) {
                try {
                    // Handle different potential formats from Socket.IO
                    val messageData = args[0]
                    Log.d("Socket", "Message data type: ${messageData?.javaClass}")

                    val messageJson = when (messageData) {
                        is JSONObject -> messageData
                        is String -> JSONObject(messageData)
                        else -> {
                            // Try to parse as a string representation
                            try {
                                JSONObject(messageData.toString())
                            } catch (e: Exception) {
                                Log.e("Socket", "Failed to parse message: ${messageData.toString()}", e)
                                return@on
                            }
                        }
                    }

                    Log.d("Socket", "Parsed message JSON: $messageJson")

                    val messageOp = Gson().fromJson(
                        messageJson.toString(),
                        Message::class.java
                    )

                    // Update UI on the main thread
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        msgList.add(messageOp)
                        Log.d("Socket", "Added message to UI: $messageOp")
                    }
                } catch (e: Exception) {
                    Log.e("Socket", "Error processing message: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }


        val fetchMessagesJson = JSONObject().apply {
            put("roomId", roomId)
            put("limit", 50)
        }
        socket.emit("fetchMessages", fetchMessagesJson)

        socket.on("messageHistory") { args ->
            println("args ${args[0]}")
            if (args.isNotEmpty() && args[0] is JSONArray) {
                val messagesArray = args[0] as JSONArray
                val newMessages = mutableListOf<Message>()

                for (i in 0 until messagesArray.length()) {
                    val messageJson = messagesArray.getJSONObject(i) // Get JSONObject
                    val message = Gson().fromJson(
                        messageJson.toString(),
                        Message::class.java
                    ) // Convert JSON to Message
                    newMessages.add(message)
                }

                msgList.clear()
                msgList.addAll(newMessages)
            } else {
                Log.d("Socket", "messageHistory event received but data is invalid")
            }
        }


    }

    DisposableEffect(Unit) {
        onDispose {
            val socket = SocketHandler.getSocket()
            socket.off("messageHistory")
            socket.off("message")
        }
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

            ChatMessages(
                modifier = Modifier.weight(1f),
                msgList,
                onSendMessage = { message ->
                    println("Sending message.... $message")
                    val currentUserId = prefHelper.getString(AppConstants.userId) ?: ""
                    val otherUserId = userId ?: ""

                    val sortedCurrentUserId = currentUserId.toCharArray().sorted().joinToString("")
                    val sortedOtherUserId = otherUserId.toCharArray().sorted().joinToString("")

                    val sortedIds = listOf(sortedCurrentUserId, sortedOtherUserId).sorted()

                    val roomId = "${sortedIds[0]}-${sortedIds[1]}"
                    val json = JSONObject().apply {
                        put("roomId", roomId)
                        put("userId", currentUserId)
                        put("message", message)
                    }
                    SocketHandler.getSocket().emit("sendMessage", json)
                    msgList.add(
                        Message(
                            messageId = "",
                            content = message,
                            senderId = currentUserId,
                            timestamp = "Just now",
                            roomId = roomId
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun ChatMessages(
    modifier: Modifier,
    messages: List<Message>,
    onSendMessage: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var msg by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier.weight(1.0f),
            state = listState
        ) {
            items(messages) { message ->
                ChatBubble(message)
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
                value = msg,
                onValueChange = { msg = it },
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
                        keyboardController?.hide()
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
                    onSendMessage(msg)
                    msg = ""
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

@Composable
fun ChatBubble(message: Message) {
    val context = LocalContext.current
    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()
    val isCurrentUser = message.senderId == prefHelper.getString(AppConstants.userId) ?: ""
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
