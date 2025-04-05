package com.demo.vchat.models.token_request

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("data")
    val data: Token,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Token(
        @SerializedName("refreshToken")
        val refreshToken: String,
        @SerializedName("accessToken")
        val accessToken: String,
    )
}
