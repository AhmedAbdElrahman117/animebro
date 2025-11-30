package com.example.animbro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.animbro.data.local.entity.WatchListModel
import kotlinx.coroutines.flow.Flow


@Dao
interface WatchListDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: WatchListModel)

    @Delete
    suspend fun deleteAnime(anime: WatchListModel)

    @Query("SELECT * FROM watchlist_table WHERE category = :category")
    fun getAnimeByCategory(category: String): Flow<List<WatchListModel>>

    @Query("SELECT * FROM watchlist_table WHERE id = :id")
    suspend fun getAnimeById(id: Int): WatchListModel?

    @Query("SELECT * FROM watchlist_table WHERE isFavourite = 1")
    fun getFavouriteAnime(): Flow<List<WatchListModel>>

}