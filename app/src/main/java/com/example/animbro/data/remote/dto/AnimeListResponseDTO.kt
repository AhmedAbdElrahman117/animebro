package com.example.animbro.data.remote.dto

import com.google.gson.annotations.SerializedName


data class RankingInfoDTO(
    val rank: Int,
    @SerializedName("previous_rank")
    val previousRank: Int?,
)

data class DataDTO(
    val node: AnimeNodeDTO,
    val ranking: RankingInfoDTO
)

data class AnimeListResponseDTO(
    val data: List<DataDTO>,
    val paging: PagingDTO
)
