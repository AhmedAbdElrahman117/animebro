package com.example.animbro.repositories

import android.util.Log
import androidx.compose.foundation.text.input.rememberTextFieldState
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.local.entity.WatchListModel
import com.example.animbro.data.mapper.toDomain
import com.example.animbro.data.prefs.PreferencesManager
import com.example.animbro.data.remote.Endpoints
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.map

class AnimeRepositoryImp @Inject constructor(
    val api: Endpoints,
    val db: WatchListDAO,
    val preferencesManager: PreferencesManager,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
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
        val myAnime = db.getAnimeById(id)

        if (!(myAnime?.category == null || myAnime.category == ""))
            return db.getAnimeById(id)?.category

        return null
    }

    override suspend fun addToWatchList(anime: Anime, category: String) {
        val existingEntity = db.getAnimeById(anime.id)

        val entityToSave = if (existingEntity != null) {
            existingEntity.copy(
                category = category,
                title = anime.title,
                image = anime.image?.large ?: anime.image?.medium,
                score = existingEntity.score,
                episodes = existingEntity.episodes,
                isFavourite = existingEntity.isFavourite
            )
        } else anime.toDomain(category)

        db.insertAnime(entityToSave)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            try {
                val data = hashMapOf(
                    "id" to entityToSave.id,
                    "title" to entityToSave.title,
                    "image" to entityToSave.image,
                    "category" to category,
                    "score" to entityToSave.score,
                    "episodes" to entityToSave.episodes,
                    "status" to entityToSave.status
                )

                firestore.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(anime.id.toString())
                    .set(data, SetOptions.merge())
                    .await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override suspend fun removeFromWatchList(id: Int) {
        val anime = db.getAnimeById(id)

        if (anime == null)
            return

        db.deleteAnime(anime)

        if (anime.isFavourite) {
            db.insertAnime(
                WatchListModel(
                    id = anime.id,
                    title = anime.title,
                    image = anime.image,
                    category = "",
                    score = anime.score,
                    episodes = anime.episodes,
                    isFavourite = true,
                    status = anime.status
                )
            )
        }

        val userId = auth.currentUser?.uid

        if (userId != null) {
            try {
                val docRef = firestore.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(id.toString())

                if (anime.isFavourite) {
                    docRef.update("category", "").await()
                } else {
                    docRef.delete().await()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getUserFavouriteAnime(): Flow<List<Anime>> {
        return db.getFavouriteAnime().map {
            it.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun isAnimeFavourite(id: Int): Boolean {
        return db.getAnimeById(id)?.isFavourite == true
    }

    override suspend fun toggleFavourite(anime: Anime) {
        val existing = db.getAnimeById(anime.id)

        val newFavoriteState = !(existing?.isFavourite ?: false)

        val entityToSave = existing?.copy(isFavourite = newFavoriteState)
            ?: anime.toDomain(category = "").copy(isFavourite = true)

        db.insertAnime(entityToSave)

        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                val data = hashMapOf(
                    "id" to anime.id,
                    "title" to anime.title,
                    "image" to (anime.image?.large ?: anime.image?.medium ?: ""),
                    "is_favourite" to newFavoriteState,
                    "last_updated" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(userId)
                    .collection("watchlist")
                    .document(anime.id.toString())
                    .set(data, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
