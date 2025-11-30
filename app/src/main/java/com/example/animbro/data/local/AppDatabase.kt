package com.example.animbro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.animbro.data.local.dao.WatchListDAO
import com.example.animbro.data.local.entity.WatchListModel


@Database(
    entities = [WatchListModel::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchListDao(): WatchListDAO
    abstract fun favoriteDao(): FavoriteDao


}
