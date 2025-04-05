package com.demo.vchat.repository

import android.content.Context
import com.demo.vchat.api.VChatApi
import com.demo.vchat.models.connectUser.ConnectUserRequest
import com.demo.vchat.models.connectUser.ConnectUserResponse
import com.demo.vchat.models.forgot_password.ForgotPasswordRequest
import com.demo.vchat.models.get_connections.UserConnectionResponse
import com.demo.vchat.models.login.UserLoginRequest
import com.demo.vchat.models.login.UserloginResponse
import com.demo.vchat.models.requests.GetAllRequestResponse
import com.demo.vchat.models.requests.RespondRequestResponse
import com.demo.vchat.models.signup.UserSignupRequest
import com.demo.vchat.models.token_request.TokenRequestJWT
import com.demo.vchat.models.useridRequest.UserIdRequest
import com.demo.vchat.util.AppConstants
import com.demo.vchat.util.DataCache
import com.demo.vchat.util.PrefHelper
import com.demo.vchat.util.Util
import retrofit2.Response
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VChatRepository @Inject constructor(
    private val vChatApi: VChatApi,
    private val prefHelper: PrefHelper
) {

    suspend fun refreshToken(
        context: Context, isApiCall: Boolean? = false
    ) {
        try {
            if (Util.isExpired(prefHelper) || isApiCall == true) {
                DataCache.refreshToken = prefHelper.getString(AppConstants.refreshToken)
                val token = TokenRequestJWT(
                    prefHelper.getString(AppConstants.refreshToken) ?: ""
                )
                val response = vChatApi.refreshToken(token)
                if (response.isSuccessful) {
                    DataCache.accessToken = response.body()?.data?.accessToken
                    DataCache.refreshToken = response.body()?.data?.refreshToken
                    val currentTime: Long = Calendar.getInstance().time.time
                    prefHelper.putString(AppConstants.currentTime, currentTime.toString())
                    prefHelper.putString(
                        AppConstants.refreshToken, response.body()?.data?.refreshToken.toString()
                    )
                    prefHelper.putString(
                        AppConstants.accessToken, response.body()?.data?.accessToken.toString()
                    )
                    prefHelper.putString(
                        AppConstants.expiryTime,
                        TimeUnit.SECONDS.toMillis("3000".toLong())
                            .toString()
                    )
                } else {
                    prefHelper.clear()
//                    if (context is ViewComponentManager.FragmentContextWrapper) {
//                        (context.baseContext as ToolbarChangeListener?)?.triggerRebirth()
//                    } else {
//                        (context as ToolbarChangeListener?)?.triggerRebirth()
//                    }
                }
            } else {
                DataCache.accessToken = prefHelper.getString(AppConstants.accessToken)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loginUser(userLoginRequest: UserLoginRequest): Response<UserloginResponse> {
        return vChatApi.loginUser(userLoginRequest)
    }

    suspend fun signUpUser(userSignUpRequest: UserSignupRequest): Response<UserloginResponse> {
        return vChatApi.signUpUser(userSignUpRequest)
    }

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Response<Nothing> {
        return vChatApi.forgotPassword(forgotPasswordRequest)
    }

    suspend fun getAllUsers(userIdRequest: UserIdRequest): Response<UserConnectionResponse> {
        return vChatApi.getAllUsers(userIdRequest)
    }

    suspend fun connectUser(connectUserRequest: ConnectUserRequest): Response<ConnectUserResponse> {
        return vChatApi.connectUser(connectUserRequest)
    }

    suspend fun getUserConnections(userIdRequest: UserIdRequest): Response<UserConnectionResponse> {
        return vChatApi.getUserConnections(userIdRequest)
    }

    suspend fun getRequests(userIdRequest: UserIdRequest): Response<GetAllRequestResponse> {
        return vChatApi.getRequests(userIdRequest)
    }

    suspend fun acceptRequest(connectUserRequest: ConnectUserRequest): Response<RespondRequestResponse> {
        return vChatApi.acceptRequest(connectUserRequest)
    }

    suspend fun rejectRequest(connectUserRequest: ConnectUserRequest): Response<RespondRequestResponse> {
        return vChatApi.rejectRequest(connectUserRequest)
    }


    suspend fun updateDeviceToken(userId: String): Response<Nothing> {
        TODO("Not yet implemented")
    }


}