package com.example.vchat.screens.connected_all_chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.get_connections.UserConnectionResponse
import com.example.vchat.models.useridRequest.UserIdRequest
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ConnectedChatsViewModel @Inject constructor(private  val repository: VChatRepository):ViewModel(){

    val connectPeopleResponse = MutableStateFlow<ApiState<UserConnectionResponse>?>(null)

    fun getConnections(userIdRequest: UserIdRequest) {
        viewModelScope.launch {
            try {
                connectPeopleResponse.value = ApiState.Loading
                val response = repository.getUserConnections(userIdRequest)
                if (response.isSuccessful) {
                    connectPeopleResponse.value = ApiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: response.message()
                    connectPeopleResponse.value = ApiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                connectPeopleResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }

}