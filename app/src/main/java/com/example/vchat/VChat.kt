package com.example.vchat

import android.app.Application
import com.example.vchat.util.PrefHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VChat :Application(){
    companion object {
        lateinit var prefHelper: PrefHelper
            private set
    }

    override fun onCreate() {
        super.onCreate()
        prefHelper = PrefHelper(applicationContext)
    }
}