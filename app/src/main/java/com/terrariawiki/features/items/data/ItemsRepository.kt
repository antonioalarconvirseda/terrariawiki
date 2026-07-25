package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.domain.ItemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ItemsRepository {
    fun observeItems(): Flow<List<Item>>
    suspend fun refresh(): Result<Unit>
    suspend fun search(query: String): Result<List<Item>>
    suspend fun getByName(name: String): Result<Item?>

    fun observeByCategory(category: ItemCategory): Flow<List<Item>>
    suspend fun refreshByCategory(category: ItemCategory): Result<Unit>
    suspend fun loadMoreByCategory(category: ItemCategory): Result<List<Item>>
    fun hasMoreFor(category: ItemCategory): StateFlow<Boolean>
}

class ItemsRepositoryImpl(
    private val api: ItemsApi
) : ItemsRepository {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    private val cacheMutex = Mutex()

    private val byCategoryFlows: MutableMap<ItemCategory, MutableStateFlow<List<Item>>> = mutableMapOf()
    private val hasMoreMap: MutableMap<ItemCategory, MutableStateFlow<Boolean>> = mutableMapOf()

    override fun observeItems(): Flow<List<Item>> = _items.asStateFlow()

    override suspend fun refresh(): Result<Unit> = runCatching {
        val response = api.queryItems(
            fields = ItemsApiImpl.DEFAULT_FIELDS,
            limit = 50
        )
        val items = response.cargoquery.map { it.title.toDomain() }
        cacheMutex.withLock {
            _items.value = items
        }
    }

    override suspend fun search(query: String): Result<List<Item>> = runCatching {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            _items.value
        } else {
            _items.value.filter { item ->
                item.name.contains(trimmed, ignoreCase = true) ||
                    item.types.any { it.contains(trimmed, ignoreCase = true) }
            }
        }
    }

    override suspend fun getByName(name: String): Result<Item?> = runCatching {
        if (_items.value.isNotEmpty()) {
            _items.value.firstOrNull { it.name.equals(name, ignoreCase = true) }
        } else {
            val response = api.getByName(name)
            response.cargoquery.firstOrNull()?.title?.toDomain()
        }
    }

    override fun observeByCategory(category: ItemCategory): Flow<List<Item>> =
        flowFor(category).asStateFlow()

    override suspend fun refreshByCategory(category: ItemCategory): Result<Unit> = runCatching {
        val response = api.queryByCategory(category = category, limit = 50, offset = 0)
        val items = response.cargoquery.map { it.title.toDomain() }
        flowFor(category).value = items
        hasMoreFor(category).value = items.size >= 50
    }

    override suspend fun loadMoreByCategory(category: ItemCategory): Result<List<Item>> =
        runCatching {
            val current = flowFor(category).value
            val offset = current.size
            val response = api.queryByCategory(category = category, limit = 50, offset = offset)
            val moreItems = response.cargoquery.map { it.title.toDomain() }
            flowFor(category).value = current + moreItems
            hasMoreFor(category).value = moreItems.size >= 50
            moreItems
        }

    override fun hasMoreFor(category: ItemCategory): StateFlow<Boolean> =
        hasMoreMap.getOrPut(category) { MutableStateFlow(true) }

    private fun flowFor(category: ItemCategory): MutableStateFlow<List<Item>> =
        byCategoryFlows.getOrPut(category) { MutableStateFlow(emptyList()) }
}
