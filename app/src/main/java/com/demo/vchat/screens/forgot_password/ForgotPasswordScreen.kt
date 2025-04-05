package com.demo.vchat.screens.forgot_password

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.demo.vchat.R
import com.demo.vchat.models.forgot_password.ForgotPasswordRequest
import com.demo.vchat.util.ApiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navHostController: NavHostController) {

    val viewmodel: ForgotPasswordViewModel = hiltViewModel()
    val forgotPasswordState by viewmodel.forgotPasswordResponse.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(forgotPasswordState) {
        when (forgotPasswordState) {
            is ApiState.Success -> {
                val response = (forgotPasswordState as ApiState.Success<Nothing>)
                Toast.makeText(context,"Password has been Reset. Please login again",Toast.LENGTH_SHORT).show()
                navHostController.popBackStack()
            }

            is ApiState.Error -> {
                val error = (forgotPasswordState as ApiState.Error).message
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 30.dp, 10.dp, 0.dp),
                    text = "Reset your Password",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 28.sp,
                    fontFamily = FontFamily(Font(R.font.inter)),
                    textAlign = TextAlign.Center,
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
                        .padding(20.dp, 50.dp, 20.dp, 0.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        // Using theme colors
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    value = email,
                    onValueChange = { email = it }
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                            painter = if (!passwordVisible) painterResource(id = R.drawable.ic_visibility) else painterResource(
                                id = R.drawable.ic_visibility_off
                            ),
                            contentDescription = "icon"
                        )
                    },
                    label = {
                        Text(
                            text = "New Password ",
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
                    onValueChange = { password = it }
                )

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Icon(
                            modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible },
                            painter = if (!confirmPasswordVisible) painterResource(id = R.drawable.ic_visibility) else painterResource(
                                id = R.drawable.ic_visibility_off
                            ),
                            contentDescription = "icon"
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
                        // Using theme colors
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it }
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 45.dp, 20.dp, 2.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    onClick = {
                        if (validateDetails(context, email, password, confirmPassword)) {
                            val forgotPasswordRequest = ForgotPasswordRequest(email, password)
                            viewmodel.forgotPassword(forgotPasswordRequest,context)
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
                            text = "Forgot Password",
                            color = MaterialTheme.colorScheme.onPrimary,
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

                if (forgotPasswordState is ApiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                            .clickable(enabled = false) {}
                            .then(Modifier.zIndex(10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        }
    }


}

private fun validateDetails(
    context: Context,
    email: String,
    password: String,
    confirmPassword: String
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