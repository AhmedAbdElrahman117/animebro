package com.example.animbro.anime.services

import android.util.Log
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
            repository.getUserFavouriteAnime().collect { favorites ->
                _favoriteAnime.value = favorites
            }
        }

    }

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible.asStateFlow()

    private val _currentCategory = MutableStateFlow<String?>(null)
    val currentCategory: StateFlow<String?> = _currentCategory.asStateFlow()

    private val _favLoadingIds = MutableStateFlow<Set<Int>>(emptySet())
    val favLoadingIds: StateFlow<Set<Int>> = _favLoadingIds.asStateFlow()

    private val _addLoadingIds = MutableStateFlow<Set<Int>>(emptySet())
    val addLoadingIds: StateFlow<Set<Int>> = _addLoadingIds.asStateFlow()

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
            _addLoadingIds.value = _addLoadingIds.value + anime.id
            try {
                repository.addToWatchList(anime, category)
                dismissDialog()
                getFavorites()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _addLoadingIds.value = _addLoadingIds.value - anime.id
            }
        }
    }

    fun removeAnimeFromList() {
        val anime = _selectedAnime ?: return
        viewModelScope.launch {
            _addLoadingIds.value = _addLoadingIds.value + anime.id
            try {
                repository.removeFromWatchList(anime.id)
                dismissDialog()
                getFavorites()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _addLoadingIds.value = _addLoadingIds.value - anime.id
            }
        }
    }

    fun toggleFavorite(anime: Anime) {
        viewModelScope.launch {
            _favLoadingIds.value = _favLoadingIds.value + anime.id
            try {
                repository.toggleFavourite(anime)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _favLoadingIds.value = _favLoadingIds.value - anime.id
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
