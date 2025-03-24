package com.example.vchat.screens.requests

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.connectUser.ConnectUserRequest
import com.example.vchat.models.get_connections.UserConnectionResponse
import com.example.vchat.models.requests.GetAllRequestResponse
import com.example.vchat.models.requests.RespondRequestResponse
import com.example.vchat.models.useridRequest.UserIdRequest
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(private val repository: VChatRepository) : ViewModel() {

    val acceptRequestResponse = MutableStateFlow<ApiState<RespondRequestResponse>?>(null)
    val rejectRequestResponse = MutableStateFlow<ApiState<RespondRequestResponse>?>(null)
    val getRequestsResponse = MutableStateFlow<ApiState<GetAllRequestResponse>?>(null)

    fun getRequests(userIdRequest: UserIdRequest,context: Context) {
        viewModelScope.launch {
            try {
                getRequestsResponse.value = ApiState.Loading
                repository.refreshToken(context)
                val response = repository.getRequests(userIdRequest)
                if (response.isSuccessful) {
                    getRequestsResponse.value = ApiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: response.message()
                    getRequestsResponse.value = ApiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getRequestsResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }

    fun acceptConnectionRequest(connectUserRequest: ConnectUserRequest,context: Context) {
        viewModelScope.launch {
            acceptRequestResponse.value = ApiState.Loading
            try {
                repository.refreshToken(context)
                val response = repository.acceptRequest(connectUserRequest)
                if (response.isSuccessful) {
                    acceptRequestResponse.value = ApiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: response.message()
                    acceptRequestResponse.value = ApiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                acceptRequestResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }

    fun rejectConnectionRequest(connectUserRequest: ConnectUserRequest,context: Context) {
        viewModelScope.launch {
            rejectRequestResponse.value = ApiState.Loading
            try {
                repository.refreshToken(context)
                val response = repository.acceptRequest(connectUserRequest)
                if (response.isSuccessful) {
                    rejectRequestResponse.value = ApiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: response.message()
                    rejectRequestResponse.value = ApiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                rejectRequestResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }

}