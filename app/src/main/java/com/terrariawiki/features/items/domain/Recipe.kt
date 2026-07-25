package com.terrariawiki.features.items.domain

data class Recipe(
    val result: String,
    val amount: Int,
    val station: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val name: String,
    val quantity: Int
)
