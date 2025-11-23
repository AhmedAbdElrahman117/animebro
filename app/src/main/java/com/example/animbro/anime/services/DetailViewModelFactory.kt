package com.example.animbro.anime.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.animbro.domain.repository.AnimeRepository

class DetailViewModelFactory(
    private val repository: AnimeRepository,
    private val animeId: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository, animeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}