package com.example.animbro.anime.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animbro.domain.models.Anime
import com.example.animbro.repositories.AnimeRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AnimeRepositoryImp
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = _uiState.value.copy(results = emptyList())
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true)
                try {
                    val list = repository.searchAnime(query)
                    _uiState.value = _uiState.value.copy(results = list, isLoading = false)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun onEditClick(anime: Anime) {
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

data class SearchUiState(
    val results: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedAnime: Anime? = null,
    val currentAnimeStatus: String? = null,
    val isDialogVisible: Boolean = false
)
