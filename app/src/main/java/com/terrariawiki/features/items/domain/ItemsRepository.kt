package com.terrariawiki.features.items.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ItemsRepository {
    fun observeItems(): Flow<List<Item>>
    suspend fun refresh(): Result<Unit>
    suspend fun search(query: String): Result<List<Item>>
    suspend fun getByName(name: String): Result<Item?>

    fun observeByCategory(category: ItemCategory): Flow<List<Item>>
    fun observeByCategoryDirect(category: ItemCategory): StateFlow<List<Item>>
    suspend fun refreshByCategory(category: ItemCategory): Result<Unit>
    suspend fun loadMoreByCategory(category: ItemCategory): Result<List<Item>>
    fun hasMoreFor(category: ItemCategory): StateFlow<Boolean>

    suspend fun getRecipes(name: String): Result<List<Recipe>>

    suspend fun searchAll(query: String, limit: Int = 25): Result<List<SearchResult>>
}
