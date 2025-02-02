package com.example.vchat.screens.login

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.VChat
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.example.vchat.util.PrefHelper
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: VChatRepository,
    private val prefHelper: PrefHelper
) : ViewModel() {

    val loginUserResponse = MutableStateFlow<ApiState<UserloginResponse>?>(null)

    fun getDeviceToken() {
        viewModelScope.launch {
            try {
                val token = Firebase.messaging.token.await()
                if (token != null) {
                    prefHelper.putString(AppConstants.deviceToken, token)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loginUser(request: UserLoginRequest) {
        viewModelScope.launch {
            try {
                loginUserResponse.value = ApiState.Loading
                val response = repository.loginUser(request)
                if (response.isSuccessful) {
                    loginUserResponse.value = ApiState.Success(response.body()!!)
                } else {
                    loginUserResponse.value = ApiState.Error(response.message())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                loginUserResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }
}