package com.example.vchat.models.signup


import com.google.gson.annotations.SerializedName


data class UserSignupRequest(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)