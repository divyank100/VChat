package com.example.vchat.screens.login

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.vchat.R
import com.example.vchat.di.PrefHelperEntryPoint
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.nav_graph.VChatNavigationItem
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.example.vchat.util.DataCache
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
    var oneTapClient: SignInClient? by remember { mutableStateOf(null) }
    var signInRequest: BeginSignInRequest? by remember { mutableStateOf(null) }




    val prefHelper = EntryPointAccessors.fromApplication(
        context,
        PrefHelperEntryPoint::class.java
    ).getPrefHelper()

    LaunchedEffect(Unit) {
        viewModel.getDeviceToken()
        oneTapClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
            println("NAME----- ${credential?.displayName}")
            viewModel.loginUser(
                UserLoginRequest(
                    prefHelper.getString(AppConstants.deviceToken) ?: "",
                    credential?.id ?: "",
                    credential?.googleIdToken ?: "",
                )
            )
        } catch (e: ApiException) {
            println("One Tap sign-in failed: ${e.message}")
        }
    }



    LaunchedEffect(loginState) {
        when (loginState) {
            is ApiState.Success -> {
                val response = (loginState as ApiState.Success<UserloginResponse>).data
                prefHelper.putString(AppConstants.userData, Gson().toJson(response.data.user))
                DataCache.accessToken=response.data.accessToken
                DataCache.refreshToken=response.data.refreshToken
                prefHelper.putString(AppConstants.userId, response.data.user.id)
                prefHelper.putString(AppConstants.accessToken, response.data.accessToken)
                prefHelper.putString(AppConstants.refreshToken, response.data.refreshToken)
                prefHelper.putString(
                    AppConstants.expiryTime,
                    java.util.concurrent.TimeUnit.SECONDS.toMillis("3000".toLong()).toString()
                )
                navigateToAllChats(navHostController)
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
            }

            is ApiState.Error -> {
                val errorMessage = (loginState as ApiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearLoginState()
            }

            else -> {}

        }
    }




    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp, 40.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title text - using theme colors
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 30.dp, 10.dp, 0.dp),
                    text = "Login to your Account",
                    // Use MaterialTheme colors instead of direct resource references
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    textAlign = TextAlign.Center,
                )

                // Subtitle text - using theme colors
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp, 10.dp, 0.dp),
                    text = "Welcome Back. Login here",
                    // Use MaterialTheme colors
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )

                // Email field - using theme colors
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    visualTransformation = VisualTransformation.None,
                    label = {
                        Text(
                            text = "E-Mail ",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontFamily = FontFamily(Font(R.font.inter)),
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 50.dp, 20.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.secondary
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
                            contentDescription = "icon",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            text = "Password ",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontFamily = FontFamily(Font(R.font.inter)),
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 10.dp, 20.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        // Using theme colors
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.secondary
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
                    // Using theme colors
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    textAlign = TextAlign.Right
                )


                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 45.dp, 20.dp, 2.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
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
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                        )
                        Image(
                            modifier = Modifier
                                .padding(10.dp, 5.dp)
                                .size(16.dp),
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = "arrow_img",
                            // Optional: colorFilter to tint the icon based on theme
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = interactionSource, indication = null) {
                            navHostController.navigate(VChatNavigationItem.SignupScreen.route)
                        }
                        .padding(15.dp),
                    fontFamily = FontFamily(Font(R.font.inter)),
                    text = "Create new account",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    fontFamily = FontFamily(Font(R.font.inter)),
                    text = "OR",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .clickable {
                                scope.launch {
                                    try {
                                        val signInResult = oneTapClient?.beginSignIn(signInRequest!!)?.await()
                                        val intentSenderRequest = signInResult?.pendingIntent?.intentSender?.let {
                                            IntentSenderRequest.Builder(it).build()
                                        }
                                        if (intentSenderRequest != null) {
                                            launcher.launch(intentSenderRequest)
                                        }
                                    } catch (e: Exception) {
                                        println("No saved credentials found: ${e.message}")
                                    }
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

            }
        }

        if (loginState is ApiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .clickable(enabled = false) {}
                    .then(Modifier.zIndex(10f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    // Using theme colors
                    color = MaterialTheme.colorScheme.primary,
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
