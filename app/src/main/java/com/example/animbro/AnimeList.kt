package com.example.animbro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animbro.ui.theme.AnimBroTheme
import kotlinx.coroutines.launch

// Data class for anime items
data class AnimeItem(
    val id: Int,
    val title: String,
    val episodes: Int,
    val type: String,
    val rating: Float,
    val imageUrl: String
)

class AnimeList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimBroTheme {
                UserListScreen()
            }
        }
    }
}

@Composable
fun UserListScreen() {
    val tabs = listOf("Watching", "Completed", "Dropped", "Pending")
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image - Painter resource must be called directly
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with user info
                UserHeader()

                // Status Tabs
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = Color(0xFF4A5BFF)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = tab,
                                    fontSize = 14.sp,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.Black
                                )
                            }
                        )
                    }
                }

                // Horizontal Pager for swipeable content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    // Only load data for current page
                    key(page) {
                        AnimeListPage(tabName = tabs[page])
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeListPage(tabName: String) {
    // Sample data - replace with actual data source
    val animeList = remember(tabName) {
        List(18) { index ->
            AnimeItem(
                id = index + tabName.hashCode(), // Unique ID per tab
                title = "Attack on Titan",
                episodes = 25,
                type = "TV",
                rating = 8.5f,
                imageUrl = "https://cdn.myanimelist.net/images/anime/10/47347.jpg"
            )
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = animeList,
            key = { anime -> anime.id }
        ) { anime ->
            AnimeListItem(anime = anime)
        }
    }
}

@Composable
fun UserHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Logo
        Image(
            painter = painterResource(id = R.drawable.animebro_logo),
            contentDescription = "AnimeBro Logo",
            modifier = Modifier
                .height(60.dp)
                .width(180.dp),
            contentScale = ContentScale.Fit
        )

        // Profile Section
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.acc_ic),
                contentDescription = "Profile",
                modifier = Modifier
                    .clickable { /* TODO: Navigate to profile */ }
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AnimeListItem(anime: AnimeItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { /* TODO: make it go to the anime details*/},
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Anime Poster
            Box(
                modifier = Modifier
                    .width(85.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = anime.imageUrl,
                    contentDescription = anime.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.poster_sample),
                    error = painterResource(id = R.drawable.poster_sample)
                )
            }

            // Anime Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title
                Text(
                    text = anime.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Additional Info
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Episodes: ${anime.episodes}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = anime.type,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Rating/Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = anime.rating.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Edit Status Button
            IconButton(
                onClick = { /* TODO: Show status edit dialog */ },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.saved_ic),
                    contentDescription = "Edit Status",
                    tint = Color(0xFF4A5BFF),
                    modifier = Modifier.size(24.dp)


                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListScreenPreview() {
    AnimBroTheme {
        UserListScreen()
    }
}
