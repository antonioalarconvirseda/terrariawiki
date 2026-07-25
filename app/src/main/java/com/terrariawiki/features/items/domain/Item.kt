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
    val critical: Int?,
    val velocity: Float?,
    val autoSwing: Boolean,
    val sellRaw: String?,
    val buyRaw: String?,
    val internalName: String?,
    val wikiId: Int?,
    val imageFilename: String?,
    val listCategories: List<String>,
    val stack: String?,
    val hardmode: Boolean
) {
    val hasStats: Boolean
        get() = damage != null || defense != null || useTime != null ||
            critical != null || velocity != null

    val primaryType: String?
        get() = types.firstOrNull()

    val isWeapon: Boolean
        get() = damage != null && (types.any { it.contains("weapon", ignoreCase = true) } ||
            listCategories.any { it.contains("weapon", ignoreCase = true) })
}
