package com.demo.vchat.models.login


import com.google.gson.annotations.SerializedName


data class UserLoginRequest(
    @SerializedName("deviceToken")
    val deviceToken: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)