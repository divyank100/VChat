package com.example.vchat.util

import java.util.Calendar
import java.util.concurrent.TimeUnit

object Util {
    //    const val BASE_URL="https://localhost:3000/"
//    const val BASE_URL = "http://10.0.2.2/"
//    const val BASE_URL = "http://172.20.10.3:5762/"
    const val BASE_URL = "http://192.168.0.104:5762/"

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