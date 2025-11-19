package com.example.animbro.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animbro.R
import com.example.animbro.auth.service.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun Background(modifier: Modifier) {
    val conf = LocalConfiguration.current;
    val height = conf.screenHeightDp * 0.46;
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.auth_background),
            contentDescription = "Anime Background",
            modifier = modifier,
            contentScale = ContentScale.FillBounds,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(height.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.97f),
                        )
                    )
                )
        )
    }
}

@Composable
fun ForegroundLayer(
    modifier: Modifier = Modifier,
    showLogo: Boolean = true,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize(),
        colors = CardDefaults.cardColors().copy(containerColor = Color.White.copy(alpha = 0.65f)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        content = {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                if (showLogo)
                    Logo(
                        modifier
                            .padding(28.dp)
                    )
                content()
            }
        },
    )
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.animebro_logo),
            contentDescription = "AnimeBroLogo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(100.dp),
        )
    }
}


@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String = "",
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        placeholder = {
            Text("Enter your Email")
        },
        label = {
            Text("Email")
        },
        leadingIcon = {
            Icon(
                Icons.Default.Email,
                contentDescription = "Email Icon",
                tint = if (isError) Color(0xFFcc0000) else LocalContentColor.current
            )
        },
        supportingText = if (isError) {
            {
                Text(errorText)
            }
        } else null,
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        onValueChange = onValueChange
    )
}


@Composable
fun UserNameTextField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String = "",
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        placeholder = {
            Text("Enter your UserName")
        },
        label = {
            Text("Username")
        },
        leadingIcon = {
            Icon(
                Icons.Default.Person,
                contentDescription = "Email Icon",
                tint = if (isError) Color(0xFFcc0000) else LocalContentColor.current
            )
        },
        supportingText = if (isError) {
            {
                Text(errorText)
            }
        } else null,
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        onValueChange = onValueChange
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    placeHolder: String,
    isError: Boolean = false,
    errorText: String = "",
    onValueChange: (String) -> Unit
) {
    var isShowPassword by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        value = value,
        placeholder = {
            Text(placeHolder)
        },
        label = {
            Text(label)
        },
        leadingIcon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Password Icon",
                tint = if (isError) Color(0xFFcc0000) else LocalContentColor.current
            )
        },
        visualTransformation = if (isShowPassword) VisualTransformation.None
        else PasswordVisualTransformation('*'),
        trailingIcon = {
            IconButton(onClick = {
                isShowPassword = !isShowPassword;
            }) {
                Crossfade(targetState = isShowPassword) { show ->
                    val icon = if (show) R.drawable.eye_solid else R.drawable.eye_slash;
                    val description = if (show) "Show Password" else "Hide Password"
                    Icon(
                        painterResource(icon),
                        modifier = Modifier.padding(6.dp),
                        contentDescription = description
                    )
                }

            }
        },
        isError = isError,
        supportingText = if (isError) {
            {
                Text(errorText)
            }
        } else null,
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        onValueChange = onValueChange,
    )
}

@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    showLogo: Boolean = true,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Box(modifier) {
        Background(Modifier.fillMaxSize());
        ForegroundLayer(
            content = content,
            showLogo = showLogo
        )
    }
}

@Composable
fun CustomDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors()
                .copy(containerColor = Color.Black),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        ) { }

        Text(
            "or",
            modifier = Modifier.padding(horizontal = 12.dp),
            color = Color(0xFFF9A620),
        )

        Card(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors()
                .copy(containerColor = Color.Black),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp),
        ) { }
    }
}

@Composable
fun SignWithGoogleButton(modifier: Modifier = Modifier, label: String) {
    val context = LocalActivity.current;

    val launcher = googleAuthLauncher(context)


    ElevatedButton(
        onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context!!.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso);

            launcher.launch(googleSignInClient.signInIntent);
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color(0xFF16476A)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = "Google Image",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(28.dp)
            )
            Text(
                label,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun googleAuthLauncher(context: Activity?): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        AuthService().signWithGoogle(result, context!!);
    }
    return launcher
}