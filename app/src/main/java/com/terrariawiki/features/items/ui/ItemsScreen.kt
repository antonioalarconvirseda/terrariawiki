package com.terrariawiki.features.items.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.terrariawiki.features.items.ui.components.EmptyState
import com.terrariawiki.features.items.ui.components.ErrorState
import com.terrariawiki.features.items.ui.components.ItemCard
import com.terrariawiki.features.items.ui.components.LoadingState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    onItemClick: (name: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val search by viewModel.searchState.collectAsStateWithLifecycle()
    val filtered by viewModel.filteredItems.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Terraria Wiki",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            SearchBar(
                query = search.query,
                onQueryChange = viewModel::onQueryChange,
                onClear = viewModel::clearQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            when (uiState) {
                is ItemsViewModel.UiState.Loading -> LoadingState()
                is ItemsViewModel.UiState.Error -> {
                    val msg = (uiState as ItemsViewModel.UiState.Error).message
                    ErrorState(message = msg, onRetry = viewModel::retry)
                }
                is ItemsViewModel.UiState.Empty -> {
                    EmptyState(
                        title = "Sin items aún",
                        message = "La wiki no devolvió items. Intenta refrescar."
                    )
                }
                is ItemsViewModel.UiState.Ready -> {
                    if (filtered.isEmpty() && search.query.isNotBlank()) {
                        EmptyState(
                            title = "Sin resultados",
                            message = "Ningún item coincide con «${search.query}»."
                        )
                    } else {
                        ItemsList(items = filtered, onItemClick = onItemClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        placeholder = {
            Text("Buscar item por nombre o tipo…")
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun ItemsList(
    items: List<com.terrariawiki.features.items.domain.Item>,
    onItemClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = items,
                key = { index, item -> "${item.internalName ?: item.name}_$index" }
            ) { _, item ->
                ItemCard(
                    item = item,
                    onClick = { onItemClick(item.name) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Mostrando ${items.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
