package com.terrariawiki.features.items.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.items.data.ItemsRepository
import com.terrariawiki.features.items.domain.SearchResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class SearchViewModel(
    private val repository: ItemsRepository
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Ready(val results: List<SearchResult>) : UiState
        data object Empty : UiState
        data class Error(val message: String) : UiState
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(FlowPreview::class)
    val uiState: StateFlow<UiState> = _query
        .debounce(250)
        .distinctUntilChanged()
        .mapLatest { q ->
            if (q.isBlank()) {
                UiState.Idle
            } else {
                repository.searchAll(q).fold(
                    onSuccess = { results ->
                        if (results.isEmpty()) UiState.Empty else UiState.Ready(results)
                    },
                    onFailure = { error ->
                        UiState.Error(error.message ?: "Error desconocido en la búsqueda")
                    }
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Idle)

    fun onQueryChange(q: String) {
        _query.update { q }
    }

    fun clearQuery() {
        _query.update { "" }
    }
}
