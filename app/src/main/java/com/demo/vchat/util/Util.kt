package com.demo.vchat.util

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object Util {
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

    fun formatTimestamp(timestamp: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC parsing

            val outputFormat =
                SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format with AM/PM
            outputFormat.timeZone = TimeZone.getDefault() // Convert to local timezone

            val date: Date = inputFormat.parse(timestamp) ?: return "Just now"
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return "Just now"
        }
        return "Just now"

    }
}