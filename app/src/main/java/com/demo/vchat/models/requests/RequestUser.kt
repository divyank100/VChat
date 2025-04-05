package com.demo.vchat.models.requests

import com.google.gson.annotations.SerializedName

data class RequestUser(
    @SerializedName("_id")
    val id: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("userProfile")
    val userProfile: String,
    @SerializedName("userStatus")
    val userStatus: String,
    @SerializedName("connectionStatus")
    val connectionStatus: String,
)
