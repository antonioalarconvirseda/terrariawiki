package com.terrariawiki.features.items.domain

import kotlinx.coroutines.flow.Flow

class GetItemsUseCase(
    private val repository: ItemsRepository
) {
    operator fun invoke(): Flow<List<Item>> = repository.observeItems()

    suspend fun refresh(): Result<Unit> = repository.refresh()
}
