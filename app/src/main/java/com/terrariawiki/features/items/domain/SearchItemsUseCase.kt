package com.terrariawiki.features.items.domain


class SearchItemsUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(query: String): Result<List<Item>> =
        repository.search(query)
}
