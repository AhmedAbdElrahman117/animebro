package com.example.animbro.auth.screens

import androidx.compose.material3.MaterialTheme

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animbro.auth.AuthBackground
import com.example.animbro.auth.CustomDivider
import com.example.animbro.auth.EmailTextField
import com.example.animbro.auth.PasswordTextField
import com.example.animbro.auth.SignWithGoogleButton
import com.example.animbro.auth.UserNameTextField
import com.example.animbro.auth.repository.ErrorCause
import com.example.animbro.auth.repository.SignUpRepository
import com.example.animbro.ui.theme.AnimBroTheme

class SignUp : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var userName by rememberSaveable { mutableStateOf("") };
            var email by rememberSaveable { mutableStateOf("") };
            var password by rememberSaveable { mutableStateOf("") };
            var confirmPassword by rememberSaveable { mutableStateOf("") };
            var userNameError by rememberSaveable { mutableStateOf("") };
            var emailError by rememberSaveable { mutableStateOf("") };
            var passwordError by rememberSaveable { mutableStateOf("") };
            var confirmError by rememberSaveable { mutableStateOf("") };
            var cause by rememberSaveable { mutableStateOf(ErrorCause.none) };
            val context = LocalActivity.current;
            var isLoading by remember { mutableStateOf(false) }

            AnimBroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthBackground(
                        Modifier.padding(innerPadding),
                        isLoading = isLoading,
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "SignUp",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(bottom = 32.dp)
                                    .align(
                                        Alignment.Center
                                    )
                            )
                        }
                        UserNameTextField(
                            value = userName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                            errorText = userNameError,
                            isError = cause == ErrorCause.userName,
                        ) {
                            userName = it;
                        }
                        EmailTextField(
                            value = email,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            errorText = emailError,
                            isError = cause == ErrorCause.email,
                            onValueChange = {
                                email = it
                            },
                        )
                        PasswordTextField(
                            value = password,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            placeHolder = "Enter your Password",
                            label = "Password",
                            errorText = passwordError,
                            isError = cause == ErrorCause.password,
                            onValueChange = {
                                password = it;
                            }
                        )
                        PasswordTextField(
                            value = confirmPassword,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            placeHolder = "Confirm your Password",
                            label = "Confirm Password",
                            errorText = confirmError,
                            isError = cause == ErrorCause.confirm,
                            onValueChange = {
                                confirmPassword = it;
                            }
                        )
                        SignupButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp, end = 28.dp, top = 32.dp)
                        ) {
                            userNameError = "";
                            emailError = "";
                            passwordError = "";
                            confirmError = "";
                            cause = ErrorCause.none;
                            isLoading = true;
                            SignUpRepository().signUp(
                                userName = userName,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                onValidatorError = { message, errorCause ->
                                    isLoading = false;
                                    cause = errorCause;
                                    when (cause) {
                                        ErrorCause.userName -> userNameError = message;
                                        ErrorCause.email -> emailError = message;
                                        ErrorCause.password -> passwordError = message;
                                        ErrorCause.confirm -> confirmError = message;
                                        else -> {}
                                    }
                                },
                                onSuccess = {

                                    SignUpRepository().sendVerificationEmail(
                                        user = it,
                                        onFailure = {
                                            isLoading = false;
                                            Toast.makeText(
                                                context,
                                                "Failed to send email",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onSuccess = {
                                            isLoading = false;
                                            Toast.makeText(
                                                context,
                                                "Email Sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(
                                                Intent(
                                                    context,
                                                    VerifyEmailSent::class.java
                                                )
                                            );
                                            finishAffinity();
                                        },
                                    );
                                },
                                onFailure = {
                                    isLoading = false;
                                    Toast.makeText(
                                        context,
                                        it,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                            );
                        }
                        CustomDivider(
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 28.dp)
                        )
                        SignWithGoogleButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp),
                            "Sign Up With Google"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SignupButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        onClick = onClick,
    ) {
        Text(
            "SignUp",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}



