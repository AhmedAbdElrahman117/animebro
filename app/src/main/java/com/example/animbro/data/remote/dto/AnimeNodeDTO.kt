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
    
    )
