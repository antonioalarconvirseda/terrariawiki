package com.terrariawiki.features.items.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.items.domain.GetItemsUseCase
import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.domain.SearchItemsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemsViewModel(
    private val getItems: GetItemsUseCase,
    private val searchItems: SearchItemsUseCase
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val items: List<Item>) : UiState
        data object Empty : UiState
        data class Error(val message: String) : UiState
    }

    data class SearchState(
        val query: String = "",
        val isSearching: Boolean = false
    )

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    @OptIn(FlowPreview::class)
    private val searchResults: StateFlow<List<Item>> = _searchState
        .debounce(250)
        .distinctUntilChanged { old, new -> old.query == new.query }
        .combine(getItems()) { search, items ->
            if (search.query.isBlank()) items
            else items.filter { item ->
                item.name.contains(search.query, ignoreCase = true) ||
                    item.types.any { it.contains(search.query, ignoreCase = true) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun onQueryChange(query: String) {
        _searchState.update { it.copy(query = query, isSearching = query.isNotBlank()) }
    }

    fun clearQuery() {
        _searchState.update { it.copy(query = "", isSearching = false) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getItems.refresh().fold(
                onSuccess = {
                    val items = getItems().first()
                    _uiState.value = if (items.isEmpty()) UiState.Empty
                    else UiState.Ready(items)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Error desconocido al cargar items"
                    )
                }
            )
        }
    }

    val filteredItems: StateFlow<List<Item>> = searchResults

    fun retry() = refresh()

    @Suppress("unused")
    fun searchNow() {
        val q = _searchState.value.query
        if (q.isBlank()) return
        viewModelScope.launch {
            searchItems(q).onFailure { error ->
                _uiState.value = UiState.Error(error.message ?: "Error en búsqueda")
            }
        }
    }
}
