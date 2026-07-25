package com.terrariawiki.features.items.domain

import com.terrariawiki.features.items.data.ItemsRepository
import kotlinx.coroutines.flow.Flow

class GetItemsUseCase(
    private val repository: ItemsRepository
) {
    operator fun invoke(): Flow<List<Item>> = repository.observeItems()

    suspend fun refresh(): Result<Unit> = repository.refresh()
}
