package com.demo.vchat.models.forgot_password

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
