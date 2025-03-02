package com.example.vchat.models.login

import com.google.gson.annotations.SerializedName

data class UserConnection(
    @SerializedName("status")
    val status: String?,
    @SerializedName("user")
    val user: String,
    @SerializedName("_id")
    val id: String,
)
