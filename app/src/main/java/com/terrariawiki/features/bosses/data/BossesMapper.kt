package com.terrariawiki.features.bosses.data

import com.terrariawiki.features.bosses.domain.Boss

private const val LIST_DELIMITER = "^"

private fun String?.cargoValueOrNull(): String? =
    this?.takeIf { it.isNotBlank() && it != "None" }

fun BossDto.toDomain(): Boss {
    val types = type.split(LIST_DELIMITER).filter { it.isNotBlank() }
    return Boss(
        name = nameraw,
        types = types,
        imageFilename = image?.extractFileName(),
        life = life.cargoValueOrNull()?.stripWikitext(),
        defense = defense.cargoValueOrNull()?.stripWikitext(),
        damage = damage.cargoValueOrNull()?.stripWikitext(),
        knockback = knockback.cargoValueOrNull()?.stripWikitext(),
        bannerName = bannername.cargoValueOrNull()?.stripWikitext()
    )
}

private val WIKI_CATEGORY = Regex("""\[\[Category:[^\]]*\]\]""")
private val WIKI_PIPED_LINK = Regex("""\[\[[^\]|]*\|([^\]]*)\]\]""")
private val WIKI_PLAIN_LINK = Regex("""\[\[([^\]]*)\]\]""")

/** Strips both HTML tags and MediaWiki `[[...]]` link/category syntax from Cargo NPC stat fields. */
fun String.stripWikitext(): String =
    replace(WIKI_CATEGORY, "")
        .replace(WIKI_PIPED_LINK, "$1")
        .replace(WIKI_PLAIN_LINK, "$1")
        .replace(Regex("<[^>]+>"), " ")
        .replace("&nbsp;", " ")
        .replace("&#32;", " ")
        .replace("&amp;", "&")
        .replace("&#39;", "'")
        .replace(Regex("\\s+"), " ")
        .trim()

private val FILE_REF = Regex("""\[\[File:([^\]|]+)""")

fun String.extractFileName(): String? {
    val match = FILE_REF.find(this)
    return match?.groupValues?.getOrNull(1)?.trim()
}
