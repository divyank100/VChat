package com.example.vchat.repository

import android.content.Context
import com.example.vchat.api.VChatApi
import com.example.vchat.models.forgot_password.ForgotPasswordRequest
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.util.AppConstants
import com.example.vchat.util.DataCache
import com.example.vchat.util.PrefHelper
import com.example.vchat.util.Util
import retrofit2.Response
import javax.inject.Inject

class VChatRepository @Inject constructor(private val vChatApi: VChatApi,private val prefHelper: PrefHelper) {

    suspend fun refreshToken(
        context: Context, isApiCall: Boolean? = false
    ) {
        try {
            if (Util.isExpired(prefHelper) || isApiCall == true) {
                DataCache.refreshToken = prefHelper.getString(AppConstants.refreshToken)
//                val token = TokenRequestJWT(
//                    DataCache.refreshToken
//                )
//                val response = vChatApi.refreshTokenJWT(token)
//                if (response.isSuccessful) {
//                    DataCache.token = response.body()?.accessToken
//                    DataCache.refreshToken = response.body()?.refreshToken
//                    val currentTime: Long = Calendar.getInstance().time.time
//                    prefHelper.putString(AppConstants.currentTime, currentTime.toString())
//                    prefHelper.putString(
//                        AppConstants.refreshToken, response.body()?.refreshToken.toString()
//                    )
//                    prefHelper.putString(
//                        AppConstants.accessToken, response.body()?.accessToken.toString()
//                    )
//                    prefHelper.putString(
//                        AppConstants.expiryTime,
//                        TimeUnit.SECONDS.toMillis(response.body()?.expiresIn.toString().toLong())
//                            .toString()
//                    )
//                }
//                else{
//                    prefHelper.clear()
//                    if (context is ViewComponentManager.FragmentContextWrapper) {
//                        (context.baseContext as ToolbarChangeListener?)?.triggerRebirth()
//                    } else {
//                        (context as ToolbarChangeListener?)?.triggerRebirth()
//                    }
//                }
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

    suspend fun signUpUser(userSignUpRequest: UserLoginRequest): Response<UserloginResponse> {
        return vChatApi.signUpUser(userSignUpRequest)
    }

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Response<Nothing> {
        return vChatApi.forgotPassword(forgotPasswordRequest)
    }

    suspend fun updateDeviceToken(userId:String):Response<Nothing> {
        println("Inside repo$userId")
        TODO("Not yet implemented")
    }


}