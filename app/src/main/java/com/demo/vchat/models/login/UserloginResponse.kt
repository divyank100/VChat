package com.demo.vchat.models.login


import com.google.gson.annotations.SerializedName


data class UserloginResponse(
    @SerializedName("data")
    val data: UserrData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String

){
    data class UserrData(
        @SerializedName("accessToken")
        val accessToken: String,
        @SerializedName("refreshToken")
        val refreshToken: String,
        @SerializedName("user")
        val user: User,
    )
}


