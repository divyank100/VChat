package com.example.vchat.repository

import com.example.vchat.api.VChatApi
import com.example.vchat.models.DummyResponse
import retrofit2.Response
import javax.inject.Inject

class VChatRepository @Inject constructor(private  val vChatApi: VChatApi) {


    suspend fun loginUser(email:String): Response<DummyResponse>{
        return vChatApi.loginUser()
    }



}