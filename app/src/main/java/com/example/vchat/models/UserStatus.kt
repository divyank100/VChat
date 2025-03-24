package com.example.vchat.models

import com.google.gson.annotations.SerializedName

data class UserStatus(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("online_status")
    val online_status: Boolean,
)
