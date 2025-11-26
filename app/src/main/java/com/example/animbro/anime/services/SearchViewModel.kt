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

    private val _results = MutableStateFlow<List<Anime>>(emptyList())
    val results: StateFlow<List<Anime>> = _results

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _results.value = emptyList()
            } else {
                val list = repository.searchAnime(query)
                _results.value = list
            }
        }
    }
}
