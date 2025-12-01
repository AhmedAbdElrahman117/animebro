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
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _favoriteAnime = MutableStateFlow<List<Anime>>(emptyList())
    val favoriteAnime: StateFlow<List<Anime>> = _favoriteAnime.asStateFlow()

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            // Fetching a limit of 10 favorites for the profile row
            repository.getUserFavouriteAnime().collect { favorites ->
                _favoriteAnime.value = favorites
            }
        }
    }

    // Dialog State
    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible.asStateFlow()

    private val _currentCategory = MutableStateFlow<String?>(null)
    val currentCategory: StateFlow<String?> = _currentCategory.asStateFlow()

    private var _selectedAnime: Anime? = null

    fun openStatusDialog(anime: Anime) {
        viewModelScope.launch {
            _selectedAnime = anime
            _currentCategory.value = repository.getAnimeCategory(anime.id)
            _isDialogVisible.value = true
        }
    }

    fun dismissDialog() {
        _isDialogVisible.value = false
        _selectedAnime = null
        _currentCategory.value = null
    }

    fun updateAnimeStatus(category: String) {
        val anime = _selectedAnime ?: return
        viewModelScope.launch {
            try {
                repository.addToWatchList(anime, category)
                dismissDialog()
                // Refresh favorites in case status change affects it (though favorites are separate)
                getFavorites()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeAnimeFromList() {
        val anime = _selectedAnime ?: return
        viewModelScope.launch {
            try {
                repository.removeFromWatchList(anime.id)
                dismissDialog()
                getFavorites()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch {
            try {
                repository.toggleFavourite(anime)
                // getFavorites() // No longer needed as Flow updates automatically
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addAnimeToWatchList(anime: Anime, category: String) {
        viewModelScope.launch {
            try {
                repository.addToWatchList(anime, category)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            try {
                repository.clearLocalDatabase()

                _favoriteAnime.value = emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
