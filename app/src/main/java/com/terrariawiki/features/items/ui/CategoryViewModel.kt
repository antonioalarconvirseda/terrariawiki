package com.terrariawiki.features.items.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.items.data.ItemsRepository
import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.domain.ItemCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val category: ItemCategory,
    private val repository: ItemsRepository
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val items: List<Item>) : UiState
        data object Empty : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    val hasMore: StateFlow<Boolean> = repository.hasMoreFor(category)

    val items: StateFlow<List<Item>> = repository.observeByCategory(category)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.refreshByCategory(category).fold(
                onSuccess = {
                    val current = items.first()
                    _uiState.value = if (current.isEmpty()) UiState.Empty
                    else UiState.Ready(current)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Error desconocido al cargar la categoría"
                    )
                }
            )
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value || !hasMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            repository.loadMoreByCategory(category).fold(
                onSuccess = {
                    val current = items.first()
                    _uiState.value = if (current.isEmpty()) UiState.Empty
                    else UiState.Ready(current)
                    _isLoadingMore.value = false
                },
                onFailure = { _isLoadingMore.value = false }
            )
        }
    }

    fun retry() = refresh()
}
