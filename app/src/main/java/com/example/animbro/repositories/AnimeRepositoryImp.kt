package com.example.animbro.repositories

import android.util.Log
import androidx.compose.foundation.text.input.rememberTextFieldState
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.mapper.toDomain
import com.example.animbro.data.prefs.PreferencesManager
import com.example.animbro.data.remote.Endpoints
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimeRepositoryImp @Inject constructor(
    val api: Endpoints,
    val db: WatchListDAO,
    val preferencesManager: PreferencesManager
) : AnimeRepository {

    suspend fun getAnimeRanking(
        rankingType: String,
        limit: Int,
    ): List<Anime> {
        return try {
            val response = api.getAnimeRanking(
                limit = limit,
                rankingType = rankingType
            )

            if (response.isSuccessful) {
                Log.d("AnimeRepositoryImp", "getAnimeRanking: ${response.body()}")
                response.body()?.data?.map { it.node.toDomain() } ?: emptyList()
            } else {

                Log.d("AnimeRepositoryImp", "getAnimeRanking is not successful")
                emptyList()
            }

        } catch (e: Exception) {
            Log.d("AnimeRepositoryImp", "getAnimeRanking: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTopRatedAnime(limit: Int): List<Anime> =
        getAnimeRanking("all", limit)

    override suspend fun getPopularAnime(limit: Int): List<Anime> =
        getAnimeRanking("bypopularity", limit)

    override suspend fun getUpcomingAnime(limit: Int): List<Anime> =
        getAnimeRanking("upcoming", limit)

    override suspend fun getFavouritesAnime(limit: Int): List<Anime> =
        getAnimeRanking("favorite", limit)

    override suspend fun searchAnime(query: String): List<Anime> {
        return try {
            val response = api.searchAnime(query)

            if (response.isSuccessful) {
                response.body()?.data?.map { it.node.toDomain() } ?: emptyList()
            } else emptyList()

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getAnimeDetails(id: Int): Anime? {
        return try {
            val response = api.getAnimeDetails(id)

            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else null

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getWatchListByCategory(category: String): Flow<List<Anime>> {
        return db.getAnimeByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAnimeCategory(id: Int): String? {
        return db.getAnimeById(id)?.category
    }

    override suspend fun addToWatchList(anime: Anime, category: String) {
        db.insertAnime(anime.toDomain(category))
    }

    override suspend fun removeFromWatchList(id: Int) {
        val anime = db.getAnimeById(id)

        if (anime != null)
            db.deleteAnime(anime)
    }

    override fun isDarkMode(): Flow<Boolean> {
        return preferencesManager.isDarkMode
    }

    override fun areNotificationsEnabled(): Flow<Boolean> {
        return preferencesManager.areNotificationsEnabled
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        return preferencesManager.setDarkMode(enabled)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        preferencesManager.setNotificationsEnabled(enabled)
    }

}