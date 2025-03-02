package com.example.vchat.util

sealed class ApiState<out T> {
    object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Error<out T>(val message: String) : ApiState<T>()
}