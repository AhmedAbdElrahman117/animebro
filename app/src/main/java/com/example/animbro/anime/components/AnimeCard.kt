package com.example.animbro.anime.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.animbro.R
import com.example.animbro.domain.models.Anime

@Composable
fun AnimeCard(
    anime: Anime,
    onClick: () -> Unit,
    onAddClick: (Anime) -> Unit,
    onFavClick: (Anime) -> Unit = {}
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
                    .clickable { onFavClick(anime) }
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
