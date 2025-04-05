package com.demo.vchat.models.get_connections

import com.demo.vchat.models.login.User
import com.google.gson.annotations.SerializedName

data class UserConnectionResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<User>,
)
