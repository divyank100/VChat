package com.example.vchat.models.requests

import com.google.gson.annotations.SerializedName

data class RespondRequestResponse(
    @SerializedName("data")
    val data: Nothing?,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String

)
