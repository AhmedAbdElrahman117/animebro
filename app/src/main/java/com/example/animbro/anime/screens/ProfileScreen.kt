package com.example.animbro.anime.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import com.example.animbro.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val themePref = ThemeManager(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                ProfileScreen(modifier = Modifier.padding(innerPadding))

            }
        }
    }
}


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth? = null,
    firestore: FirebaseFirestore? = null
) {
    // Safely get Firebase instances only when not in preview
    val firebaseAuth = auth ?: try {
        FirebaseAuth.getInstance()
    } catch (e: IllegalStateException) {
        null // Preview mode - Firebase not initialized
    }

    val firestoreInstance = firestore ?: try {
        FirebaseFirestore.getInstance()
    } catch (e: IllegalStateException) {
        null // Preview mode - Firebase not initialized
    }

    var userName by remember { mutableStateOf("Loading...") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = firebaseAuth?.currentUser?.uid
        if (uid != null && firestoreInstance != null) {
            firestoreInstance.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("username") ?: "Unknown"
                }
                .addOnFailureListener {
                    userName = "Error"
                }
        } else {
            // Preview mode or not authenticated
            userName = "Preview User"
        }
    }

    val context = LocalContext.current
    val themePref = remember { ThemeManager(context) }
    var isDark by remember {
        mutableStateOf(
            themePref.isDarkMode()
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    if (isDark) R.drawable.lightmode else R.drawable.darkmode
                ),
                contentDescription = "dark mode",
                modifier = Modifier
                    .padding(top = 40.dp)
                    .size(50.dp)
                    .clickable {
                        isDark = !isDark
                        themePref.setDarkMode(isDark)
                    }
            )
            Icon(
                painter = painterResource(R.drawable.logout),
                contentDescription = "Logout",
                modifier = Modifier
                    .padding(top = 40.dp)
                    .size(50.dp)
                    .clickable {
                        showLogoutDialog = true
                    }
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.gojo),
                contentDescription = "profile picture",
                modifier = Modifier
                    .size(320.dp)
                    .padding(top = 100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(height = 40.dp))

            Text("UserName : $userName", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)

        }

    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Do You Realy want to Logout ? ") },
            confirmButton = {
                TextButton(
                    onClick = {
                        auth?.signOut()
                        showLogoutDialog = false
                        //navController.navigate("login")  -> تودي لصفحة اللوج ان لو هنستخد النافيجيشن كونترولر
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview( showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}



