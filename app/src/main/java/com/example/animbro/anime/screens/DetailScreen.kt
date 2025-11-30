package com.example.animbro.anime.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.animbro.anime.components.Banner
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.remote.Endpoints
import com.example.animbro.domain.models.Anime
import com.example.animbro.repositories.AnimeRepositoryImp
import com.example.animbro.ui.theme.AnimBroTheme
import com.example.animbro.R
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.example.animbro.data.remote.BASE_URL
import com.example.animbro.data.remote.AuthInterceptor
import androidx.room.Room
import com.example.animbro.anime.services.DetailViewModel
import com.example.animbro.anime.services.HomeViewModel
import com.example.animbro.data.remote.dto.VideoDTO
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

enum class DetailTab {
    DETAILS,
    TRAILERS
}

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {
    private val viewModel: DetailViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animeId = intent.getIntExtra("animeId", -1)

        setContent {
            AnimBroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    DetailScreen(
                        viewModel = viewModel,
                        onAnimeClick = { newId ->
                            navigateToDetail(newId)
                        },
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }

    private fun navigateToDetail(animeId: Int) {
        val intent = android.content.Intent(this, DetailActivity::class.java).apply {
            putExtra("animeId", animeId)
        }
        startActivity(intent)
//        finish()
    }


//    private fun onDetailsClick() {
//        // TODO: Implement details click later
//    }
//
//    private fun onTrailerClick() {
//        // TODO: Implement trailer click later
//    }
}


@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isDialogVisible) {
        com.example.animbro.anime.components.StatusUpdateDialog(
            currentStatus = uiState.currentAnimeStatus,
            onDismissRequest = { viewModel.dismissDialog() },
            onStatusSelected = { category -> viewModel.updateAnimeStatus(category) },
            onRemoveClick = { viewModel.removeAnimeFromList() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error != null -> {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.loadAnimeDetails() }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text(uiState.error ?: "An error occurred")
                }
            }

            uiState.anime != null -> {
                val anime = uiState.anime!!
                DetailContent(
                    anime = anime,
                    screenPadding = 20.dp,
                    onCardClick = onAnimeClick,
                    onSavedClick = { viewModel.onAddClick() },
                    onBackClick = onBackClick,
                    isFavourite = uiState.isFavourite,
                    onFavClick = { viewModel.onFavoriteClick() }
                )
            }
        }
    }
}

@Composable
fun DetailContent(
    anime: Anime,
    screenPadding: Dp = 20.dp,
    onCardClick: (Int) -> Unit = {},
    onSavedClick: () -> Unit = {},
    isFavourite: Boolean = false,
    onFavClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var currentTab by remember { mutableStateOf(DetailTab.DETAILS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimePosterSection(
            imageUrl = anime.image?.large ?: anime.image?.medium,
            title = anime.title,
            subtitle = anime.status,
            seasonInfo = buildString {
                if (anime.startDate?.isNotEmpty() == true) append(anime.startDate)
                if (anime.episodes > 0) {
                    if (isNotEmpty()) append(" • ")
                    append("${anime.episodes} eps")
                }
            },
            selectedTab = currentTab,
            onTabSelected = { newTab -> currentTab = newTab },
            onSavedClick = onSavedClick,
            isFavourite = isFavourite,
            onFavClick = onFavClick,
            onBackClick = onBackClick
        )

        when (currentTab) {
            DetailTab.DETAILS -> DetailsBody(anime, onCardClick)
            DetailTab.TRAILERS -> TrailersBody(anime.videos)
        }

    }
}

@Composable
fun DetailsBody(
    anime: Anime,
    onCardClick: (Int) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }
    var isTextOverflowing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Description Section
        Column {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = anime.description ?: "No description available",
                fontSize = 14.sp,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    if (!isExpanded) {
                        isTextOverflowing = textLayoutResult.hasVisualOverflow
                    }
                },
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )

            if (isTextOverflowing || isExpanded) {
                Text(
                    text = if (isExpanded) "Read less" else "Read more",
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clickable { isExpanded = !isExpanded },
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Stats Section
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    "Score",
                    if (anime.score > 0) String.format(
                        "%.2f",
                        anime.score
                    ) else "N/A",
                    Modifier.weight(1f)
                )
                StatCard(
                    "Rank",
                    if (anime.rank != null && anime.rank > 0) "#${anime.rank}" else "N/A",
                    Modifier.weight(1f)
                )
                StatCard(
                    "Popularity",
                    if (anime.popularity != null && anime.popularity > 0) "#${anime.popularity}" else "N/A",
                    Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard("Episodes", "${anime.episodes}", Modifier.weight(1f))
                StatCard(
                    "Duration",
                    if (anime.duration != null && anime.duration > 0) "${anime.duration}m" else "N/A",
                    Modifier.weight(1f)
                )
                StatCard("Rating", anime.rating ?: "-", Modifier.weight(1f))
            }
        }

        // Genres Section
        if (!anime.genres.isNullOrEmpty()) {
            Column {
                Text(
                    text = "Genres",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(anime.genres!!) { genre ->
                        GenreChip(genre.name)
                    }
                }
            }
        }

        // Gallery Section
        if (!anime.pictures.isNullOrEmpty()) {
            Column {
                Text(
                    text = "Gallery",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(anime.pictures!!) { picture ->
                        AsyncImage(
                            model = picture.large,
                            contentDescription = null,
                            modifier = Modifier
                                .width(140.dp)
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.poster_sample),
                            error = painterResource(R.drawable.poster_sample)
                        )
                    }
                }
            }
        }

        // Recommendations Section
        if (!anime.recommendations.isNullOrEmpty()) {
            Column {
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(anime.recommendations!!) { recommendation ->
                        RecommendationCard(
                            anime = recommendation.node,
                            onClick = { onCardClick(recommendation.node.id) }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }

}


@Composable
fun TrailersBody(videos: List<VideoDTO>?) {
    if (videos.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No trailers available", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            videos.forEach { video ->
                Text(video.title, fontWeight = FontWeight.Bold)

                YouTubePlayer(
                    videoId = extractVideoId(video.url),
                    lifeCycleOwner = LocalLifecycleOwner.current
                )
                Log.d("VIDEO", video.url)
            }
        }
    }
}

