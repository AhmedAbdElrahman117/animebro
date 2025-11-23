package com.example.animbro.anime.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.animbro.banner.Banner
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
import com.example.animbro.anime.services.DetailViewModelFactory

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val animeId = intent.getIntExtra("animeId", -1)

        setContent {
            AnimBroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    val repository = remember {
                        val api: Endpoints = getApiInstance()
                        val dao: WatchListDAO = getDaoInstance()
                        AnimeRepositoryImp(api, dao)
                    }

                    val viewModel: DetailViewModel = viewModel(
                        factory = DetailViewModelFactory(repository, animeId)
                    )

                    DetailScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun getApiInstance(): Endpoints {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(com.google.gson.Gson()))
            .build()

        return retrofit.create(Endpoints::class.java)
    }

    private fun getDaoInstance(): WatchListDAO {
        val db = Room
            .databaseBuilder(
                this,
                com.example.animbro.data.local.AppDatabase::class.java,
                "animbro_db"
            )
            .fallbackToDestructiveMigration()
            .build()

        return db.watchListDao()
    }

    private fun onDetailsClick() {
        // TODO: Implement details click later
    }

    private fun onTrailerClick() {
        // TODO: Implement trailer click later
    }
}

@Composable
fun DetailScreen(
    viewModel: DetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    onCardClick = { /* TODO: Navigate to recommendation details */ }
                )
            }
        }
    }
}

@Composable
fun DetailContent(
    anime: Anime,
    screenPadding: Dp = 20.dp,
    onCardClick: (Int) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenPadding)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Description Section
            Column {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.text_blue)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = anime.description ?: "No description available",
                    fontSize = 14.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Read more",
                    modifier = Modifier.padding(top = 6.dp),
                    color = colorResource(id = R.color.text_blue),
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Stats Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        "Score",
                        "${anime.score / 10.0}",
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
                        color = colorResource(id = R.color.text_blue)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        anime.genres!!.forEach { genre ->
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
                        color = colorResource(id = R.color.text_blue)
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
                        color = colorResource(id = R.color.text_blue)
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
                color = Color.Gray
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = colorResource(id = R.color.text_blue),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GenreChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colorResource(id = R.color.text_blue),
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
                color = colorResource(id = R.color.white),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun RecommendationCard(anime: com.example.animbro.data.remote.dto.AnimeNodeDTO, onClick: () -> Unit) {
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
                tint = Color.White,
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
    seasonInfo: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
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
                Spacer(Modifier.height(20.dp))
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
                    onClick = { /* TODO: Implement trailer click */ },
                    border = BorderStroke(2.dp, colorResource(R.color.text_blue))
                ) {
                    Text("Trailer", color = colorResource(id = R.color.text_blue))
                }
            }
        }
    }
}