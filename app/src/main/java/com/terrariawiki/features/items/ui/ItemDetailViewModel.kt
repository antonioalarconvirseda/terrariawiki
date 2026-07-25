package com.terrariawiki.features.items.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.items.domain.GetItemByNameUseCase
import com.terrariawiki.features.items.domain.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val getItemByName: GetItemByNameUseCase
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val item: Item) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(name: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getItemByName(name).fold(
                onSuccess = { item ->
                    _uiState.value = if (item != null) UiState.Ready(item)
                    else UiState.Error("No se encontró «$name»")
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Error desconocido al cargar el item"
                    )
                }
            )
        }
    }
}
