package com.example.vchat

import android.app.Application
import com.example.vchat.util.PrefHelper
import com.zegocloud.uikit.ZegoUIKit
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VChat :Application(){
    override fun onCreate() {
        super.onCreate()

    }

}