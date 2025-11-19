package com.example.animbro.data.remote.dto

import com.google.gson.annotations.SerializedName


data class AnimeRecommendation(
    val node: AnimeNodeDTO,
    @SerializedName("num_recommendations")
    val size: Int
)
