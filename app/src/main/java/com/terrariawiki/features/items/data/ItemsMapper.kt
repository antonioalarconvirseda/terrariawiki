package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Item

private const val LIST_DELIMITER = "^"

fun ItemDto.toDomain(): Item = Item(
    name = name,
    types = type.split(LIST_DELIMITER).filter { it.isNotBlank() },
    rarity = rare.toIntOrNull() ?: 0,
    tooltip = tooltip?.takeIf { it.isNotBlank() }?.stripHtml(),
    damage = damage?.toIntOrNull(),
    defense = defense?.toIntOrNull(),
    knockback = knockback?.toFloatOrNull(),
    useTime = usetime?.toIntOrNull(),
    sellRaw = sell?.takeIf { it.isNotBlank() },
    internalName = internalname?.takeIf { it.isNotBlank() },
    wikiId = itemid?.toIntOrNull(),
    imageFilename = image?.extractFileName()
)

fun String.stripHtml(): String =
    replace(Regex("<[^>]+>"), "")
        .replace("&nbsp;", " ")
        .replace("&#32;", " ")
        .replace("&amp;", "&")
        .replace("&#39;", "'")
        .trim()

private val SELL_NUMERIC = Regex("data-sort-value=\"(\\d+)\"")

fun extractSellValue(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    val match = SELL_NUMERIC.find(raw)
    val numeric = match?.groupValues?.getOrNull(1)
    return when {
        numeric == null -> raw.stripHtml()
        else -> "$numeric ${inferCoin(raw)}"
    }
}

private fun inferCoin(raw: String): String = when {
    "Platinum" in raw || " PC" in raw -> "PC"
    "Gold" in raw || " GC" in raw -> "GC"
    "Silver" in raw || " SC" in raw -> "SC"
    "Copper" in raw || " CC" in raw -> "CC"
    else -> "CC"
}

private val FILE_REF = Regex("""\[\[File:([^\]|]+)""")

fun String.extractFileName(): String? {
    val match = FILE_REF.find(this)
    return match?.groupValues?.getOrNull(1)?.trim()
}
