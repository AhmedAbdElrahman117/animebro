package com.example.animbro.anime.components

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.animbro.anime.screens.AnimeListActivity
import com.example.animbro.anime.screens.AnimeListPage
import com.example.animbro.anime.screens.HomeActivity
import com.example.animbro.anime.screens.SearchActivity
import com.example.animbro.ui.theme.AnimBroTheme


sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val activityClass: Class<*>
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home", HomeActivity::class.java)
    object Search : BottomNavItem("search", Icons.Default.Search, "Search", SearchActivity::class.java)
    object AnimeList : BottomNavItem("animelist", Icons.Default.List, "My List", AnimeListActivity::class.java)
    // object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile", ProfileActivity::class.java)
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (Context, Class<*>) -> Unit = { context, activityClass ->
        val intent = Intent(context, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        context.startActivity(intent)
    }
) {
    val context = LocalContext.current
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.AnimeList,
        // BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF4A5BFF)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(context, item.activityClass)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4A5BFF),
                    selectedTextColor = Color(0xFF4A5BFF),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF4A5BFF).copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Home Selected")
@Composable
fun BottomNavigationBarPreview_Home() {
    AnimBroTheme {
        BottomNavigationBar(
            currentRoute = "home",
            onNavigate = { _, _ -> } // No-op for preview
        )
    }
}
