package com.example.animbro.anime.services

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animbro.domain.models.Anime
import com.example.animbro.domain.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val anime: Anime? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDialogVisible: Boolean = false,
    val currentAnimeStatus: String? = null,
    val isFavourite: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val animeId: Int = savedStateHandle.get<Int>("animeId") ?: -1


    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        if (animeId != -1) {
            loadAnimeDetails()
        }
    }

    fun loadAnimeDetails(id: Int = animeId) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val anime = repository.getAnimeDetails(id)
                if (anime != null) {
                    _uiState.value = _uiState.value.copy(
                        anime = anime,
                        isLoading = false
                    )

                    checkLocalStatus(anime.id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Anime not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    private fun checkLocalStatus(id: Int) {
        viewModelScope.launch {
            val status = repository.getAnimeCategory(id)

            val isFav = repository.isAnimeFavourite(id)

            _uiState.value = _uiState.value.copy(
                currentAnimeStatus = status,
                isFavourite = isFav
            )
        }
    }

    fun onAddClick() {
        _uiState.value = _uiState.value.copy(isDialogVisible = true)
        // Re-check status just in case
        if (_uiState.value.anime != null) {
            checkLocalStatus(_uiState.value.anime!!.id)
        }
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(isDialogVisible = false)
    }

    fun onFavoriteClick() {
        val anime = _uiState.value.anime ?: return

        viewModelScope.launch {
            repository.toggleFavourite(anime)

            _uiState.value = _uiState.value.copy(
                isFavourite = !_uiState.value.isFavourite
            )
        }
    }

    fun updateAnimeStatus(category: String) {
        val anime = _uiState.value.anime ?: return
        viewModelScope.launch {
            repository.addToWatchList(anime, category)
            _uiState.value = _uiState.value.copy(
                currentAnimeStatus = category,
                isDialogVisible = false
            )
        }
    }

    fun removeAnimeFromList() {
        val anime = _uiState.value.anime ?: return
        viewModelScope.launch {
            repository.removeFromWatchList(anime.id)
            _uiState.value = _uiState.value.copy(
                currentAnimeStatus = null,
                isDialogVisible = false
            )
        }
    }
}
