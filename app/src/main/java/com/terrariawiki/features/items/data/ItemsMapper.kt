package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Item

private const val LIST_DELIMITER = "^"

/** MediaWiki Cargo returns the literal string "None" (not JSON null) for unset scalar fields. */
private fun String?.cargoValueOrNull(): String? =
    this?.takeIf { it.isNotBlank() && it != "None" }

fun ItemDto.toDomain(): Item {
    val types = type.split(LIST_DELIMITER).filter { it.isNotBlank() }
    val categories = listCat?.split(LIST_DELIMITER)?.filter { it.isNotBlank() }.orEmpty()
    return Item(
        name = name,
        types = types,
        rarity = rare.toIntOrNull() ?: 0,
        tooltip = tooltip.cargoValueOrNull()?.stripHtml(),
        damage = damage?.toIntOrNull(),
        defense = defense?.toIntOrNull(),
        knockback = knockback?.toFloatOrNull(),
        useTime = usetime?.toIntOrNull(),
        critical = critical?.toIntOrNull(),
        velocity = velocity?.toFloatOrNull(),
        autoSwing = autoswing == "1",
        sellRaw = extractSellValue(sell),
        buyRaw = extractSellValue(buy),
        internalName = internalname.cargoValueOrNull(),
        wikiId = itemid?.toIntOrNull(),
        imageFilename = imageFile.cargoValueOrNull()
            ?: image?.extractFileName(),
        listCategories = categories,
        stack = stack.cargoValueOrNull(),
        hardmode = hardmode == "1"
    )
}

fun String.stripHtml(): String =
    replace(Regex("<[^>]+>"), "")
        .replace("&nbsp;", " ")
        .replace("&#32;", " ")
        .replace("&amp;", "&")
        .replace("&#39;", "'")
        .trim()

private val SELL_TITLE = Regex("""title="(\d+)\s+(\w+)\s+Coins"""")

private val COIN_ABBR = mapOf(
    "Copper" to "CC",
    "Silver" to "SC",
    "Gold" to "GC",
    "Platinum" to "PC"
)

fun extractSellValue(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    val match = SELL_TITLE.find(raw)
    if (match != null) {
        val qty = match.groupValues[1]
        val coin = COIN_ABBR[match.groupValues[2]] ?: "CC"
        return "$qty $coin"
    }
    return raw.stripHtml().takeIf { it.isNotBlank() }
}

private val FILE_REF = Regex("""\[\[File:([^\]|]+)""")

fun String.extractFileName(): String? {
    val match = FILE_REF.find(this)
    return match?.groupValues?.getOrNull(1)?.trim()
}
