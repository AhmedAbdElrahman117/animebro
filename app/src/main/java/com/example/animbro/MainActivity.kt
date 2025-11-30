package com.example.animbro

import androidx.compose.material3.MaterialTheme

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.animbro.auth.screens.SignUp
import com.example.animbro.navigation.AppNavigation
import com.example.animbro.ui.theme.AnimBroTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup notification channel
        notificationChannel()

        setContent {
            AnimBroTheme {
                AppNavigation()
            }
        }
    }

    private fun notificationHanlder(): ActivityResultLauncher<String> {
        val handler = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                sendNotificationMain()
            }
        }
        return handler
    }

    private fun notificationChannel() {
        val channel =
            NotificationChannel("30", "Notifications", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Main Notifications"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    private fun sendNotificationMain() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.animebro_logo)
        val builder = NotificationCompat.Builder(this, "30")
        builder.setSmallIcon(R.drawable.animebro_logo)
            .setContentTitle("What's New")
            .setContentText("See if something came up !!")
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        NotificationManagerCompat
            .from(this)
            .notify(10, builder.build())
    }
}


@Composable
fun SignUpText(modifier: Modifier = Modifier) {
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
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
                                color = errorColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                            pressedStyle = SpanStyle(
                                background = onSurfaceVariant.copy(alpha = 0.2f)
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
            containerColor = MaterialTheme.colorScheme.primary
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
    val primaryColor = MaterialTheme.colorScheme.primary
    val text by remember {
        mutableStateOf(buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = primaryColor,
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





