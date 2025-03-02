package com.example.vchat.models.connectUser

import com.google.gson.annotations.SerializedName

data class ConnectUserRequest(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("receiverId")
    val receiverId: String,
)

