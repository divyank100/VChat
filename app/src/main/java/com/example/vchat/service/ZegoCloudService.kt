package com.example.vchat.service

import android.app.Application
import android.content.Context
import android.util.Log
import com.zegocloud.uikit.internal.ZegoUIKitLanguage
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.core.invite.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoTranslationText
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import im.zego.zim.enums.ZIMConnectionEvent
import im.zego.zim.enums.ZIMConnectionState
import org.json.JSONObject
import timber.log.Timber


class ZegoCloudService {
    companion object {
        private const val APP_ID = "2114986318"
        private const val APP_SIGN =
            "77c240491b72cf9334c7f4b0d4d4443c68c9854d02645d413ffbb98cf7a9e242"
        private var isInitialized = false

        fun initService(context: Context, userId: String, userName: String) {
            if (isInitialized) {
                return
            }
            val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
            callInvitationConfig.translationText = ZegoTranslationText(ZegoUIKitLanguage.ENGLISH)
            callInvitationConfig.provider =
                ZegoUIKitPrebuiltCallConfigProvider { invitationData: ZegoCallInvitationData? ->
                    ZegoUIKitPrebuiltCallInvitationConfig.generateDefaultConfig(
                        invitationData
                    )
                }
            ZegoUIKitPrebuiltCallService.events.errorEventsListener =
                ErrorEventsListener { errorCode: Int, message: String ->
                    Timber.d("onError() called with: errorCode = [$errorCode], message = [$message]")
                }
            ZegoUIKitPrebuiltCallService.events.invitationEvents.pluginConnectListener =
                SignalPluginConnectListener { state: ZIMConnectionState, event: ZIMConnectionEvent, extendedData: JSONObject ->
                    Timber.d("onSignalPluginConnectionStateChanged() called with: state = [$state], event = [$event], extendedData = [$extendedData$]")
                }
            ZegoUIKitPrebuiltCallService.init(
                context.applicationContext as Application,
                APP_ID.toLong(),
                APP_SIGN,
                userId,
                userName,
                callInvitationConfig
            )
            ZegoUIKitPrebuiltCallService.enableFCMPush()

            ZegoUIKitPrebuiltCallService.events.callEvents.callEndListener =
                CallEndListener { callEndReason: ZegoCallEndReason?, jsonObject: String? ->

                    Log.d(
                        "CallEndListener",
                        "Call Ended with reason: $callEndReason and json: $jsonObject"
                    )
                }
            isInitialized = true
        }


        fun uninitService() {
            ZegoUIKitPrebuiltCallInvitationService.unInit()
        }
    }
}