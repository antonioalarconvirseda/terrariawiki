package com.terrariawiki.features.events.domain

/**
 * Terraria's Cargo API has no table for events (unlike Items/NPCs) — events only exist as
 * regular wiki pages grouped under MediaWiki categories, and no reliable rule extracts a
 * representative image from their wikitext (tested: neither the `{{flavor text|...}}` template
 * nor the first `[[File:...]]` link consistently points at the event's icon). This list is
 * curated by hand instead, with every image filename verified to exist on terraria.wiki.gg.
 * Terraria's event roster changes rarely, so the maintenance cost of a static list is low.
 */
object EventCatalog {
    val all: List<Event> = listOf(
        Event("Blood Moon", "Bestiary Blood Moon.png", EventCategory.RANDOM, "Blood Moon"),
        Event("Solar Eclipse", "Bestiary Eclipse.png", EventCategory.RANDOM, "Solar Eclipse"),
        Event("Sandstorm", "Sandstorm.gif", EventCategory.RANDOM, "Sandstorm"),
        Event("Rain", "Weather Radio.png", EventCategory.RANDOM, "Rain"),
        Event("Goblin Army", "Goblin Army Icon.png", EventCategory.SUMMONED, "Goblin Army"),
        Event("Pirate Invasion", "Pirate Invasion Icon.png", EventCategory.SUMMONED, "Pirate Invasion"),
        Event("Frost Legion", "Frost Legion Icon.png", EventCategory.SUMMONED, "Frost Legion"),
        Event("Frost Moon", "Frost Moon Icon.png", EventCategory.SUMMONED, "Frost Moon"),
        Event("Pumpkin Moon", "Pumpkin Moon Icon.png", EventCategory.SUMMONED, "Pumpkin Moon"),
        Event("Martian Madness", "Martian Madness Icon.png", EventCategory.SUMMONED, "Martian Madness"),
        Event("Lunar Events", "Celestial Sigil.png", EventCategory.SUMMONED, "Lunar Events"),
        Event("Old One's Army", "Eternia Crystal.png", EventCategory.SUMMONED, "Old One's Army"),
        Event("Chinese New Year", "Firework Fountain.png", EventCategory.SEASONAL, "Chinese New Year"),
        Event("St. Patrick's Day", "Lucky Coin.png", EventCategory.SEASONAL, "St. Patrick's Day")
    )
}
