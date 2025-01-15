package com.example.vchat.models

import com.google.gson.annotations.SerializedName

data class Reactions(
    @SerializedName("dislikes")
    val dislikes: Int,
    @SerializedName("likes")
    val likes: Int
)