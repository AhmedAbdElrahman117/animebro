package com.example.animbro.anime.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import com.example.animbro.anime.components.AnimeCard
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.animbro.domain.models.Anime
import com.example.animbro.R
import androidx.compose.ui.tooling.preview.Preview
import com.example.animbro.data.remote.dto.MainPictureDTO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.example.animbro.ui.theme.AnimBroTheme
import com.example.animbro.anime.services.HomeViewModel
import androidx.compose.ui.draw.clip
import com.example.animbro.navigation.BottomNavigationBar
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import com.example.animbro.anime.components.StatusUpdateDialog
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.draw.shadow

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnimBroTheme {
                val navController = rememberNavController()
                val uiState by viewModel.uiState.collectAsState()
                val context = LocalContext.current

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            currentRoute = "home"
                        )
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        HomeScreen(
                            viewModel = viewModel,
                            onAnimeClick = { animeId ->
                                navigateToDetail(animeId)
                            },
                            onSearchClick = {
                                val intent = Intent(context, SearchActivity::class.java)
                                context.startActivity(intent)
                            },
                            onMoreClick = { section ->
                                // TODO: Navigate to section list screen
                            },
                            onError = {
                                // For HomeActivity usage, maybe show toast or do nothing if not main flow
                            }
                        )
                    }
                }
            }
        }
    }

    private fun navigateToDetail(animeId: Int) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("animeId", animeId)
        }
    }
}

