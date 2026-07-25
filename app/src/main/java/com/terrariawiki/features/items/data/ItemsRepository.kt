package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface ItemsRepository {
    fun observeItems(): Flow<List<Item>>
    suspend fun refresh(): Result<Unit>
    suspend fun search(query: String): Result<List<Item>>
    suspend fun getByName(name: String): Result<Item?>
}

class ItemsRepositoryImpl(
    private val api: ItemsApi
) : ItemsRepository {

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    private val cacheMutex = Mutex()

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
}
