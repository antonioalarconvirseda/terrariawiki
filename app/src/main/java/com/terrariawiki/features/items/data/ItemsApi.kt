package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.ItemCategory

interface ItemsApi {
    suspend fun queryItems(
        fields: String,
        where: String? = null,
        limit: Int = 50,
        offset: Int = 0,
        orderBy: String? = null
    ): CargoResponse<ItemDto>

    suspend fun searchItems(query: String, limit: Int = 25): SearchResponse

    suspend fun getByName(name: String): CargoResponse<ItemDto>

    suspend fun queryByCategory(
        category: ItemCategory,
        fields: String = ItemsApiImpl.DEFAULT_FIELDS,
        limit: Int = 50,
        offset: Int = 0
    ): CargoResponse<ItemDto>
}
