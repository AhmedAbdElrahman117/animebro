package com.example.animbro.auth

import androidx.compose.material3.MaterialTheme

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Helper Composables for Auth Screens

@Composable
fun SignUpText(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error

    val text by remember {
        mutableStateOf(buildAnnotatedString {
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
                    onClick()
                },
            )
            {
                append("Sign up")
            }
        })
    }
    Text(text, modifier = modifier)
}

@Composable
fun LoginText(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val context = LocalActivity.current;
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val text by remember {
        mutableStateOf(
            buildAnnotatedString {
                append("Already have an account? ")

                withLink(
                    LinkAnnotation.Clickable(
                        "l",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = primaryColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                            pressedStyle = SpanStyle(
                                background = onSurfaceVariant.copy(alpha = 0.2f)
                            ),
                        )
                    ) {
                        onClick()
                    },
                )
                {
                    append("Login")
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
fun SignUpButton(
    modifier: Modifier = Modifier,
    email: String,
    username: String,
    password: String,
    confirmPassword: String,
    onClick: () -> Unit
) {
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
            "Sign Up",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun ResetPasswordButton(
    modifier: Modifier = Modifier,
    email: String,
    onClick: () -> Unit
) {
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
            "Send Reset Email",
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@Composable
fun BackToLoginText(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = "Back to Login",
        modifier = modifier.clickable(onClick = onClick),
        color = Color(0xFF5683D4),
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
    )
}
