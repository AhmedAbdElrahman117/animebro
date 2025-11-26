package com.example.animbro.domain.models

import com.example.animbro.data.remote.dto.AnimeRecommendation
import com.example.animbro.data.remote.dto.GenreDTO
import com.example.animbro.data.remote.dto.MainPictureDTO
import com.example.animbro.data.remote.dto.VideoDTO

data class Anime(
    val id: Int,
    val title: String,
    val image: MainPictureDTO?,
    val rank: Int? = 0,
    val status: String = "",
    val episodes: Int = 0,
    val rating: String? = "",
    val score: Float = 0.toFloat(),
    val popularity: Int? = 0,
    val duration: Int? = 0,
    val startDate: String? = "",
    val endDate: String? = "",
    val description: String? = "",
    val chapters: Int? = 0,
    val genres: List<GenreDTO>? = emptyList(),
    val videos: List<VideoDTO>? = emptyList(),
    val recommendations: List<AnimeRecommendation>? = emptyList(),
    val pictures: List<MainPictureDTO>? = emptyList(),
    val isFavourite: Boolean = false
)

data class AnimeItem(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val index: Int
)

data class AnimeSection(
    val title: String,
    val items: List<AnimeItem>
)
