package com.example.vchat.models.connectUser

import com.google.gson.annotations.SerializedName

data class ConnectUserResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: Nothing,
)
