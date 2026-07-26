package com.terrariawiki.features.bosses.data

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
data class BossDto(
    val nameraw: String = "",
    val type: String = "",
    val image: String? = null,
    val life: String? = null,
    val defense: String? = null,
    val damage: String? = null,
    val knockback: String? = null,
    val banner: String? = null,
    val bannername: String? = null
)
