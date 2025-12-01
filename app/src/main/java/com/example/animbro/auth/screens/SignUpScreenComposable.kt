package com.example.animbro.auth.screens

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.animbro.R
import com.example.animbro.auth.AuthBackground
import com.example.animbro.auth.CustomDivider
import com.example.animbro.auth.EmailTextField
import com.example.animbro.auth.PasswordTextField
import com.example.animbro.auth.SignWithGoogleButton
import com.example.animbro.auth.repository.ErrorCause
import com.example.animbro.auth.repository.SignUpRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.UserProfileChangeRequest

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
    val activity = LocalActivity.current;

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
                            authResult.result.user!!.updateProfile(
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build()
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    onSignUpSuccess();
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Google login Failed: ${authResult.exception?.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            };

                        } else {
                            Toast.makeText(
                                context,
                                "Google login Failed: ${authResult.exception?.localizedMessage}",
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
            {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }

            com.example.animbro.auth.LoginText(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = onNavigateToLogin
            )
        }
    }
}
