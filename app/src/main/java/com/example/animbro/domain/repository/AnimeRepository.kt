package com.example.animbro.domain.repository

import com.example.animbro.domain.models.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    suspend fun getTopRatedAnime(limit: Int): List<Anime>
    suspend fun getPopularAnime(limit: Int): List<Anime>
    suspend fun getUpcomingAnime(limit: Int): List<Anime>
    suspend fun getFavouritesAnime(limit: Int): List<Anime>
    suspend fun searchAnime(query: String): List<Anime>
    suspend fun getAnimeDetails(id: Int): Anime?

    fun getWatchListByCategory(category: String): Flow<List<Anime>>
    suspend fun addToWatchList(anime: Anime, category: String)
    suspend fun removeFromWatchList(id: Int)
}


