package com.example.animbro.banner

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
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
import com.example.animbro.R

@Composable
fun Banner(
    modifier: Modifier = Modifier,
    height: Dp = 24.dp,
    onFavClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onAccClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = 8.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.animebro_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(height)
                .padding(start = 4.dp),
            contentScale = ContentScale.Fit
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.fav_ic),
                contentDescription = "Favorite",
                modifier = Modifier
                    .size(height)
                    .padding(5.dp)
                    .clickable { onFavClick() },
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(id = R.drawable.saved_ic),
                contentDescription = "Saved",
                modifier = Modifier
                    .size(height)
                    .padding(5.dp)
                    .clickable { onSavedClick() },
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(id = R.drawable.acc_ic),
                contentDescription = "Account",
                modifier = Modifier
                    .size(height)
                    .padding(5.dp)
                    .clickable { onAccClick() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BannerPreview() {
    Surface(color = Color.Black) {
        Banner(height = 20.dp)
    }
}