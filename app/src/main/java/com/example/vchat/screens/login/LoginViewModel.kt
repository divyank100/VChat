package com.example.vchat.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.DummyResponse
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiResponse
import com.example.vchat.util.ApiResponseStatus
import com.example.vchat.util.ApiState
import com.example.vchat.util.SingleResult
import com.example.vchat.util.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: VChatRepository) : ViewModel() {

    val loginUserResponse = MutableStateFlow<ApiState<DummyResponse>?>(null)

    fun loginUser(request: String) {
        viewModelScope.launch {
            try {
                loginUserResponse.value = ApiState.Loading
                val response = repository.loginUser("")
                if (response.isSuccessful){
                    loginUserResponse.value = ApiState.Success(response.body()!!)
                }
                else{
                    loginUserResponse.value = ApiState.Error(response.message())
                }

            } catch (e: Exception) {
                e.printStackTrace()
                loginUserResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }
}