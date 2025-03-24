package com.example.vchat.util

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.UserStatus
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException

object SocketHandler {
    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            mSocket = IO.socket(AppConstants.BASE_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        if (!mSocket.connected()) {
            mSocket.connect()
        }
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    fun leaveUserConnection(userId: String, onCompletion: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (userId.isNotEmpty()) {
                println("LEAVINGGGGGGG......")
                val data = JSONObject().apply {
                    put("userId", userId)
                }
                mSocket?.emit("leave", data)

                mSocket.on("user_status_change") { args ->
                    CoroutineScope(Dispatchers.Main).launch {
                        if (args.isNotEmpty()) {
                            try {
                                onCompletion()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        }
    }

    fun connectUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = JSONObject().apply {
                put("userId", DataCache.userId)
            }
            mSocket?.emit("user_connected", data)
        }
    }
}