package com.example.vchat.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.DummyResponse
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val repository: VChatRepository) : ViewModel() {

    val signUpUserResponse = MutableStateFlow<ApiState<DummyResponse>?>(null)

    fun signUpUser(request: String) {
        viewModelScope.launch {
            try {
                signUpUserResponse.value = ApiState.Loading
                val response = repository.loginUser("")
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
