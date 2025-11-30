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
import com.example.animbro.auth.EmailTextField

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        AuthBackground(Modifier.padding(innerPadding), isLoading = isLoading) {
            Text(
                "Reset Password",
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Enter your email address and we'll send you a link to reset your password",
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                fontSize = 14.sp
            )

            EmailTextField(
                value = email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                isError = emailError.isNotEmpty(),
                errorText = emailError,
                onValueChange = { email = it }
            )

            com.example.animbro.auth.ResetPasswordButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                email = email,
                onClick = {
                    emailError = ""
                    isLoading = true


                }
            )

            com.example.animbro.auth.BackToLoginText(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = onNavigateBack
            )
        }
    }
}
