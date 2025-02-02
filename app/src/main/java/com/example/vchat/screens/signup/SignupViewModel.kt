package com.example.vchat.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val repository: VChatRepository) : ViewModel() {

    val signUpUserResponse = MutableStateFlow<ApiState<UserloginResponse>?>(null)

    fun signUpUser(request: UserLoginRequest) {
        viewModelScope.launch {
            try {
                signUpUserResponse.value = ApiState.Loading
                val response = repository.signUpUser(request)
                if (response.isSuccessful) {
                    signUpUserResponse.value = ApiState.Success(response.body()!!)
                } else {
                    signUpUserResponse.value = ApiState.Error(response.message())
                }

            } catch (e: Exception) {
                e.printStackTrace()
                signUpUserResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }
}
