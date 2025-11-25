package com.example.animbro.anime.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.AsyncImage
import com.example.animbro.R
import com.example.animbro.anime.services.SearchViewModel
import com.example.animbro.anime.services.SearchViewModelFactory
import com.example.animbro.domain.models.Anime
import com.example.animbro.repositories.AnimeRepositoryImp
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.local.AppDatabase
import com.example.animbro.data.remote.AuthInterceptor
import com.example.animbro.data.remote.BASE_URL
import com.example.animbro.data.remote.Endpoints
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.res.painterResource

class SearchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = getApiInstance()
        val dao = getDaoInstance()
        val repository = AnimeRepositoryImp(api, dao)

        setContent {
            MaterialTheme {
                SearchScreen(viewModel = viewModel(factory = SearchViewModelFactory(repository)))
            }
        }
    }

    private fun getApiInstance(): Endpoints {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val okHttp = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(Endpoints::class.java)
    }

    private fun getDaoInstance(): WatchListDAO {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "animbro_db"
        ).fallbackToDestructiveMigration().build()

        return db.watchListDao()
    }
}

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.results.collectAsState()
    val darkBlue = Color(0xFF0A3D62)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = darkBlue
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    if (query.isEmpty()) Text(text = "Search anime...", color = Color.Gray)

                    BasicTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            viewModel.search(it)
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(darkBlue),
                        textStyle = TextStyle(color = darkBlue, fontWeight = FontWeight.Medium),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // عرض النتائج
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(results) { anime ->
                AnimeResultCard(anime = anime, onClick = {
                    val intent = android.content.Intent(context, DetailActivity::class.java)
                    intent.putExtra("animeId", anime.id)
                    context.startActivity(intent)
                })
            }
        }
    }
}

@Composable
fun AnimeResultCard(anime: Anime, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            // صورة الأنمي
            AsyncImage(
                model = anime.image?.medium ?: anime.image?.large,
                contentDescription = anime.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.poster_sample),
                error = painterResource(R.drawable.poster_sample)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // النصوص (العنوان والوصف)
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = anime.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = anime.description ?: "No description available",
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
        }
    }
}
