package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Ingredient
import com.terrariawiki.features.items.domain.Recipe

private const val RECIPES_LIST_DELIMITER = "^"
private const val FIELD_WRAP = "\u00a6"

fun RecipeDto.toDomain(): Recipe {
    val ingredientsList = ings
        .split(RECIPES_LIST_DELIMITER)
        .filter { it.isNotBlank() }
        .map { entry ->
            val parts = entry.split(FIELD_WRAP)
            val name = parts.getOrNull(1).orEmpty()
            val qty = parts.getOrNull(2)?.toIntOrNull() ?: 1
            Ingredient(name = name, quantity = qty)
        }
    return Recipe(
        result = result,
        amount = amount.toIntOrNull() ?: 1,
        station = station,
        ingredients = ingredientsList
    )
}
