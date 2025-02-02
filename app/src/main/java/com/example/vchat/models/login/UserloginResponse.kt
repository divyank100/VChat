package com.example.vchat.models.login


import com.google.gson.annotations.SerializedName


data class UserloginResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)