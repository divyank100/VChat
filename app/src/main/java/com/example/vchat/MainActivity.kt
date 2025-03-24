package com.example.vchat

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.login.User
import com.example.vchat.nav_graph.VChatNavigation
import com.example.vchat.ui.theme.VChatTheme
import com.example.vchat.util.AppConstants
import com.example.vchat.util.DataCache
import com.example.vchat.util.SocketHandler
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.zegocloud.uikit.internal.ZegoUIKitLanguage
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.core.invite.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener
import com.zegocloud.uikit.prebuilt.call.event.ZegoCallEndReason
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoTranslationText
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import im.zego.zim.enums.ZIMConnectionEvent
import im.zego.zim.enums.ZIMConnectionState
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefHelper = EntryPointAccessors.fromApplication(
            this,
            PrefHelperEntryPoint::class.java
        ).getPrefHelper()

        setContent {

            val isDarkTheme =
                remember { mutableStateOf(prefHelper.getBoolean(AppConstants.userTheme)) }

            DisposableEffect(Unit) {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    if (key == AppConstants.userTheme) {
                        isDarkTheme.value = prefHelper.getBoolean(AppConstants.userTheme)
                    }
                }
                prefHelper.registerPreferenceChangeListener(listener)
                onDispose {
                    prefHelper.unregisterPreferenceChangeListener(listener)
                }
            }


            VChatTheme(darkTheme = isDarkTheme.value) {
                val navHostController = rememberNavController()
                VChatNavigation(navHostController, isDarkTheme.value)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
        permissionHandling(this)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun initZegoInviteService(appID: Long, appSign: String, userID: String, userName: String) {
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
            application, appID, appSign, userID, userName, callInvitationConfig
        )
        ZegoUIKitPrebuiltCallService.enableFCMPush()

        ZegoUIKitPrebuiltCallService.events.callEvents.callEndListener =
            CallEndListener { callEndReason: ZegoCallEndReason?, jsonObject: String? ->

                Log.d(
                    "CallEndListener",
                    "Call Ended with reason: $callEndReason and json: $jsonObject"
                )
            }

    }

    private fun permissionHandling(activityContext: FragmentActivity) {
        PermissionX.init(activityContext).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason { scope, deniedList ->
                val message =
                    "We need your consent for the following permissions in order to use the offline call function properly"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }.request { allGranted, grantedList, deniedList -> }
    }


    override fun onPause() {
        super.onPause()
        SocketHandler.leaveUserConnection(DataCache.userId) {
            SocketHandler.closeConnection()
        }
    }

    override fun onResume() {
        println("ONRESUME")
        super.onResume()
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        SocketHandler.connectUser()
    }

}

