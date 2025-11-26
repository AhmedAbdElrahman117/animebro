package com.example.animbro.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimeNodeDTO(
    val id: Int,
    val title: String,
    @SerializedName("main_picture")
    val image: MainPictureDTO?,
    // nullable int
    val rank: Int?,
    val status: String,
    @SerializedName("num_episodes")
    val episodes: Int,
    val rating: String?,
    @SerializedName("mean")
    val score: Float?,
    val popularity: Int?,
    @SerializedName("average_episode_duration")
    val duration: Int?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("synopsis")
    val description: String?,
    @SerializedName("num_chapters")
    val chapters: Int,
    val genres: List<GenreDTO>,
    val videos: List<VideoDTO>,
    val recommendations: List<AnimeRecommendation>,
    val pictures: List<MainPictureDTO>

)
