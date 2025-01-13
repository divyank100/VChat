package com.example.vchat.util

import com.google.gson.annotations.SerializedName

class ApiResponse <T>{
    @SerializedName("data")
    var data: T? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("message")
    var message: String? = null

    constructor()
    constructor(data: T? = null, status: String? = null, message: String? = null) {
        this.data = data
        this.message = message
        this.status = status
    }
}