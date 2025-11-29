package com.example.animbro.anime.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnimeListUiState(
    val isDialogVisible: Boolean = false,
    val selectedAnime: Anime? = null,
    val currentCategory: String? = null
)

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeListUiState())
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

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
            else -> watchingList
        }
    }

    fun onEditClick(anime: Anime, currentCategory: String) {
        _uiState.value = _uiState.value.copy(
            isDialogVisible = true,
            selectedAnime = anime,
            currentCategory = currentCategory
        )
    }

    fun updateAnimeStatus(category: String) {
        val anime = _uiState.value.selectedAnime ?: return

        viewModelScope.launch {
            repository.addToWatchList(anime, category)
            dismissDialog()
        }
    }

    fun removeAnimeFromList() {
        val anime = _uiState.value.selectedAnime ?: return

        viewModelScope.launch {
            repository.removeFromWatchList(anime.id)
            dismissDialog()
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(
            isDialogVisible = false,
            selectedAnime = null,
            currentCategory = null
        )
    }
}
