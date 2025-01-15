package com.example.vchat.screens.landing

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.vchat.R
import com.example.vchat.nav_graph.VChatNavigationItem

@Composable
fun LandingScreen(navHostController: NavHostController) {
    var scaleTarget by remember {
        mutableStateOf(0f)
    }
    var xTarget by remember {
        mutableStateOf(-1000f)
    }
    val scale by animateFloatAsState(
        targetValue = scaleTarget,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )
    val x by animateFloatAsState(
        targetValue = xTarget,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(key1 = Unit) {
        scaleTarget = 1f
        xTarget = 0f
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.msg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(20.dp, 10.dp)
                        .scale(scale),
                    contentDescription = "image"
                )
                Text(
                    modifier = Modifier
                        .padding(20.dp, 20.dp, 50.dp, 0.dp)
                        .offset(x = x.dp),
                    text = "Connect easily with \n your family and friends \n  over countries",
                    color = Color.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 30.sp,
                    fontFamily = FontFamily(Font(R.font.inter)),
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp, 20.dp, 42.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.blue)),
                onClick = {
                    navHostController.navigate(VChatNavigationItem.LoginScreen.route) {
                        popUpTo(VChatNavigationItem.LandingScreen.route) {
                            inclusive = true
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(0.dp, 7.dp),
                        text = "Start Messaging",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily(Font(R.font.inter)),
                    )
                    Image(
                        modifier = Modifier.size(35.dp),
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "arrow_img"
                    )

                }

            }

        }
    }

}