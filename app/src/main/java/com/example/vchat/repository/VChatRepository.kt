package com.example.vchat.repository

import com.example.vchat.api.VChatApi
import com.example.vchat.models.DummyResponse
import com.example.vchat.util.ApiResponse
import com.example.vchat.util.SingleResult
import retrofit2.Response
import javax.inject.Inject

class VChatRepository @Inject constructor(private  val vChatApi: VChatApi) {

    suspend fun <T> apiCall(
        apiCall: suspend () -> T
    ): SingleResult<T> {
        return try {
            SingleResult.Success(apiCall.invoke())
        } catch (e: Exception) {
            SingleResult.Error(e)
        }
    }

    suspend fun loginUser(email:String): Response<DummyResponse>{
        return vChatApi.loginUser()
    }



}