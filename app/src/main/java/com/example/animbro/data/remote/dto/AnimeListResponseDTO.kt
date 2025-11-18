package com.example.animbro.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimeListResponseDTO(
    @SerializedName("data")
    val data: List<AnimeNodeDTO>,
    @SerializedName("paging")
    val paging: PagingDTO
)
