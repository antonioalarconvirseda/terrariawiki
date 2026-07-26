package com.terrariawiki.features.items.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terrariawiki.features.items.domain.ItemsRepository
import com.terrariawiki.features.items.domain.GetItemByNameUseCase
import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.domain.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val getItemByName: GetItemByNameUseCase,
    private val repository: ItemsRepository
) : ViewModel() {

    sealed interface UiState {
        data object Loading : UiState
        data class Ready(val item: Item) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    fun load(name: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getItemByName(name).fold(
                onSuccess = { item ->
                    if (item != null) {
                        _uiState.value = UiState.Ready(item)
                        repository.getRecipes(item.name).onSuccess { recipes ->
                            _recipes.value = recipes
                        }
                    } else {
                        _uiState.value = UiState.Error("No se encontró «$name»")
                    }
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