@Composable
fun YouTubePlayer(
    videoId: String,
    lifeCycleOwner: LifecycleOwner
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        factory = {
            YouTubePlayerView(context = it).apply {
                lifeCycleOwner.lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(videoId = videoId, 0f)
                    }
                })
            }
        }
    )
}

fun extractVideoId(url: String): String {
    return try {
        val uri = Uri.parse(url)
        when {
            // Case 1: Short URL (https://youtu.be/LHtdKWJdif4)
            // The ID is simply the last part of the path
            uri.host?.contains("youtu.be") == true -> {
                uri.lastPathSegment ?: ""
            }

            // Case 2: Standard URL (https://www.youtube.com/watch?v=LHtdKWJdif4)
            // The ID is the value of the query parameter "v"
            uri.host?.contains("youtube.com") == true -> {
                uri.getQueryParameter("v") ?: ""
            }

            // Case 3: Embed URL (https://www.youtube.com/embed/LHtdKWJdif4)
            // Sometimes APIs send this. The ID is the last part of the path.
            uri.path?.contains("embed") == true -> {
                uri.lastPathSegment ?: ""
            }

            // Fallback: If it fails, return empty
            else -> ""
        }
    } catch (e: Exception) {
        ""
    }
}


@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(92.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GenreChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .height(32.dp)
            .wrapContentWidth()
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun RecommendationCard(
    anime: com.example.animbro.data.remote.dto.AnimeNodeDTO,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box {
            AsyncImage(
                model = anime.image?.large ?: anime.image?.medium,
                contentDescription = anime.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.poster_sample),
                error = painterResource(R.drawable.poster_sample)
            )

            Icon(
                painter = painterResource(id = R.drawable.fav_ic),
                contentDescription = "fav",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
fun AnimePosterSection(
    imageUrl: String?,
    title: String,
    subtitle: String,
    seasonInfo: String,
    selectedTab: DetailTab,
    onTabSelected: (DetailTab) -> Unit,
    onSavedClick: () -> Unit,
    isFavourite: Boolean,
    onFavClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
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

        Banner(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .align(Alignment.TopCenter),
            onSavedClick = onSavedClick,
            isFavourite = isFavourite,
            onFavClick = onFavClick,
            onBackClick = onBackClick
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp + 24.dp + 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight()
            ) {
                Spacer(Modifier.height(70.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(subtitle, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(seasonInfo, color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = { onTabSelected(DetailTab.DETAILS) },
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Details", color = MaterialTheme.colorScheme.primary)
                }

                OutlinedButton(
                    onClick = { onTabSelected(DetailTab.TRAILERS) },
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground)
                ) {
                    Text("Trailer", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    AnimBroTheme {
        DetailContent(
            anime = com.example.animbro.domain.models.Anime(
                id = 1,
                title = "Demon Slayer: Kimetsu no Yaiba",
                image = null,
                rank = 1,
                status = "Finished Airing",
                episodes = 26,
                rating = "R - 17+ (violence & profanity)",
                score = 8.54f,
                popularity = 1,
                duration = 24,
                startDate = "Apr 6, 2019",
                description = "It is the Taisho Period in Japan. Tanjiro, a kindhearted boy who sells charcoal for a living, finds his family slaughtered by a demon. To make matters worse, his younger sister Nezuko, the sole survivor, has been transformed into a demon herself. Though devastated by this grim reality, Tanjiro resolves to become a “demon slayer” so that he can turn his sister back into a human, and kill the demon that massacred his family.",
                genres = listOf(
                    com.example.animbro.data.remote.dto.GenreDTO(1, "Action"),
                    com.example.animbro.data.remote.dto.GenreDTO(2, "Demons")
                ),
                isFavourite = true
            )
        )
    }
}
