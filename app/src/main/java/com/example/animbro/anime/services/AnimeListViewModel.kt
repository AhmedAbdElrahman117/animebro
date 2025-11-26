package com.example.animbro.anime.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    val watchingList: StateFlow<List<Anime>> = repository.getWatchListByCategory("Watching")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedList: StateFlow<List<Anime>> = repository.getWatchListByCategory("Completed")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val droppedList: StateFlow<List<Anime>> = repository.getWatchListByCategory("Dropped")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingList: StateFlow<List<Anime>> = repository.getWatchListByCategory("Pending")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getListByCategory(category: String): StateFlow<List<Anime>> {
        return when (category) {
            "Watching" -> watchingList
            "Completed" -> completedList
            "Dropped" -> droppedList
            "Pending" -> pendingList
            else -> watchingList // Default or empty
        }
    }
}
