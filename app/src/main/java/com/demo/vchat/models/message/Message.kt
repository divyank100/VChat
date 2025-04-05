package com.demo.vchat.models.message

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("messageId")
    val messageId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("roomId")
    val roomId: String
)
