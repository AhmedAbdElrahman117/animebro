package com.example.animbro.anime.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animbro.domain.models.Anime
import com.example.animbro.R
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.animbro.data.remote.dto.MainPictureDTO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.remote.Endpoints
import com.example.animbro.repositories.AnimeRepositoryImp
import com.example.animbro.ui.theme.AnimBroTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animbro.anime.services.HomeViewModel
import com.example.animbro.anime.components.Banner
import com.example.animbro.data.remote.AuthInterceptor
import com.example.animbro.data.remote.BASE_URL
import androidx.compose.material.icons.Icons
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import com.example.animbro.anime.components.BottomNavigationBar
import android.inputmethodservice.Keyboard.Row
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.animbro.anime.components.StatusUpdateDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnimBroTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(currentRoute = "home")
                    }

                ) { paddingValues ->

                    Box(modifier = Modifier.padding(paddingValues)) {
                        HomeScreen(
                            viewModel = viewModel,
                            screenPadding = 20.dp,
                            onAnimeClick = { animeId ->
                                navigateToDetail(animeId)
                            },
                            onMoreClick = { section ->
                                // TODO: Navigate to section list screen
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
        startActivity(intent)
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    screenPadding: Dp = 20.dp,
    onAnimeClick: (Int) -> Unit = {},
    onMoreClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.randomAnimeId) {
        uiState.randomAnimeId?.let { id ->
            onAnimeClick(id)
            viewModel.onRandomAnimeNavigated()
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (!uiState.isRandomLoading) {
                    viewModel.findRandomAnime()
                }
            }) {
                if (uiState.isRandomLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_dice),
                        contentDescription = "Random"
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = paddingValues.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
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
                // Trending Section
                item {
                    AnimeSection(
                        title = "Trending",
                        animeList = uiState.trendingAnime,
                        screenPadding = screenPadding,
                        onAnimeClick = onAnimeClick,
                        onMoreClick = { onMoreClick("Trending") },
                        onAddClick = { anime -> viewModel.onAddClick(anime) }
                    )
                }

                // Top Ranked Section
                item {
                    AnimeSection(
                        title = "Top Ranked",
                        animeList = uiState.topRankedAnime,
                        screenPadding = screenPadding,
                        onAnimeClick = onAnimeClick,
                        onMoreClick = { onMoreClick("Top Ranked") },
                        onAddClick = { anime -> viewModel.onAddClick(anime) }
                    )
                }

                // Upcoming Section
                item {
                    AnimeSection(
                        title = "Upcoming",
                        animeList = uiState.upcomingAnime,
                        screenPadding = screenPadding,
                        onAnimeClick = onAnimeClick,
                        onMoreClick = { onMoreClick("Upcoming") },
                        onAddClick = { anime -> viewModel.onAddClick(anime) }
                    )
                }

                // Favourite Anime Section
                item {
                    AnimeSection(
                        title = "Most Favourite Anime",
                        animeList = uiState.favouriteAnime,
                        screenPadding = screenPadding,
                        onAnimeClick = onAnimeClick,
                        onMoreClick = { onMoreClick("All Anime") },
                        onAddClick = { anime -> viewModel.onAddClick(anime) }
                    )
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Error message
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.onRefresh() }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

// Preview Functions
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun HomeScreenPreview() {
//    MaterialTheme {
//        HomeScreen(
//            viewModel = PreviewHomeViewModel(),
//            onAnimeClick = {},
//            onMoreClick = {}
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun AnimeSectionPreview() {
    MaterialTheme {
        AnimeSection(
            title = "Trending",
            animeList = getSampleAnimeList(),
            screenPadding = 20.dp,
            onAnimeClick = {},
            onMoreClick = {},
            onAddClick = {}
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
            onAddClick = {}
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

@Preview(showBackground = true)
@Composable
fun AnimeSectionRowPreview() {
    MaterialTheme {
        AnimeSectionRow(
            title = "Trending",
            onMoreClick = {}
        )
    }
}

// Preview Helper Functions
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

// Preview ViewModel - Creates a mock ViewModel with sample data
//private fun PreviewHomeViewModel(): HomeViewModel {
//    val mockRepository = object : com.example.animbro.domain.repository.AnimeRepository {
//        override suspend fun getTopRatedAnime(limit: Int) = getSampleAnimeList()
//        override suspend fun getPopularAnime(limit: Int) = getSampleAnimeList()
//        override suspend fun getUpcomingAnime(limit: Int) = getSampleAnimeList()
//        override suspend fun getFavouritesAnime(limit: Int) = getSampleAnimeList()
//        override suspend fun searchAnime(query: String) = getSampleAnimeList()
//        override suspend fun getAnimeDetails(id: Int) = getSampleAnime()
//        override fun getWatchListByCategory(category: String) =
//            MutableStateFlow(getSampleAnimeList())
//
//        override suspend fun addToWatchList(anime: Anime, category: String) {}
//        override suspend fun removeFromWatchList(id: Int) {}
//    }
//
//    return HomeViewModel(mockRepository)
//}

@Composable
fun AnimeSection(
    title: String,
    animeList: List<Anime>,
    screenPadding: Dp,
    onAnimeClick: (Int) -> Unit,
    onMoreClick: () -> Unit,
    onAddClick: (Anime) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = screenPadding)
    ) {
        AnimeSectionRow(
            title = title,
            onMoreClick = onMoreClick
        )

        if (animeList.isEmpty()) {
            Text(
                text = "No anime available",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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
                        onAddClick = onAddClick
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

        // Gradient overlay
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.0f),
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 1.0f)
                        )
                    )
                )
        )
        Banner(
            height = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .align(Alignment.TopCenter)
        )
        // Content
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

                // Description
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

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    border = BorderStroke(1.dp, colorResource(id = R.color.text_blue))
                ) {
                    Text("Details", color = colorResource(id = R.color.text_blue))
                }

                OutlinedButton(
                    onClick = onTrailerClick,
                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Text("Trailer", color = colorResource(id = R.color.text_blue))
                }
            }
        }
    }
}

@Composable
fun AnimeSectionRow(title: String, onMoreClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.text_blue)
        )
        Text(
            "more",
            modifier = Modifier.clickable { onMoreClick() },
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.text_blue),
            textDecoration = TextDecoration.Underline
        )
    }
}

@Composable
fun AnimeCard(
    anime: Anime,
    onClick: () -> Unit,
    onAddClick: (Anime) -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(210.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box {
            // Anime image
            AsyncImage(
                model = anime.image?.large ?: anime.image?.medium,
                contentDescription = anime.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.poster_sample),
                error = painterResource(R.drawable.poster_sample)
            )

            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clickable { onAddClick(anime) }
            )

            Icon(
                painter = painterResource(
                    if (anime.isFavourite) R.drawable.fav_ic else R.drawable.fav_ic
                ),
                contentDescription = "Favorite",
                tint = if (anime.isFavourite) Color.Red else Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { /* Handle favorite */ }
            )

            if (anime.rank != null && anime.rank > 0) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "#${anime.rank}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(onClick: () -> Unit) {
    val darkBlue = Color(0xFF0A3D62)

    Box(
        modifier = Modifier
            .width(350.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = darkBlue
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "                      Search…",
                color = Color.Gray
            )
        }
    }
}

