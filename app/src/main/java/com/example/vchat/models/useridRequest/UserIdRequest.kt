package com.example.vchat.models.useridRequest

import com.google.gson.annotations.SerializedName

data class UserIdRequest(
    @SerializedName("userId")
    val userId: String,
)
