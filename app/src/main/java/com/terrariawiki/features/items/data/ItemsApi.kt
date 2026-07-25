package com.terrariawiki.features.items.data

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
}
