package com.example.vchat.repository

import com.example.vchat.util.SingleResult
import javax.inject.Inject

class VChatRepository @Inject constructor(private  val repository: VChatRepository) {

    suspend fun <T> apiCall(
        apiCall: suspend () -> T
    ): SingleResult<T> {
        return try {
            SingleResult.Success(apiCall.invoke())
        } catch (e: Exception) {
            SingleResult.Error(e)
        }
    }

}