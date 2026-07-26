package com.terrariawiki.features.items.domain


class GetItemByNameUseCase(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(name: String): Result<Item?> =
        repository.getByName(name)
}
