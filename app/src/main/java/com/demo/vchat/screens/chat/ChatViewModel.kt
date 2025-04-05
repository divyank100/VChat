package com.demo.vchat.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.vchat.models.UserStatus
import com.demo.vchat.models.message.Message
import com.demo.vchat.util.SocketHandler
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {
    private val TAG = "ChatViewModel"
    private val socket: Socket = SocketHandler.getSocket()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isOtherUserOnline = MutableStateFlow(UserStatus("", false))
    val isOtherUserOnline: StateFlow<UserStatus> = _isOtherUserOnline.asStateFlow()


    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    var roomId = ""
    private var listenersSetup = false

    init {
        setupSocketListeners()
        checkConnectionStatus()
    }


    private fun checkConnectionStatus() {
        _isConnected.value = socket.connected()
    }

    private fun setupSocketListeners() {
        if (listenersSetup) return
        listenersSetup = true

        socket.on(Socket.EVENT_CONNECT) {
            Log.d(TAG, "Socket connected")
            _isConnected.value = true
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d(TAG, "Socket disconnected")
            _isConnected.value = false
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e(TAG, "Connection error: ${args[0]}")
            _isConnected.value = false
        }

        socket?.on("user_status_change") { args ->
            Log.d(TAG, "User status changed")
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as JSONObject
                    val userStatus =
                        UserStatus(data.getString("userId"), data.getBoolean("online_status"))
                    _isOtherUserOnline.value = userStatus

                    println("ONLINE STATUS---- ${data.getString("online_status")}")
                    println("USERIDDDDD---- ${data.getString("userId")}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing previousMessages array", e)
                }
            }
        }

        socket.on("previousMessages") { args ->
            Log.d(TAG, "Previous messages received")
            if (args.isNotEmpty()) {
                try {
                    val jsonArray = args[0] as JSONArray
                    val messagesList = List(jsonArray.length()) { index ->
                        try {
                            val jsonObject = jsonArray.getJSONObject(index)
                            Message(
                                messageId = jsonObject.getString("messageId"),
                                content = jsonObject.getString("content"),
                                senderId = jsonObject.getString("senderId"),
                                timestamp = jsonObject.getString("timestamp"),
                                roomId = jsonObject.getString("roomId")
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing previous message at index $index", e)
                            null
                        }
                    }.filterNotNull()
                    _messages.value = messagesList
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing previousMessages array", e)
                }
            }
        }


        socket.on("messageHistory") { args ->
            Log.d(TAG, "Message history received")
            if (args.isNotEmpty()) {
                try {
                    val jsonArray = args[0] as JSONArray
                    val messagesList = List(jsonArray.length()) { index ->
                        try {
                            val jsonObject = jsonArray.getJSONObject(index)
                            Message(
                                messageId = jsonObject.getString("messageId"),
                                content = jsonObject.getString("content"),
                                senderId = jsonObject.getString("senderId"),
                                timestamp = jsonObject.getString("timestamp"),
                                roomId = jsonObject.getString("roomId")
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing message history at index $index", e)
                            null
                        }
                    }.filterNotNull()
                    _messages.value = messagesList
                    println("MSG HISTORY---- ${_messages.value}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing messageHistory array", e)
                }
            }
        }


        socket.on("message") { args ->
            Log.d(TAG, "New message received")
            if (args.isNotEmpty()) {
                try {
                    val jsonObject = args[0] as JSONObject
                    val newMessage = Message(
                        messageId = jsonObject.getString("messageId"),
                        content = jsonObject.getString("content"),
                        senderId = jsonObject.getString("senderId"),
                        timestamp = jsonObject.getString("timestamp"),
                        roomId = jsonObject.getString("roomId")
                    )

                    // Check if message is for current room
                    if (newMessage.roomId == roomId &&
                        !_messages.value.any { it.messageId == newMessage.messageId }
                    ) {
                        println("%%%%%NEW MSGGG RECEIVEDDD")
                        _messages.value = _messages.value + newMessage
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing incoming message", e)
                }
            }
        }

    }

    fun connectUserToSocket() {
        if (!socket.connected()) {
            socket.connect()
        }

    }

    fun joinRoom(currentUserId: String, otherUserId: String) {
        val sortedIds = listOf(currentUserId, otherUserId).sorted()
        println("currentId ${currentUserId}")
        println("otherId ${otherUserId}")
        println("SORTED IDS ${sortedIds}")
        roomId = "${sortedIds[0]}-${sortedIds[1]}"
        println("ROOM ID ${roomId}")

        val data = JSONObject().apply {
            put("userId", currentUserId)
            put("recipientId", otherUserId)
        }
        socket.emit("joinRoom", data)

        fetchMessages()
    }

    fun fetchMessages() {
        val data = JSONObject().apply {
            put("roomId", roomId)
            put("limit", 50)
        }
        socket.emit("fetchMessages", data)
    }

    fun sendMessage(userId: String, message: String) {
        if (message.isBlank() || roomId.isEmpty()) return

        viewModelScope.launch {
            val data = JSONObject().apply {
                put("roomId", roomId)
                put("userId", userId)
                put("message", message)
            }
            socket.emit("sendMessage", data)

            val currentTime =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(Date())

            val newMessage = Message(
                messageId = UUID.randomUUID().toString(),
                content = message,
                senderId = userId,
                timestamp = currentTime,
                roomId = roomId
            )

            _messages.value = _messages.value + newMessage
        }
    }

    fun removeSocketListeners() {
        socket.off(Socket.EVENT_CONNECT)
        socket.off(Socket.EVENT_DISCONNECT)
        socket.off(Socket.EVENT_CONNECT_ERROR)
        socket.off("user_status_change")
        socket.off("previousMessages")
        socket.off("messageHistory")
        socket.off("message")
        listenersSetup = false
    }

    fun disconnectSocket() {
        if (socket.connected()) {
            socket.disconnect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeSocketListeners()
        // No need to disconnect here if you want to maintain the socket connection
        // across different screens. If you want to disconnect, uncomment:
        // socket.disconnect()
    }

}