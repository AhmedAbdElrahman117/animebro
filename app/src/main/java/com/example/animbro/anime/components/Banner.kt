package com.example.animbro.anime.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.example.animbro.R

@Composable
fun Banner(
    modifier: Modifier = Modifier,
    height: Dp = 35.dp,
    onFavClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    isFavourite: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 8.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackClick != null) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(height + 8.dp)
                        .padding(end = 8.dp)
                        .clickable { onBackClick() }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.animebro_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(height)
                    .padding(start = 2.dp),
                contentScale = ContentScale.Fit
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShadowedIcon(
                painter = painterResource(id = R.drawable.fav_ic),
                contentDescription = "Favorite",
                tint = if (isFavourite) Color.Red else Color.White,
                onClick = onFavClick,
                size = 50.dp
            )

            ShadowedIcon(
                painter = painterResource(id = R.drawable.saved_ic),
                contentDescription = "Saved",
                tint = Color.White, // Always white
                onClick = onSavedClick,
                size = 50.dp
            )

        }
    }
}

@Composable
private fun ShadowedIcon(
    painter: Painter,
    contentDescription: String?,
    tint: Color,
    onClick: () -> Unit,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .padding(2.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 1.dp, y = 1.dp)
        )

        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Preview(showBackground = false)
@Composable
private fun BannerPreview() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Banner()
    }
}
