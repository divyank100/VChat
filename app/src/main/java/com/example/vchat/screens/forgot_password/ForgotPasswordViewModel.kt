package com.example.vchat.screens.forgot_password

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.models.forgot_password.ForgotPasswordRequest
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val repository: VChatRepository):ViewModel() {

    val forgotPasswordResponse= MutableStateFlow<ApiState<Nothing>?>(null)

    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest,context: Context){
        viewModelScope.launch {
            try {
                forgotPasswordResponse.value=ApiState.Loading
                repository.refreshToken(context)
                val response=repository.forgotPassword(forgotPasswordRequest)
                if (response.isSuccessful){
                    forgotPasswordResponse.value=ApiState.Success(response.body()!!)
                }
                else{
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = errorBody?.let {
                        try {
                            JSONObject(it).getString("message")
                        } catch (e: Exception) {
                            "Unknown error"
                        }
                    } ?: response.message()
                    forgotPasswordResponse.value = ApiState.Error(errorMsg)
                }
            }
            catch (e:Exception){
                e.printStackTrace()
                forgotPasswordResponse.value=ApiState.Error("Exception: ${e.message}")
            }
        }
    }

}