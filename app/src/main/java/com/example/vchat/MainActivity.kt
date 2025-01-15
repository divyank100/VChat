package com.example.vchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.vchat.nav_graph.VChatNavigation
import com.example.vchat.ui.theme.VChatTheme
import com.example.vchat.util.SocketHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        val mSocket=SocketHandler.getSocket()
//        mSocket.on("eventName") { args ->
//            if (args[0] != null) {
//                val counter = args[0] as Int
//                Log.i("I",counter.toString())
//                runOnUiThread {
//                    // The is where you execute the actions after you receive the data
//                }
//            }
//        }
        setContent {
            VChatTheme {
                val navHostController = rememberNavController()
                VChatNavigation(navHostController)
            }
        }
    }
}

