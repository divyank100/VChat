package com.demo.vchat.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.vchat.models.login.UserloginResponse
import com.demo.vchat.models.signup.UserSignupRequest
import com.demo.vchat.repository.VChatRepository
import com.demo.vchat.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val repository: VChatRepository) : ViewModel() {

    val signUpUserResponse = MutableStateFlow<ApiState<UserloginResponse>?>(null)

    fun signUpUser(request: UserSignupRequest) {
        viewModelScope.launch {
            try {
                signUpUserResponse.value = ApiState.Loading
                val response = repository.signUpUser(request)
                if (response.isSuccessful) {
                    signUpUserResponse.value = ApiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: response.message()
                    signUpUserResponse.value = ApiState.Error(errorMsg)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                signUpUserResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }

    fun clearSignupState() {
        signUpUserResponse.value = null
    }
}
