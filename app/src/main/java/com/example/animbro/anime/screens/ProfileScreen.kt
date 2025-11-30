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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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

import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.animbro.anime.services.ProfileViewModel
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.animbro.anime.components.AnimeCard
import androidx.compose.runtime.collectAsState
import com.example.animbro.domain.models.Anime
import com.example.animbro.anime.components.StatusUpdateDialog

@AndroidEntryPoint
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
    navController: NavController? = null,
    auth: FirebaseAuth? = null,
    firestore: FirebaseFirestore? = null,
    viewModel: ProfileViewModel = hiltViewModel()
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

    val isDialogVisible by viewModel.isDialogVisible.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()

    if (isDialogVisible) {
        StatusUpdateDialog(
            currentStatus = currentCategory,
            onDismissRequest = { viewModel.dismissDialog() },
            onStatusSelected = { category ->
                viewModel.updateAnimeStatus(category)
            },
            onRemoveClick = {
                viewModel.removeAnimeFromList()
            }
        )
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
                    .size(30.dp)
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
                    .size(30.dp)
                    .clickable {
                        showLogoutDialog = true
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Image(
                painter = painterResource(if (isDark) R.drawable.person_white else R.drawable.person),
                contentDescription = "profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            Text(userName, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)

            Spacer(modifier = Modifier.height(height = 32.dp))

            // Favorites Row
            FavRow(
                viewModel = viewModel,
                onAnimeClick = { animeId ->
                    navController?.let {
                        // Navigate to anime details using the navigation controller
                        it.navigate("anime_details/$animeId")
                    }
                },
                onAddClick = { anime ->
                    viewModel.openStatusDialog(anime)
                },
                onFavClick = { anime ->
                    viewModel.toggleFavorite(anime)
                }
            )
        }

    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Do You Really want to Logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        firebaseAuth?.signOut()
                        showLogoutDialog = false
                        // Navigate to login screen
                        navController?.let {
                            it.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
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



@Composable
fun FavRow(
    viewModel: ProfileViewModel,
    onAnimeClick: (Int) -> Unit,
    onAddClick: (Anime) -> Unit,
    onFavClick: (Anime) -> Unit
) {
    val favorites by viewModel.favoriteAnime.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Favorite Anime",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            Text(
                text = "No favorites yet",
                modifier = Modifier.padding(start = 8.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
                items(favorites) { anime ->
                    AnimeCard(
                        anime = anime,
                        onClick = { onAnimeClick(anime.id) },
                        onAddClick = { onAddClick(anime) },
                        onFavClick = { onFavClick(anime) }
                    )
                }
            }
        }
    }
}




@Preview( showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}



