package com.example.animbro.anime.screens

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
import com.example.animbro.R
import com.example.animbro.ui.theme.AnimBroTheme
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animbro.anime.services.AnimeListViewModel
import com.example.animbro.anime.services.AnimeListViewModelFactory
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.remote.Endpoints
import com.example.animbro.repositories.AnimeRepositoryImp
import com.example.animbro.domain.models.Anime



class AnimeList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimBroTheme {
                val repository = remember {
                    val api: Endpoints = getApiInstance()
                    val dao: WatchListDAO = getDaoInstance()
                    AnimeRepositoryImp(api, dao)
                }

                val viewModel: AnimeListViewModel = viewModel(
                    factory = AnimeListViewModelFactory(repository)
                )

                UserListScreen(viewModel)
            }
        }
    }

    private fun getApiInstance(): Endpoints {
        val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
            level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
        }

        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(com.example.animbro.data.remote.BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create(com.google.gson.Gson()))
            .build()

        return retrofit.create(Endpoints::class.java)
    }

    private fun getDaoInstance(): WatchListDAO {
        val db = androidx.room.Room
            .databaseBuilder(
                this,
                com.example.animbro.data.local.AppDatabase::class.java,
                "animbro_db"
            )
            .fallbackToDestructiveMigration()
            .build()

        return db.watchListDao()
    }
}

@Composable
fun UserListScreen(viewModel: AnimeListViewModel) {
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
                val tabName = tabs[page]
                val animeList by viewModel.getListByCategory(tabName).collectAsState()

                // Only load data for current page
                key(page) {
                    AnimeListPage(animeList = animeList)
                }
            }
        }
    }
}

@Composable
fun AnimeListPage(animeList: List<Anime>) {
    // Hoist painter resources to avoid repeated lookups
    val placeholderPainter = painterResource(id = R.drawable.poster_sample)
    val errorPainter = painterResource(id = R.drawable.poster_sample)
    val savedIconPainter = painterResource(id = R.drawable.saved_ic)

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = animeList,
            key = { anime -> anime.id }
        ) { anime ->
            AnimeListItem(
                anime = anime,
                placeholderPainter = placeholderPainter,
                errorPainter = errorPainter,
                savedIconPainter = savedIconPainter
            )
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
fun AnimeListItem(
    anime: Anime,
    placeholderPainter: androidx.compose.ui.graphics.painter.Painter,
    errorPainter: androidx.compose.ui.graphics.painter.Painter,
    savedIconPainter: androidx.compose.ui.graphics.painter.Painter
) {
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
                    model = anime.image?.large ?: anime.image?.medium,
                    contentDescription = anime.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter
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
                            text = anime.status, // Using status instead of type
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
                            text = if (anime.score != null && anime.score > 0) anime.score.toString() else "N/A",
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
                    painter = savedIconPainter,
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
        UserListScreen(viewModel = PreviewAnimeListViewModel())
    }
}

private fun PreviewAnimeListViewModel(): AnimeListViewModel {
    val mockRepository = object : com.example.animbro.domain.repository.AnimeRepository {
        override suspend fun getTopRatedAnime(limit: Int) = emptyList<Anime>()
        override suspend fun getPopularAnime(limit: Int) = emptyList<Anime>()
        override suspend fun getUpcomingAnime(limit: Int) = emptyList<Anime>()
        override suspend fun getFavouritesAnime(limit: Int) = emptyList<Anime>()
        override suspend fun searchAnime(query: String) = emptyList<Anime>()
        override suspend fun getAnimeDetails(id: Int) = null
        override fun getWatchListByCategory(category: String) = kotlinx.coroutines.flow.MutableStateFlow(
            List(5) {
                Anime(
                    id = it,
                    title = "Sample Anime $it",
                    image = null,
                    episodes = 12,
                    status = "TV",
                    score = 85.0.toFloat()
                )
            }
        )
        override suspend fun addToWatchList(anime: Anime, category: String) {}
        override suspend fun removeFromWatchList(id: Int) {}
    }
    return AnimeListViewModel(mockRepository)
}
