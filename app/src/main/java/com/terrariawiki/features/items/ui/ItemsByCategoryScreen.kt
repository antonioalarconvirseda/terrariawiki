package com.terrariawiki.features.items.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.terrariawiki.features.items.domain.ItemCategory
import com.terrariawiki.features.items.ui.components.EmptyState
import com.terrariawiki.features.items.ui.components.ErrorState
import com.terrariawiki.features.items.ui.components.ItemCard
import com.terrariawiki.features.items.ui.components.LoadingState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsByCategoryScreen(
    category: ItemCategory,
    onItemClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CategoryViewModel = koinViewModel(parameters = { parametersOf(category) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val hasMore by viewModel.hasMore.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = (uiState as? CategoryViewModel.UiState.Ready)?.items?.size ?: 0
            lastVisible >= totalItems - 5 && hasMore && !isLoadingMore && totalItems > 0
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is CategoryViewModel.UiState.Loading -> LoadingState()
                is CategoryViewModel.UiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.retry() }
                )
                is CategoryViewModel.UiState.Empty -> EmptyState(
                    title = "Sin items",
                    message = "No se encontraron items en esta categoría."
                )
                is CategoryViewModel.UiState.Ready -> {
                    val items = state.items
                    LazyColumn(
                        state = listState,
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
                        if (isLoadingMore) {
                            item { LoadingFooter() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp,
            modifier = Modifier.padding(8.dp)
        )
    }
}
