package com.example.animbro.anime.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val trendingAnime: List<Anime> = emptyList(),
    val topRankedAnime: List<Anime> = emptyList(),
    val upcomingAnime: List<Anime> = emptyList(),
    val allAnime: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadAllAnimeData()
    }

    fun loadAllAnimeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val trendingDeferred = launch { loadTrendingAnime() }
                val topRankedDeferred = launch { loadTopRankedAnime() }
                val upcomingDeferred = launch { loadUpcomingAnime() }
                val allAnimeDeferred = launch { loadAllAnime() }
                trendingDeferred.join()
                topRankedDeferred.join()
                upcomingDeferred.join()
                allAnimeDeferred.join()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    private suspend fun loadTrendingAnime() {
        try {
            val anime = repository.getPopularAnime(limit = 10)
            _uiState.value = _uiState.value.copy(trendingAnime = anime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadTopRankedAnime() {
        try {
            val anime = repository.getTopRatedAnime(limit = 10)
            _uiState.value = _uiState.value.copy(topRankedAnime = anime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadUpcomingAnime() {
        try {
            // Using "upcoming" for anime not yet aired
            val anime = repository.getUpcomingAnime(limit = 10)
            _uiState.value = _uiState.value.copy(upcomingAnime = anime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadAllAnime() {
        try {
            val anime = repository.getTopRatedAnime(limit = 10)
            _uiState.value = _uiState.value.copy(allAnime = anime)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onRefresh() {
        loadAllAnimeData()
    }
}