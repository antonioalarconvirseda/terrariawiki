package com.terrariawiki.features.events.domain

enum class EventCategory(val displayName: String) {
    RANDOM("Aleatorio"),
    SEASONAL("Estacional"),
    SUMMONED("Invocado")
}

data class Event(
    val name: String,
    val imageFilename: String,
    val category: EventCategory,
    val wikiPageTitle: String
)
