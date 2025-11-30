package com.example.animbro

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.animbro.anime.screens.HomeActivity
import com.example.animbro.auth.AuthBackground
import com.example.animbro.auth.CustomDivider
import com.example.animbro.auth.EmailTextField
import com.example.animbro.auth.PasswordTextField
import com.example.animbro.auth.SignWithGoogleButton
import com.example.animbro.auth.repository.ErrorCause
import com.example.animbro.auth.repository.LoginRepository
import com.example.animbro.auth.screens.ForgotPassword
import com.example.animbro.auth.screens.SignUp
import com.example.animbro.ui.theme.AnimBroTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var email by rememberSaveable { mutableStateOf("") };
            var password by rememberSaveable { mutableStateOf("") };
            var emailError by rememberSaveable { mutableStateOf("") };
            var passwordError by rememberSaveable { mutableStateOf("") };
            var cause by rememberSaveable { mutableStateOf(ErrorCause.none) };
            val context = LocalContext.current;
            var isLoading by remember { mutableStateOf(false) }

            AnimBroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthBackground(Modifier.padding(innerPadding), isLoading = isLoading) {
                        Text(
                            "Login",
                            modifier = Modifier
                                .padding(vertical = 24.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        );
                        EmailTextField(
                            value = email,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            isError = cause == ErrorCause.email,
                            errorText = emailError,
                            onValueChange = {
                                email = it
                            }
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
                            onValueChange = {
                                password = it;
                            }
                        )
                        ForgotPassword(
                            Modifier
                                .padding(
                                    top = 4.dp, bottom = 20.dp,
                                    start = 20.dp, end = 20.dp
                                )
                                .clickable(onClick = {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            ForgotPassword::class.java
                                        )
                                    );

                                }),
                        );
                        LoginButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp),
                            email = email,
                            password = password,
                            onClick = {
                                cause = ErrorCause.none;
                                emailError = ""
                                passwordError = ""
                                isLoading = true;
                                LoginRepository().login(
                                    email,
                                    password,
                                    { message, errorCause ->
                                        cause = errorCause;
                                        isLoading = false;
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
                                        isLoading = false;
                                        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                                        context.startActivity(
                                            Intent(
                                                context,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()

                                    },
                                    {
                                        isLoading = false;
                                        Toast.makeText(
                                            context,
                                            it,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    },
                                );

                            },
                        )
                        CustomDivider()
                        SignWithGoogleButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp),
                            "Login With Google"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp)
                        ) {
                            SignUpText(modifier = Modifier.align(alignment = Alignment.Center))
                        }

                    }
                }
            }
        }

    }


    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}


@Composable
fun SignUpText(modifier: Modifier = Modifier) {
    val context = LocalActivity.current;

    val text by remember {
        mutableStateOf(
            buildAnnotatedString {
                append("Don't have an account yet? ")

                withLink(
                    LinkAnnotation.Clickable(
                        "s",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Color(0xFFBF092F),
                                textDecoration = TextDecoration.Underline,
                            ),
                            pressedStyle = SpanStyle(
                                background = Color.Gray.copy(alpha = 0.2f)
                            ),
                        )
                    ) {
                        context?.startActivity(Intent(context, SignUp::class.java));
                    },
                )
                {
                    append("Sign up")
                }
            },
        )
    }

    Text(text, modifier = modifier);
}


@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onClick: () -> Unit
) {
    val context = LocalActivity.current
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
            "Login",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun ForgotPassword(modifier: Modifier = Modifier) {
    val text by remember {
        mutableStateOf(buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = Color(0xFF5683D4),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.W700, fontSize = 16.sp,
                )

            ) {
                append("Forgot Password?")
            }
        });
    }
    Text(
        modifier = modifier,
        text = text,
    )
}





