package com.example.animbro.anime.screens

import androidx.compose.material3.MaterialTheme


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animbro.R
import com.example.animbro.navigation.BottomNavigationBar
import com.example.animbro.ui.theme.AnimBroTheme
import kotlinx.coroutines.launch
import com.example.animbro.anime.services.AnimeListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.animbro.domain.models.Anime
import androidx.compose.ui.text.style.TextAlign
import dagger.hilt.android.AndroidEntryPoint


import androidx.navigation.compose.rememberNavController

@AndroidEntryPoint
class AnimeListActivity : ComponentActivity() {

    private val viewModel: AnimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimBroTheme {

                val viewModel: AnimeListViewModel = hiltViewModel()
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = "animelist"
                        )
                    }
                ) { paddingValues ->
                    UserListScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(paddingValues),
                        onAnimeClick = { animeId ->
                            val intent = Intent(this, DetailActivity::class.java).apply {
                                putExtra("animeId", animeId)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserListScreen(
    viewModel: AnimeListViewModel,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit
) {
    val tabs = listOf("Watching", "Completed", "Dropped", "Pending")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    // Show StatusUpdateDialog
    if (uiState.isDialogVisible) {
        com.example.animbro.anime.components.StatusUpdateDialog(
            currentStatus = uiState.currentCategory,
            onDismissRequest = { viewModel.dismissDialog() },
            onStatusSelected = { category ->
                viewModel.updateAnimeStatus(category)
            },
            onRemoveClick = {
                viewModel.removeAnimeFromList()
            }
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
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
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary
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
                                color = MaterialTheme.colorScheme.onSurface
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

                key(page) {
                    AnimeListPage(
                        animeList = animeList,
                        category = tabName,
                        onAnimeClick = onAnimeClick,
                        onEditClick = { anime ->
                            viewModel.onEditClick(anime, tabName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeListPage(
    animeList: List<Anime>,
    category: String = "",
    onAnimeClick: (Int) -> Unit,
    onEditClick: (Anime) -> Unit = {}
) {
    // Enhanced Debug log
    LaunchedEffect(animeList) {
        Log.d("AnimeListPage", "=== Category: $category, Count: ${animeList.size} ===")
        animeList.forEachIndexed { index, anime ->
            Log.d("AnimeListPage", """
                [$index] Anime Details:
                - ID: ${anime.id}
                - Title: ${anime.title}
                - Image Medium: ${anime.image?.medium}
                - Score: ${anime.score}
                - Status: ${anime.status}
                - Episodes: ${anime.episodes}
                - isFavourite: ${anime.isFavourite}
            """.trimIndent())
        }
    }

    // Hoist painter resources to avoid repeated lookups
    val placeholderPainter = painterResource(id = R.drawable.poster_sample)
    val errorPainter = painterResource(id = R.drawable.poster_sample)
    val savedIconPainter = painterResource(id = R.drawable.saved_ic)

    if (animeList.isEmpty()) {
        // Empty State
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.saved_ic),
                    contentDescription = "Empty List",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Nothing here yet!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (category.isNotEmpty()) {
                        "Start adding anime to your $category list"
                    } else {
                        "Start adding anime to your list"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
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
                    savedIconPainter = savedIconPainter,
                    onClick = { onAnimeClick(anime.id) },
                    onEditClick = { onEditClick(anime) }
                )
            }
        }
    }
}

@Composable
fun UserHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // App Logo
        // Banner(
        //     height = 24.dp,
        //     modifier = Modifier
        //         .fillMaxWidth()
        //         .padding(top = 20.dp)
        //         .align(Alignment.TopCenter)
        // )
        Image(
            painter = painterResource(id = R.drawable.animebro_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.Center)
        )

    }
}

@Composable
fun AnimeListItem(
    anime: Anime,
    placeholderPainter: androidx.compose.ui.graphics.painter.Painter,
    errorPainter: androidx.compose.ui.graphics.painter.Painter,
    savedIconPainter: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Anime Poster
            Box(
                modifier = Modifier
                    .width(85.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = anime.image?.medium,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // EP and Status
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Episodes: ${if (anime.episodes > 0) anime.episodes else "N/A"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (anime.status.isNotEmpty()) anime.status else "Unknown",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Rating/Score
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (anime.score > 0) String.format("%.1f", anime.score) else "N/A",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Edit Status Button
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    painter = savedIconPainter,
                    contentDescription = "Edit Status",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun UserListScreenPreview() {
//    AnimBroTheme {
//        Scaffold(
//            bottomBar = {
//                BottomNavigationBar(currentRoute = "animelist")
//            }
//        ) { paddingValues ->
//            UserListScreen(
//                viewModel = previewAnimeListViewModel(),
//                modifier = Modifier.padding(paddingValues),
//                onAnimeClick = {}
//            )
//        }
//    }
//}

//private fun previewAnimeListViewModel(): AnimeListViewModel {
//    val mockRepository = object : com.example.animbro.domain.repository.AnimeRepository {
//        override suspend fun getTopRatedAnime(limit: Int) = emptyList<Anime>()
//        override suspend fun getPopularAnime(limit: Int) = emptyList<Anime>()
//        override suspend fun getUpcomingAnime(limit: Int) = emptyList<Anime>()
//        override suspend fun getFavouritesAnime(limit: Int) = emptyList<Anime>()
//        override suspend fun searchAnime(query: String) = emptyList<Anime>()
//        override suspend fun getAnimeDetails(id: Int) = null
//        override fun getWatchListByCategory(category: String) =
//            kotlinx.coroutines.flow.MutableStateFlow(
//                List(5) {
//                    Anime(
//                        id = it,
//                        title = "Sample Anime $it",
//                        image = null,
//                        episodes = 12,
//                        status = "TV",
//                        score = 85.0.toFloat()
//                    )
//                }
//            )
//
//        override suspend fun addToWatchList(anime: Anime, category: String) {}
//        override suspend fun removeFromWatchList(id: Int) {}
//    }
//    return AnimeListViewModel(mockRepository)
//}
