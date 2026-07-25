package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Item
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemsMapperTest {

    @Test
    fun `maps simple item with default rarity`() {
        val dto = ItemDto(name = "Wood", type = "block^crafting material")

        val item: Item = dto.toDomain()

        assertEquals("Wood", item.name)
        assertEquals(listOf("block", "crafting material"), item.types)
        assertEquals(0, item.rarity)
        assertNull(item.damage)
        assertNull(item.defense)
        assertNull(item.tooltip)
    }

    @Test
    fun `parses rarity as integer`() {
        val dto = ItemDto(name = "Terra Blade", type = "weapon^melee", rare = "5")

        val item = dto.toDomain()

        assertEquals(5, item.rarity)
    }

    @Test
    fun `parses numeric stats and ignores empty strings`() {
        val dto = ItemDto(
            name = "Slime Staff",
            damage = "8",
            defense = "",
            knockback = "1.5",
            usetime = "22"
        )

        val item = dto.toDomain()

        assertEquals(8, item.damage)
        assertNull(item.defense)
        assertEquals(1.5f, item.knockback!!, 0.001f)
        assertEquals(22, item.useTime)
    }

    @Test
    fun `strips HTML from tooltip`() {
        val dto = ItemDto(
            name = "Star",
            tooltip = "<span class=\"tooltip\">Cae del cielo</span>"
        )

        val item = dto.toDomain()

        assertEquals("Cae del cielo", item.tooltip)
    }

    @Test
    fun `extracts file name from wikitext image`() {
        val dto = ItemDto(
            name = "Wood",
            image = "[[File:Wood.png|64x64px]]"
        )

        val item = dto.toDomain()

        assertEquals("Wood.png", item.imageFilename)
    }

    @Test
    fun `returns null image filename when no image`() {
        val dto = ItemDto(name = "Nameless")

        val item = dto.toDomain()

        assertNull(item.imageFilename)
    }

    @Test
    fun `parses wikiId from itemid string`() {
        val dto = ItemDto(name = "Wood", itemid = "2702")

        val item = dto.toDomain()

        assertEquals(2702, item.wikiId)
    }

    @Test
    fun `falls back to rarity 0 on unparseable string`() {
        val dto = ItemDto(name = "X", rare = "not-a-number")

        val item = dto.toDomain()

        assertEquals(0, item.rarity)
    }

    @Test
    fun `hasStats returns true when any stat is present`() {
        val dto = ItemDto(name = "X", damage = "5")
        val item = dto.toDomain()
        assertTrue(item.hasStats)
    }

    @Test
    fun `sellRaw strips HTML and extracts numeric sell value with coin type`() {
        val dto = ItemDto(
            name = "Wood",
            sell = "<span class=\"coin\" title=\"60 Copper Coins\" data-sort-value=\"60\">" +
                "<span class=\"cc\">60<i> CC</i></span></span>"
        )
        val item = dto.toDomain()
        assertEquals("60 CC", item.sellRaw)
    }

    @Test
    fun `sellRaw parses title attribute for all four coin types`() {
        val cases = listOf(
            """<span class="coin" title="60 Copper Coins" data-sort-value="60">..."" to "60 CC",
            """<span class="coin" title="2 Silver Coins" data-sort-value="200">..."" to "2 SC",
            """<span class="coin" title="20 Gold Coins" data-sort-value="200000">..."" to "20 GC",
            """<span class="coin" title="5 Platinum Coins" data-sort-value="5000000">..."" to "5 PC",
        )
        for ((raw, expected) in cases) {
            val dto = ItemDto(name = "X", sell = raw)
            assertEquals("Failed for input: $raw", expected, dto.toDomain().sellRaw)
        }
    }

    @Test
    fun `sellRaw is null when sell is blank or null`() {
        val dto1 = ItemDto(name = "X", sell = "")
        val dto2 = ItemDto(name = "Y", sell = null)
        assertNull(dto1.toDomain().sellRaw)
        assertNull(dto2.toDomain().sellRaw)
    }

    @Test
    fun `prefers imagefile over image wikitext when both are present`() {
        val dto = ItemDto(
            name = "Terra Blade",
            imagefile = "Terra Blade.png",
            image = "[[File:Obsolete.png|caption]]"
        )
        val item = dto.toDomain()
        assertEquals("Terra Blade.png", item.imageFilename)
    }

    @Test
    fun `falls back to image wikitext when imagefile is blank`() {
        val dto = ItemDto(
            name = "X",
            imagefile = "",
            image = "[[File:FromWikitext.png|caption]]"
        )
        val item = dto.toDomain()
        assertEquals("FromWikitext.png", item.imageFilename)
    }
}
