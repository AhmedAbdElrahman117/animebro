package com.example.animbro.auth.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.animbro.MainActivity
import com.example.animbro.R
import com.example.animbro.auth.AuthBackground
import com.example.animbro.ui.theme.AnimBroTheme


class VerifyEmailSent : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.checked))

            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            val context = LocalContext.current

            AnimBroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthBackground(
                        Modifier.padding(innerPadding),
                        showLogo = false
                    ) {
                        Box {
                            LottieAnimation(
                                modifier = Modifier,
                                composition = composition,
                                progress = {
                                    progress
                                },
                            )
                            SignUpSuccessText(modifier = Modifier.align(Alignment.BottomCenter))
                        }

                        Row(modifier = Modifier.padding(top = 44.dp)) {
                            OpenLoginButton(
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                context,
                            )
                            OpenGmailButton(
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 16.dp),
                                context,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OpenLoginButton(modifier: Modifier = Modifier, context: Context) {
    val activity = LocalActivity.current;
    ElevatedButton(
        onClick = {
            context.startActivity(Intent(context, MainActivity::class.java));
            activity?.finishAffinity();
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color(0xFF16476A)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Login", fontSize = 20.sp)
    }
}

@Composable
private fun OpenGmailButton(modifier: Modifier = Modifier, context: Context) {
    ElevatedButton(
        onClick = {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.`package` = "com.google.android.gm"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color(0xFF16476A)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Open Email", fontSize = 20.sp)
    }
}

@Composable
private fun SignUpSuccessText(modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        append("Verification Email has been sent to ");
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        ) {
            append("ahmedaboelnaga713@gmail.com")
        }
    }
    Text(text, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = modifier)
}
