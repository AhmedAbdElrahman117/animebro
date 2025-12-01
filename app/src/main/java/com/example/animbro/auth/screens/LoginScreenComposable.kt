package com.example.animbro.auth.screens

import com.example.animbro.auth.viewModel.AuthViewModel
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.animbro.R
import com.example.animbro.auth.AuthBackground
import com.example.animbro.auth.CustomDivider
import com.example.animbro.auth.EmailTextField
import com.example.animbro.auth.PasswordTextField
import com.example.animbro.auth.SignWithGoogleButton
import com.example.animbro.auth.repository.ErrorCause
import com.example.animbro.auth.repository.LoginRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf("") }
    var cause by rememberSaveable { mutableStateOf(ErrorCause.none) }
    val context = LocalContext.current
    val activity = LocalActivity.current
    var isLoading by remember { mutableStateOf(false) }
    val myViewModel: AuthViewModel = hiltViewModel()

    // Google Sign-In Launcher
    val firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential =
                    com.google.firebase.auth.GoogleAuthProvider.getCredential(account.idToken, null)

                isLoading = true
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { authResult ->
                        isLoading = false
                        if (authResult.isSuccessful) {
                            Toast.makeText(context, "Google Login Success", Toast.LENGTH_LONG)
                                .show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(
                                context,
                                "Firebase Auth Failed: ${authResult.exception?.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In Failed: ${e.statusCode}", Toast.LENGTH_LONG)
                    .show()
            }
        }



    Scaffold { innerPadding ->
        AuthBackground(Modifier.padding(innerPadding), isLoading = isLoading) {
            Text(
                "Login",
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            EmailTextField(
                value = email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isError = cause == ErrorCause.email,
                errorText = emailError,
                onValueChange = { email = it }
            )

            PasswordTextField(
                value = password,
                label = "Password",
                placeHolder = "Enter your Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                isError = cause == ErrorCause.password,
                errorText = passwordError,
                onValueChange = { password = it }
            )

            Text(
                "Forgot Password?",
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                    .clickable(onClick = onNavigateToForgotPassword),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700
            )

            com.example.animbro.auth.LoginButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                email = email,
                password = password,
                onClick = {
                    cause = ErrorCause.none
                    emailError = ""
                    passwordError = ""
                    isLoading = true

                    LoginRepository().login(
                        email,
                        password,
                        { message, errorCause ->
                            cause = errorCause
                            isLoading = false
                            when (errorCause) {
                                ErrorCause.email -> {
                                    emailError = message;
                                };
                                ErrorCause.password -> {
                                    passwordError = message;
                                };
                                else -> {
                                    cause = ErrorCause.none;
                                    emailError = ""
                                    passwordError = ""
                                }
                            }
                        },
                        {
                            myViewModel.sync();
                            isLoading = false
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        },
                        {
                            isLoading = false;
                            Toast.makeText(
                                context,
                                it,
                                Toast.LENGTH_LONG
                            ).show()
                        },
                    )

                }
            )

            CustomDivider(
                modifier = Modifier.padding(vertical = 28.dp, horizontal = 28.dp)
            )

            SignWithGoogleButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                label = "Login with Google",
            )
            {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }

            com.example.animbro.auth.SignUpText(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = onNavigateToSignUp
            )
        }
    }
}