package com.example.vchat.screens.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.vchat.R
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.nav_graph.VChatNavigationItem
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.google.gson.Gson
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navHostController: NavHostController) {

    val viewModel: LoginViewModel = hiltViewModel()
    val loginState by viewModel.loginUserResponse.collectAsState()
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    var passwordVisible by remember {
        mutableStateOf(false)
    }


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()


    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()

    LaunchedEffect(Unit) {
        viewModel.getDeviceToken()
    }


    LaunchedEffect(loginState) {
        when (loginState) {
            is ApiState.Success -> {
                val response = (loginState as ApiState.Success<UserloginResponse>).data
                prefHelper.putString(AppConstants.userData, Gson().toJson(response.data.user))
                prefHelper.putString(AppConstants.userId, response.data.user.id)
                prefHelper.putString(AppConstants.accessToken, response.data.accessToken)
                prefHelper.putString(AppConstants.refreshToken, response.data.refreshToken)
                prefHelper.putString(
                    AppConstants.expiryTime,
                    java.util.concurrent.TimeUnit.SECONDS.toMillis("1000000".toLong()).toString()
                )
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                navigateToAllChats(navHostController)
            }

            is ApiState.Error -> {
                val errorMessage = (loginState as ApiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            else -> {}

        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
//                .background(Color.Black)
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp, 40.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 30.dp, 10.dp, 0.dp),
                    text = "Login to your Account",
                    color = colorResource(id = R.color.blue),
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter)),
//                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp, 10.dp, 0.dp),
                    text = "Welcome Back. Login here",
                    color = Color.Black,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    visualTransformation = VisualTransformation.None,
                    label = {
                        Text(
                            text = "E-Mail ",
                            color = Color.LightGray,
                            fontFamily = FontFamily(Font(R.font.inter)),
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 50.dp, 20.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.blue),
                        focusedLabelColor = colorResource(
                            id = R.color.blue
                        ),
                        containerColor = colorResource(
                            id = R.color.light_blue
                        )
                    ),
                    value = email,
                    onValueChange = {
                        email = it
                    }
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                passwordVisible = !passwordVisible
                            },
                            painter = if (!passwordVisible) painterResource(id = R.drawable.ic_visibility) else painterResource(
                                id = R.drawable.ic_visibility_off
                            ),
                            contentDescription = "icon"
                        )
                    },
                    label = {
                        Text(
                            text = "Password ",
                            color = Color.LightGray,
                            fontFamily = FontFamily(Font(R.font.inter)),
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 10.dp, 20.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.blue),
                        focusedLabelColor = colorResource(
                            id = R.color.blue
                        ),
                        containerColor = colorResource(
                            id = R.color.light_blue
                        )
                    ),
                    value = password,
                    onValueChange = {
                        password = it
                    }
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = interactionSource, indication = null) {
                            navHostController.navigate(VChatNavigationItem.ForgotPasswordScreen.route)
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    text = " Forgot password ?",
                    color = colorResource(id = R.color.blue),
                    fontFamily = FontFamily(Font(R.font.inter)),
                    textAlign = TextAlign.Right
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 45.dp, 20.dp, 2.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.blue)),
                    onClick = {
                        if (validateDetails(context, email, password)) {
                            viewModel.loginUser(
                                UserLoginRequest(
                                    prefHelper.getString(AppConstants.deviceToken) ?: "",
                                    email,
                                    password
                                )
                            )
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
                            fontFamily = FontFamily(Font(R.font.inter)),
                            text = "Sign in",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                        Image(
                            modifier = Modifier
                                .padding(10.dp, 5.dp)
                                .size(16.dp),
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "arrow_img"
                        )

                    }

                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = interactionSource, indication = null) {
                            navHostController.navigate(VChatNavigationItem.SignupScreen.route)
                        }
                        .padding(20.dp),
                    fontFamily = FontFamily(Font(R.font.inter)),
                    text = "Create new account",
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 40.dp, 20.dp, 10.dp),
                    fontFamily = FontFamily(Font(R.font.inter)),
                    text = "Or continue with",
                    color = colorResource(id = R.color.blue),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 10.dp, 10.dp, 0.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Card(
                        modifier = Modifier
                            .width(
                                100.dp
                            )
                            .height(80.dp)
                            .padding(10.dp, 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(7.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp),
                                contentDescription = "google_image"
                            )
                        }

                    }

                }


            }
        }
        if (loginState is ApiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.blue),
                    strokeWidth = 4.dp
                )
            }
        }
    }
}

fun navigateToAllChats(navHostController: NavHostController) {
    navHostController.navigate(VChatNavigationItem.ConnectedAllChats.route) {
        popUpTo(VChatNavigationItem.LoginScreen.route) {
            inclusive = true
        }
    }
}

private fun validateDetails(
    context: Context,
    email: String,
    password: String,
): Boolean {
    if (email.isEmpty()) {
        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT)
            .show()
        return false
    }
    if (password.isEmpty()) {
        Toast.makeText(
            context,
            "Please enter your password",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
    return true
}
