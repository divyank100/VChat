package com.example.vchat.util

import java.util.Calendar
import java.util.concurrent.TimeUnit

object Util {
    //    const val BASE_URL="https://localhost:3000/"
    const val BASE_URL = "https://dummyjson.com/"

    fun isExpired(prefHelper: PrefHelper): Boolean {
        val previousTimeStamp = prefHelper.getString(AppConstants.currentTime)?.toLong()
        val expiryTime =
            prefHelper.getString(AppConstants.expiryTime)?.toLong()
        if (previousTimeStamp != null) {
            val currentTimeStamp = Calendar.getInstance().time.time
            val difference = currentTimeStamp - previousTimeStamp
            if (expiryTime != null) {
                val isExpired =
                    difference >= (expiryTime - TimeUnit.SECONDS.toMillis(300))
                return isExpired
            } else {
                return true
            }
        } else {
            return true
        }
    }
}