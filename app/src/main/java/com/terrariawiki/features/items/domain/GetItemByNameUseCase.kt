package com.terrariawiki.features.items.domain

import com.terrariawiki.features.items.data.ItemsRepository

class GetItemByNameUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(name: String): Result<Item?> =
        repository.getByName(name)
}
