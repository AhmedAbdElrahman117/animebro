package com.example.animbro.anime.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class HomeUiState(
    val trendingAnime: List<Anime> = emptyList(),
    val topRankedAnime: List<Anime> = emptyList(),
    val upcomingAnime: List<Anime> = emptyList(),
    val favouriteAnime: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val randomAnimeId: Int? = null,
    val isRandomLoading: Boolean = false,
    val randomError: String? = null,
    val selectedAnime: Anime? = null,
    val currentAnimeStatus: String? = null,
    val isDialogVisible: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()


    init {
        loadAllAnimeData()
    }

    fun loadAllAnimeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                coroutineScope {
                    launch { loadTrendingAnime() }
                    launch { loadTopRankedAnime() }
                    launch { loadUpcomingAnime() }
                    launch { loadFavouriteAnime() }
                }
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
        val anime = repository.getPopularAnime(limit = 10)
        _uiState.value = _uiState.value.copy(trendingAnime = anime)
    }

    private suspend fun loadTopRankedAnime() {
        val anime = repository.getTopRatedAnime(limit = 10)
        _uiState.value = _uiState.value.copy(topRankedAnime = anime)
    }

    private suspend fun loadUpcomingAnime() {
        // Using "upcoming" for anime not yet aired
        val anime = repository.getUpcomingAnime(limit = 10)
        _uiState.value = _uiState.value.copy(upcomingAnime = anime)
    }

    private suspend fun loadFavouriteAnime() {
        val anime = repository.getFavouritesAnime(limit = 10)
        _uiState.value = _uiState.value.copy(favouriteAnime = anime)
    }

    fun findRandomAnime() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRandomLoading = true,
                randomError = null,
                randomAnimeId = null
            )

            var validIdFound = false
            var attempts = 0
            val maxAttempts = 20

            while (!validIdFound && attempts < maxAttempts) {
                val randomId = (1..60000).random()

                try {
                    val anime = repository.getAnimeDetails(randomId)

                    if (
                        anime != null &&
                        anime.score > 0 &&
                        anime.rating != "rx" &&
                        anime.rating != "r+"
                    ) {
                        validIdFound = true
                        _uiState.value = _uiState.value.copy(
                            isRandomLoading = false,
                            randomAnimeId = anime.id
                        )
                    }
                } catch (e: Exception) {

                }

                attempts++
            }

            if (!validIdFound) {
                _uiState.value = _uiState.value.copy(
                    isRandomLoading = false,
                    randomError = "Could not find a random anime. Please try again."
                )
            }
        }
    }

    fun onRandomAnimeNavigated() {
        _uiState.value = _uiState.value.copy(randomAnimeId = null)
    }

    fun onRefresh() {
        loadAllAnimeData()
    }

    fun onAddClick(anime: Anime) {
        viewModelScope.launch {
            val status = repository.getAnimeCategory(anime.id)

            _uiState.value = _uiState.value.copy(
                selectedAnime = anime,
                currentAnimeStatus = status,
                isDialogVisible = true
            )
        }
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
            selectedAnime = null
        )
    }

}
