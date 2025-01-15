package com.example.vchat.screens.login

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vchat.VChat
import com.example.vchat.models.DummyResponse
import com.example.vchat.repository.VChatRepository
import com.example.vchat.util.ApiState
import com.example.vchat.util.PrefHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: VChatRepository,private val prefHelper: PrefHelper): ViewModel() {

    val loginUserResponse = MutableStateFlow<ApiState<DummyResponse>?>(null)

    fun loginUser(request: String) {
        viewModelScope.launch {
            try {
                prefHelper.putString("email",request)
                loginUserResponse.value = ApiState.Loading
                val response = repository.loginUser("")
                if (response.isSuccessful){
                    loginUserResponse.value = ApiState.Success(response.body()!!)
                }
                else{
                    loginUserResponse.value = ApiState.Error(response.message())
                }
                println("PREFHELPER DATAA----- ${prefHelper.getString("email")}")

            } catch (e: Exception) {
                e.printStackTrace()
                loginUserResponse.value = ApiState.Error("Exception: ${e.message}")
            }
        }
    }
}