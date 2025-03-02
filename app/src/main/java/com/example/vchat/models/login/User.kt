package com.example.vchat.models.login


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("device_token")
    val deviceToken: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("userProfile")
    val userProfile: String,
    @SerializedName("userStatus")
    val userStatus: Boolean,
    @SerializedName("__v")
    val v: Int,
    @SerializedName("connections")
    val connections: List<UserConnection>,
    @SerializedName("requestedConnections")
    val requestedConnections: List<UserConnection>,
    @SerializedName("connectionStatus")
    var connectionStatus: String
)