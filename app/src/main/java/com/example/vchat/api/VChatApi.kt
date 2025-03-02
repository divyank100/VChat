package com.example.vchat.api

import com.example.vchat.models.connectUser.ConnectUserRequest
import com.example.vchat.models.connectUser.ConnectUserResponse
import com.example.vchat.models.forgot_password.ForgotPasswordRequest
import com.example.vchat.models.get_connections.UserConnectionResponse
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.models.requests.GetAllRequestResponse
import com.example.vchat.models.requests.RespondRequestResponse
import com.example.vchat.models.signup.UserSignupRequest
import com.example.vchat.models.token_request.TokenRequestJWT
import com.example.vchat.models.token_request.TokenResponse
import com.example.vchat.models.useridRequest.UserIdRequest
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
    suspend fun signUpUser(@Body signUpRequest: UserSignupRequest): Response<UserloginResponse>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Response<Nothing>

    @POST("api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body tokenRequestJWT: TokenRequestJWT): Response<TokenResponse>

    @POST("api/v1/auth/get-all-users")
    suspend fun getAllUsers(@Body userIdRequest: UserIdRequest): Response<UserConnectionResponse>

    @POST("api/v1/chat/create-connection")
    suspend fun connectUser(@Body connectUserRequest: ConnectUserRequest): Response<ConnectUserResponse>

    @POST("api/v1/chat/get-connections")
    suspend fun getUserConnections(@Body userIdRequest: UserIdRequest): Response<UserConnectionResponse>

    @POST("api/v1/chat/get-requests")
    suspend fun getRequests(@Body userIdRequest: UserIdRequest): Response<GetAllRequestResponse>

    @POST("api/v1/chat/accept-request")
    suspend fun acceptRequest(@Body connectUserRequest: ConnectUserRequest): Response<RespondRequestResponse>

    @POST("api/v1/chat/reject-request")
    suspend fun rejectRequest(@Body connectUserRequest: ConnectUserRequest): Response<RespondRequestResponse>


}