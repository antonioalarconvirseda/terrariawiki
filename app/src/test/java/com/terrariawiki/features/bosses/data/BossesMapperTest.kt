package com.terrariawiki.features.bosses.data

import com.terrariawiki.features.bosses.domain.Boss
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BossesMapperTest {

    @Test
    fun `maps simple boss with types`() {
        val dto = BossDto(nameraw = "Deerclops", type = "boss")

        val boss: Boss = dto.toDomain()

        assertEquals("Deerclops", boss.name)
        assertEquals(listOf("boss"), boss.types)
    }

    @Test
    fun `splits multiple types by delimiter`() {
        val dto = BossDto(nameraw = "Duke Fishron", type = "boss^aquatic")

        val boss = dto.toDomain()

        assertEquals(listOf("boss", "aquatic"), boss.types)
    }

    @Test
    fun `extracts file name from wikitext image`() {
        val dto = BossDto(
            nameraw = "Betsy",
            image = "<span class=\"npcimg\">[[File:Animated Betsy.gif|link=]]</span>"
        )

        val boss = dto.toDomain()

        assertEquals("Animated Betsy.gif", boss.imageFilename)
    }

    @Test
    fun `returns null image filename when no image`() {
        val dto = BossDto(nameraw = "Nameless")

        val boss = dto.toDomain()

        assertNull(boss.imageFilename)
    }

    @Test
    fun `strips html tags and wiki links from stat fields`() {
        val dto = BossDto(
            nameraw = "Nebula Pillar",
            life = "<span class=\"npcstat\"><span class=\"m-all\">20000</span></span>"
        )

        val boss = dto.toDomain()

        assertEquals("20000", boss.life)
    }

    @Test
    fun `converts piped wiki links to their display text`() {
        val dto = BossDto(
            nameraw = "Brain of Cthulhu",
            knockback = "<span class=\"m-expert\">[[Expert Mode|<abbr class=\"mode-exclusive expert\" title=\"Expert Mode \">60%</abbr>]]</span>"
        )

        val boss = dto.toDomain()

        assertEquals("60%", boss.knockback)
    }

    @Test
    fun `removes category links entirely`() {
        val dto = BossDto(
            nameraw = "Betsy",
            damage = "80 [[Category:Pages with information based on outdated versions of Terraria's source code]]"
        )

        val boss = dto.toDomain()

        assertEquals("80", boss.damage)
    }

    @Test
    fun `treats Cargo literal None as absent for stat fields`() {
        val dto = BossDto(
            nameraw = "X",
            life = "None",
            defense = "None"
        )

        val boss = dto.toDomain()

        assertNull(boss.life)
        assertNull(boss.defense)
    }

    @Test
    fun `hasStats is true when any stat present`() {
        val boss = Boss(
            name = "X", types = emptyList(), imageFilename = null,
            life = "100", defense = null, damage = null, knockback = null, bannerName = null
        )
        assertTrue(boss.hasStats)
    }

    @Test
    fun `hasStats is false when no stat present`() {
        val boss = Boss(
            name = "X", types = emptyList(), imageFilename = null,
            life = null, defense = null, damage = null, knockback = null, bannerName = null
        )
        assertEquals(false, boss.hasStats)
    }
}
