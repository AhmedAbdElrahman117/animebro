package com.example.animbro.auth.screens

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animbro.auth.AuthBackground
import com.example.animbro.auth.CustomDivider
import com.example.animbro.auth.EmailTextField
import com.example.animbro.auth.PasswordTextField
import com.example.animbro.auth.SignWithGoogleButton
import com.example.animbro.auth.repository.ErrorCause
import com.example.animbro.auth.repository.SignUpRepository

@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf("") }
    var usernameError by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf("") }
    var confirmPasswordError by rememberSaveable { mutableStateOf("") }
    var cause by rememberSaveable { mutableStateOf(ErrorCause.none) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        AuthBackground(Modifier.padding(innerPadding), isLoading = isLoading) {
            Text(
                "Sign Up",
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            com.example.animbro.auth.UserNameTextField(
                value = username,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                isError = usernameError.isNotEmpty(),
                errorText = usernameError,
                onValueChange = { username = it }
            )

            EmailTextField(
                value = email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                isError = emailError.isNotEmpty(),
                errorText = emailError,
                onValueChange = { email = it }
            )

            PasswordTextField(
                value = password,
                label = "Password",
                placeHolder = "Enter your Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                isError = passwordError.isNotEmpty(),
                errorText = passwordError,
                onValueChange = { password = it }
            )

            PasswordTextField(
                value = confirmPassword,
                label = "Confirm Password",
                placeHolder = "Confirm your Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                isError = confirmPasswordError.isNotEmpty(),
                errorText = confirmPasswordError,
                onValueChange = { confirmPassword = it }
            )

            com.example.animbro.auth.SignUpButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                email = email,
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                onClick = {
                    emailError = ""
                    usernameError = ""
                    passwordError = ""
                    confirmPasswordError = ""
                    isLoading = true

                    SignUpRepository().signUp(
                        email = email,
                        userName = username,
                        password = password,
                        confirmPassword = confirmPassword,
                        onValidatorError = { message, errorCause ->
                            isLoading = false
                            when (errorCause) {
                                ErrorCause.email -> emailError = message
                                ErrorCause.password -> passwordError = message
                                ErrorCause.userName -> usernameError = message
                                ErrorCause.confirm -> confirmPasswordError = message
                                else -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { message ->
                            isLoading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess()
                        }
                    )



                }
            )

            CustomDivider(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 28.dp)
            )

            SignWithGoogleButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                label = "Sign up with Google"
            )

            com.example.animbro.auth.LoginText(
                modifier = Modifier.padding(top = 32.dp),
                onClick = onNavigateToLogin
            )
        }
    }
}
