package com.example.vchat.models.requests

import com.google.gson.annotations.SerializedName

data class GetAllRequestResponse(
    @SerializedName("data")
    val data: List<RequestUser>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
