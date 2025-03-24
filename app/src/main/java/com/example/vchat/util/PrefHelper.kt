package com.example.vchat.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

//@Singleton
class PrefHelper(context: Context) {

    private val prefsName = "shared_preferences"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()

    fun putString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPref.getString(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun registerPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun clear() {
        editor.clear().apply()
    }
}
