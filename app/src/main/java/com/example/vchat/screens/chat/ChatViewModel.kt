package com.example.vchat.ui.chat

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.message.Message
import com.example.vchat.util.AppConstants
import com.example.vchat.util.SocketHandler
import com.example.vchat.util.Util
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

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    var roomId = ""

    init {
        setupSocketListeners()
        checkConnectionStatus()
    }


    private fun checkConnectionStatus() {
        _isConnected.value = socket.connected()
    }

    private fun setupSocketListeners() {
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
                    if (newMessage.roomId == roomId) {
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
        roomId = "${sortedIds[0]}-${sortedIds[1]}"

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

    fun disconnectSocket() {
        if (socket.connected()) {
            socket.disconnect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // No need to disconnect here if you want to maintain the socket connection
        // across different screens. If you want to disconnect, uncomment:
        // socket.disconnect()
    }

}