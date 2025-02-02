package com.example.vchat.api

import com.example.vchat.models.forgot_password.ForgotPasswordRequest
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface VChatApi {
    @POST("api/v1/auth/login")
    suspend fun loginUser(@Body userLoginRequest: UserLoginRequest): Response<UserloginResponse>

    @POST("api/v1/auth/register")
    suspend fun signUpUser(@Body signUpRequest: UserLoginRequest): Response<UserloginResponse>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Nothing>
}