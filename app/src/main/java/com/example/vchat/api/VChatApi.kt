package com.example.vchat.api

import com.example.vchat.models.DummyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface VChatApi {
    @GET("posts")
    suspend fun loginUser(): Response<DummyResponse>
}