package com.terrariawiki.features.items.domain

data class Item(
    val name: String,
    val types: List<String>,
    val rarity: Int,
    val tooltip: String?,
    val damage: Int?,
    val defense: Int?,
    val knockback: Float?,
    val useTime: Int?,
    val sellRaw: String?,
    val internalName: String?,
    val wikiId: Int?,
    val imageFilename: String?
) {
    val hasStats: Boolean
        get() = damage != null || defense != null || useTime != null

    val primaryType: String?
        get() = types.firstOrNull()
}
