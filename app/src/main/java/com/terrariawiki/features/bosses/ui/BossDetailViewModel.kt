package com.terrariawiki.features.bosses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.bosses.domain.Boss
import com.terrariawiki.features.bosses.domain.GetBossByNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BossDetailViewModel(
    private val getBossByName: GetBossByNameUseCase
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val boss: Boss) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(name: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getBossByName(name).fold(
                onSuccess = { boss ->
                    _uiState.value = if (boss != null) UiState.Ready(boss)
                    else UiState.Error("No se encontró «$name»")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Error desconocido al cargar el jefe"
                    )
                }
            )
        }
    }
}
