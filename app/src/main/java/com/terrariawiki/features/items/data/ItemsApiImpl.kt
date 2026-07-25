package com.terrariawiki.features.items.data

import com.terrariawiki.core.network.TerrariaApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ItemsApiImpl(
    private val client: HttpClient
) : ItemsApi {

    override suspend fun queryItems(
        fields: String,
        where: String?,
        limit: Int,
        offset: Int,
        orderBy: String?
    ): CargoResponse<ItemDto> = client.get(TerrariaApiConfig.BASE_PATH) {
        parameter("action", "cargoquery")
        parameter("tables", "Items")
        parameter("fields", fields)
        if (where != null) parameter("where", where)
        if (orderBy != null) parameter("order_by", orderBy)
        parameter("limit", limit)
        parameter("offset", offset)
        parameter("format", "json")
    }.body()

    override suspend fun searchItems(query: String, limit: Int): SearchResponse =
        client.get(TerrariaApiConfig.BASE_PATH) {
            parameter("action", "query")
            parameter("list", "search")
            parameter("srsearch", query)
            parameter("srlimit", limit)
            parameter("format", "json")
        }.body()

    override suspend fun getByName(name: String): CargoResponse<ItemDto> = queryItems(
        fields = DEFAULT_FIELDS,
        where = "name='${name.replace("'", "''")}'",
        limit = 1
    )

    companion object {
        const val DEFAULT_FIELDS =
            "name,type,rare,sell,damage,defense,knockback,usetime,tooltip," +
                "internalname,itemid,listcat,tag,placeable,stack,hardmode,image"
    }
}
