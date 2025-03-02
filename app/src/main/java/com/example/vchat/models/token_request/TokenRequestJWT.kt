package com.example.vchat.models.token_request

import com.google.gson.annotations.SerializedName

data class TokenRequestJWT(
    @SerializedName("refreshToken")
    val refreshToken: String,
)
