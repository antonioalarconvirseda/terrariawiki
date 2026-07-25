package com.terrariawiki.features.items.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CargoResponse<T>(
    val cargoquery: List<CargoItem<T>> = emptyList()
)

@Serializable
data class CargoItem<T>(
    val title: T
)

@Serializable
data class SearchResponse(
    val query: SearchQuery = SearchQuery()
)

@Serializable
data class SearchQuery(
    val search: List<SearchHit> = emptyList(),
    @SerialName("searchinfo") val searchInfo: SearchInfo = SearchInfo()
)

@Serializable
data class SearchHit(
    val title: String = "",
    val pageid: Int = 0,
    val snippet: String = ""
)

@Serializable
data class SearchInfo(
    val totalhits: Int = 0
)

@Serializable
data class ItemDto(
    val name: String = "",
    val type: String = "",
    val rare: String = "0",
    val sell: String? = null,
    val damage: String? = null,
    val defense: String? = null,
    val knockback: String? = null,
    val usetime: String? = null,
    val tooltip: String? = null,
    val internalname: String? = null,
    val itemid: String? = null,
    @SerialName("listcat") val listCat: String? = null,
    val tag: String? = null,
    val placeable: String? = null,
    val stack: String? = null,
    val hardmode: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("imagefile") val imageFile: String? = null,
    val critical: String? = null,
    val velocity: String? = null,
    val autoswing: String? = null,
    val buy: String? = null
)
