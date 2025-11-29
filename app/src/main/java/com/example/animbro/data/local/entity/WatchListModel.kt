package com.example.animbro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.animbro.data.remote.dto.MainPictureDTO

@Entity(tableName = "watchlist_table")
data class WatchListModel(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val image: String?,
    val category: String,
    val score: Float,
    val status: String?,
    val episodes: Int,
    val isFavourite: Boolean = false
)