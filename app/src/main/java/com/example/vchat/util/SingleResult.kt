package com.example.vchat.util

import java.io.IOException

sealed class SingleResult<out R> {

    data class Success<out T>(val data: T) : SingleResult<T>()
    data class Error(val exception: Exception) : SingleResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]" }
    }
}


/**
 * `true` if [SingleResult] is of type [Success] & holds non-null [Success.data].
 */
val SingleResult<*>.succeeded
    get() = this is SingleResult.Success && data != null

fun <T> SingleResult<T>.successOr(fallback: T): T {
    return (this as? SingleResult.Success<T>)?.data ?: fallback
}

val <T> SingleResult<T>.data: T?
    get() = (this as? SingleResult.Success)?.data

fun <T> SingleResult<T>.successOrThrow(): SingleResult.Success<T> {
    return (this as? SingleResult.Success<T>) ?: run {
        val error = (this as? SingleResult.Error)?.exception
        throw error ?: IOException("Required SingleResult.Success but was $this")
    }
}
