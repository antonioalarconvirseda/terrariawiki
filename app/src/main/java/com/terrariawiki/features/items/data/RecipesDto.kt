package com.terrariawiki.features.items.data

import kotlinx.serialization.Serializable

@Serializable
data class RecipesResponse(
    val cargoquery: List<RecipesCargoItem> = emptyList()
)

@Serializable
data class RecipesCargoItem(
    val title: RecipeDto
)

@Serializable
data class RecipeDto(
    val result: String = "",
    val amount: String = "",
    val station: String = "",
    val ings: String = ""
)
