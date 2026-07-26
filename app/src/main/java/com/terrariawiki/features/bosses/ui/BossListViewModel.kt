package com.terrariawiki.features.bosses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.bosses.domain.BossesRepository
import com.terrariawiki.features.bosses.domain.Boss
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BossListViewModel(
    private val repository: BossesRepository
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val bosses: List<Boss>) : UiState
        data object Empty : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.refresh().fold(
                onSuccess = {
                    val current = repository.observeBosses().value
                    _uiState.value = if (current.isEmpty()) UiState.Empty
                    else UiState.Ready(current)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Error desconocido al cargar los jefes"
                    )
                }
            )
        }
    }

    fun retry() = refresh()
}
