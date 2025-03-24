package com.example.vchat.screens.signup

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.MutableState
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
import com.example.vchat.models.login.UserLoginRequest
import com.example.vchat.models.login.UserloginResponse
import com.example.vchat.models.signup.UserSignupRequest
import com.example.vchat.screens.login.LoginViewModel
import com.example.vchat.util.ApiState
import com.example.vchat.util.AppConstants
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navHostController: NavHostController) {

    val viewModel: SignupViewModel = hiltViewModel()
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }
    val signupState by viewModel.signUpUserResponse.collectAsState()
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var oneTapClient: SignInClient? by remember { mutableStateOf(null) }
    var signInRequest: BeginSignInRequest? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
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
            viewModel.signUpUser(
                UserSignupRequest(
                    credential?.displayName ?: "",
                    credential?.id ?: "",
                    credential?.googleIdToken ?: ""
                )
            )
        } catch (e: ApiException) {
            println("One Tap sign-in failed: ${e.message}")
        }
    }


    LaunchedEffect(signupState) {
        when (signupState) {

            is ApiState.Success -> {
                val response = (signupState as ApiState.Success<UserloginResponse>).data
                Toast.makeText(
                    context,
                    response?.message ?: "Signup Successful",
                    Toast.LENGTH_SHORT
                ).show()
                navHostController.popBackStack()
            }

            is ApiState.Error -> {
                val errorMessage = (signupState as ApiState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearSignupState()
            }

            else -> {}

        }
    }

    val scrollState = rememberScrollState()




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
                    .wrapContentHeight()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 30.dp, 10.dp, 0.dp),
                    text = "Signup to your Account",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 20.dp, 10.dp, 0.dp),
                    text = "Create your account to enjoy personalized chatting environment",
                    // Using theme colors
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    visualTransformation = VisualTransformation.None,
                    label = {
                        Text(
                            text = "Username ",
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
                    value = userName,
                    onValueChange = {
                        userName = it
                    }
                )

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
                        .padding(20.dp, 10.dp, 20.dp, 0.dp),
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

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                confirmPasswordVisible = !confirmPasswordVisible
                            },
                            painter = if (!confirmPasswordVisible) painterResource(id = R.drawable.ic_visibility) else painterResource(
                                id = R.drawable.ic_visibility_off
                            ),
                            contentDescription = "icon",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            text = "Confirm Password ",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontFamily = FontFamily(Font(R.font.inter)),
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 10.dp, 20.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                    }
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 45.dp, 20.dp, 2.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    onClick = {
                        if (validateDetails(context, userName, email, password, confirmPassword)) {
                            val signUpUserRequest = UserSignupRequest(userName, email, password)
                            viewModel.signUpUser(signUpUserRequest)
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
                            text = "Sign up",
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
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = interactionSource, indication = null) {
                            navHostController.popBackStack()
                        }
                        .padding(15.dp),
                    fontFamily = FontFamily(Font(R.font.inter)),
                    text = "Already have an account? Login",
                    // Using theme colors with alpha for light gray appearance
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

        // Loading indicator - using theme colors
        if (signupState is ApiState.Loading) {
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

private fun validateDetails(
    context: Context,
    userName: String,
    email: String,
    password: String,
    confirmPassword: String
): Boolean {
    if (userName.isEmpty()) {
        Toast.makeText(context, "Please enter user name", Toast.LENGTH_SHORT)
            .show()
        return false
    }
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
    if (confirmPassword.isEmpty()) {
        Toast.makeText(
            context,
            "Please again enter your password",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
    if (confirmPassword.compareTo(password) != 0) {
        Toast.makeText(
            context,
            "Password and Confirm should be same",
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
    return true
}