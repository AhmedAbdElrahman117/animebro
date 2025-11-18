package com.example.animbro.auth.screens

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animbro.auth.AuthBackground
import com.example.animbro.auth.EmailTextField
import com.example.animbro.auth.repository.ErrorCause
import com.example.animbro.auth.repository.LoginRepository
import com.example.animbro.ui.theme.AnimBroTheme

class ForgotPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var email by rememberSaveable { mutableStateOf("") };
            var errorMessage by rememberSaveable { mutableStateOf("") };
            var cause by rememberSaveable { mutableStateOf(ErrorCause.none) };

            val activity = LocalActivity.current;
            val context = LocalContext.current;

            AnimBroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthBackground(modifier = Modifier.padding(innerPadding)) {

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Reset Password",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(bottom = 32.dp)
                                    .align(
                                        Alignment.Center
                                    )
                            )
                        }
                        EmailTextField(
                            value = email,
                            errorText = errorMessage,
                            isError = cause == ErrorCause.email,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        ) {
                            email = it
                        }

                        ResetPasswordButton(
                            email = email,
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            cause = ErrorCause.none;
                            errorMessage = "";
                            LoginRepository().sendResetPasswordEmail(
                                email,
                                onValidatorError = { message, errorCause ->
                                    cause = errorCause;
                                    if (cause == ErrorCause.email) {
                                        errorMessage = message;
                                    }
                                },
                                onSuccess = {
                                    activity!!.startActivity(
                                        Intent(
                                            context,
                                            VerifyEmailSent::class.java
                                        ),
                                    );
                                    activity.finishAffinity();
                                },
                                onFailure = {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                                },
                            );
                        };
                    }
                }
            }
        }
    }
}

@Composable
private fun ResetPasswordButton(email: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color(0xFF5683D4)
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        onClick = onClick,
    ) {
        Text(
            "Reset Password",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}
