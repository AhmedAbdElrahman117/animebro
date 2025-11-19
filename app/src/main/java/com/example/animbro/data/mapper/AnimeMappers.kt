package com.example.animbro.data.mapper

import androidx.compose.material3.rememberTooltipState
import com.example.animbro.data.local.entity.WatchListModel
import com.example.animbro.data.remote.dto.AnimeNodeDTO
import com.example.animbro.domain.models.Anime


fun AnimeNodeDTO.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        image = image,
        description = description,
        rank = rank,
        popularity = popularity,
        duration = duration,
        genres = genres,
        status = status,
        episodes = episodes,
        chapters = chapters,
        startDate = startDate,
        endDate = endDate,
        rating = rating,
        videos = videos,
        recommendations = recommendations,
        pictures = pictures,
        score = score,
    )
}

fun WatchListModel.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        image = image,
    )
}

fun Anime.toDomain(category: String): WatchListModel {
    return WatchListModel(
        id = id,
        title = title,
        image = image,
        category = category
    )
}