@Composable
fun StickySearchBar(
    onSearchClick: () -> Unit,
    isSticky: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .then(
                if (isSticky)
                    Modifier
                        .shadow(8.dp, RoundedCornerShape(25.dp))
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(25.dp)
                        )
                else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search anime...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    screenPadding: Dp = 20.dp,
    onAnimeClick: (Int) -> Unit = {},
    onMoreClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onError: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val showStickySearch by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1 ||
                    (listState.firstVisibleItemIndex == 1 && listState.firstVisibleItemScrollOffset > 0)
        }
    }

    LaunchedEffect(uiState.randomAnimeId) {
        uiState.randomAnimeId?.let { id ->
            onAnimeClick(id)
            viewModel.onRandomAnimeNavigated()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            onError()
        }
    }

    if (uiState.isDialogVisible && uiState.selectedAnime != null) {
        StatusUpdateDialog(
            currentStatus = uiState.currentAnimeStatus,
            onDismissRequest = { viewModel.dismissDialog() },
            onStatusSelected = { category ->
                viewModel.updateAnimeStatus(category)
            },
            onRemoveClick = {
                viewModel.removeAnimeFromList()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null -> {
                // Error handled by LaunchedEffect -> Navigation
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // Poster Section
                        item {
                            if (uiState.trendingAnime.isNotEmpty()) {
                                val featuredAnime = uiState.trendingAnime.first()
                                PosterSection(
                                    anime = featuredAnime,
                                    onDetailsClick = { onAnimeClick(featuredAnime.id) },
                                    onTrailerClick = { /* Handle trailer */ }
                                )
                            }
                        }
                        // Search Bar
                        item {
                            StickySearchBar(
                                onSearchClick = onSearchClick,
                                isSticky = false
                            )
                        }
                        // Main Content
                        item {
                            HomeScreenContent(
                                viewModel = viewModel,
                                screenPadding = screenPadding,
                                onAnimeClick = onAnimeClick,
                            )
                        }
                    }
                    if (showStickySearch) {
                        StickySearchBar(
                            onSearchClick = onSearchClick,
                            isSticky = true,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(10f)
                        )
                    }
                }
            }
        }

        // Floating Action Button for Random Anime
        if (!uiState.isLoading && uiState.error == null) {
            FloatingActionButton(
                onClick = {
                    if (!uiState.isRandomLoading) {
                        viewModel.findRandomAnime()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                if (uiState.isRandomLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_dice),
                        contentDescription = "Random Anime"
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    screenPadding: Dp = 20.dp,
    onAnimeClick: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Trending Section
        AnimeSection(
            title = "Trending",
            animeList = uiState.trendingAnime,
            screenPadding = screenPadding,
            onAnimeClick = onAnimeClick,
            onAddClick = { anime -> viewModel.onAddClick(anime) },
            onFavClick = { anime -> viewModel.onFavoriteClick(anime) }
        )

        // Top Ranked Section
        AnimeSection(
            title = "Top Ranked",
            animeList = uiState.topRankedAnime,
            screenPadding = screenPadding,
            onAnimeClick = onAnimeClick,
            onAddClick = { anime -> viewModel.onAddClick(anime) },
            onFavClick = { anime -> viewModel.onFavoriteClick(anime) }
        )

        // Upcoming Section
        AnimeSection(
            title = "Upcoming",
            animeList = uiState.upcomingAnime,
            screenPadding = screenPadding,
            onAnimeClick = onAnimeClick,
            onAddClick = { anime -> viewModel.onAddClick(anime) },
            onFavClick = { anime -> viewModel.onFavoriteClick(anime) }
        )

        // Favourite Anime Section
        AnimeSection(
            title = "Most Favourite",
            animeList = uiState.favouriteAnime,
            screenPadding = screenPadding,
            onAnimeClick = onAnimeClick,
            onAddClick = { anime -> viewModel.onAddClick(anime) },
            onFavClick = { anime -> viewModel.onFavoriteClick(anime) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeSectionPreview() {
    MaterialTheme {
        AnimeSection(
            title = "Trending",
            animeList = getSampleAnimeList(),
            screenPadding = 20.dp,
            onAnimeClick = {},
            onAddClick = {},
            onFavClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeCardPreview() {
    MaterialTheme {
        AnimeCard(
            anime = getSampleAnime(),
            onClick = {},
            onAddClick = {},
            onFavClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun PosterSectionPreview() {
    MaterialTheme {
        PosterSection(
            anime = getSampleAnime(),
            onDetailsClick = {},
            onTrailerClick = {}
        )
    }
}

private fun getSampleAnime() = Anime(
    id = 1,
    title = "Attack on Titan",
    image = MainPictureDTO(
        medium = "https://cdn.myanimelist.net/images/anime/10/47347.jpg",
        large = "https://cdn.myanimelist.net/images/anime/10/47347l.jpg"
    ),
    rank = 1,
    status = "Finished Airing",
    episodes = 25,
    rating = "R - 17+",
    score = 8.5.toFloat(),
    popularity = 1,
    duration = 24,
    startDate = "2013-04-07",
    endDate = "2013-09-29",
    description = "Centuries ago, mankind was slaughtered to near extinction by monstrous humanoid creatures called titans, forcing humans to hide in fear behind enormous concentric walls.",
    isFavourite = false
)

private fun getSampleAnimeList() = listOf(
    getSampleAnime().copy(id = 1, title = "Attack on Titan", rank = 1),
    getSampleAnime().copy(id = 2, title = "Fullmetal Alchemist", rank = 2),
    getSampleAnime().copy(id = 3, title = "Death Note", rank = 3),
    getSampleAnime().copy(id = 4, title = "One Punch Man", rank = 4),
    getSampleAnime().copy(id = 5, title = "Steins;Gate", rank = 5)
)

@Composable
fun AnimeSection(
    title: String,
    animeList: List<Anime>,
    screenPadding: Dp,
    onAnimeClick: (Int) -> Unit,
    onAddClick: (Anime) -> Unit,
    onFavClick: (Anime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = screenPadding)
    ) {
        AnimeSectionRow(title = title)

        if (animeList.isEmpty()) {
            Text(
                text = "No anime available",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(animeList.size) { index ->
                    val anime = animeList[index]
                    AnimeCard(
                        anime = anime,
                        onClick = { onAnimeClick(anime.id) },
                        onAddClick = onAddClick,
                        onFavClick = onFavClick
                    )
                }
            }
        }
    }
}

@Composable
fun PosterSection(
    anime: Anime,
    onDetailsClick: () -> Unit,
    onTrailerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        AsyncImage(
            model = anime.image?.large ?: anime.image?.medium,
            contentDescription = anime.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.poster_sample),
            error = painterResource(R.drawable.poster_sample)
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 1.0f)
                        )
                    )
                )
        )

        Image(
            painter = painterResource(id = R.drawable.animebro_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.TopStart)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    anime.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = buildString {
                        if (anime.status.isNotEmpty()) append(anime.status)
                        if (anime.episodes > 0) {
                            if (isNotEmpty()) append(" • ")
                            append("${anime.episodes} eps")
                        }
                    },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = buildString {
                        if (anime.score > 0) append("★ ${anime.score}")
                        if (anime.startDate?.isNotEmpty() == true) {
                            if (isNotEmpty()) append(" • ")
                            append(anime.startDate)
                        }
                    },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    anime.description ?: "No description available",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Details", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun AnimeSectionRow(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}