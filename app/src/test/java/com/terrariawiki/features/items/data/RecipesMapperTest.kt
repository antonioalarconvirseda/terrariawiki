package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.Ingredient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipesMapperTest {

    private const val WRAP = "\u00a6"
    private const val LIST = "^"

    @Test
    fun `parses single ingredient recipe`() {
        val dto = RecipeDto(
            result = "Wooden Sword",
            amount = "1",
            station = "Work Bench",
            ings = "${WRAP}Wood${WRAP}"
        )
        val recipe = dto.toDomain()
        assertEquals("Wooden Sword", recipe.result)
        assertEquals(1, recipe.amount)
        assertEquals("Work Bench", recipe.station)
        assertEquals(1, recipe.ingredients.size)
        assertEquals(Ingredient("Wood", 1), recipe.ingredients.first())
    }

    @Test
    fun `parses single ingredient with quantity`() {
        val dto = RecipeDto(
            result = "Statue",
            amount = "1",
            station = "Heavy Assembler",
            ings = "${WRAP}Stone Block${WRAP}50"
        )
        val recipe = dto.toDomain()
        assertEquals(Ingredient("Stone Block", 50), recipe.ingredients.first())
    }

    @Test
    fun `parses multi-ingredient recipe`() {
        val dto = RecipeDto(
            result = "True Excalibur",
            amount = "1",
            station = "Mythril Anvil",
            ings = "${WRAP}Broken Hero Sword${WRAP}1${LIST}${WRAP}Excalibur${WRAP}1"
        )
        val recipe = dto.toDomain()
        assertEquals(2, recipe.ingredients.size)
        assertEquals(Ingredient("Broken Hero Sword", 1), recipe.ingredients[0])
        assertEquals(Ingredient("Excalibur", 1), recipe.ingredients[1])
    }

    @Test
    fun `parses multi-ingredient with varied quantities`() {
        val dto = RecipeDto(
            result = "Mythril Anvil",
            amount = "1",
            station = "Iron Anvil",
            ings = "${WRAP}Mythril Bar${WRAP}10${LIST}${WRAP}Iron Anvil${WRAP}1"
        )
        val recipe = dto.toDomain()
        assertEquals(Ingredient("Mythril Bar", 10), recipe.ingredients[0])
        assertEquals(Ingredient("Iron Anvil", 1), recipe.ingredients[1])
    }

    @Test
    fun `falls back to amount 1 when amount field is zero or empty`() {
        val dto1 = RecipeDto(
            result = "X",
            amount = "0",
            station = "Bench",
            ings = "${WRAP}Wood${WRAP}"
        )
        assertEquals(1, dto1.toDomain().amount)

        val dto2 = RecipeDto(
            result = "Y",
            amount = "",
            station = "Bench",
            ings = "${WRAP}Wood${WRAP}"
        )
        assertEquals(1, dto2.toDomain().amount)
    }

    @Test
    fun `returns empty ingredients list when ings is empty`() {
        val dto = RecipeDto(result = "X", amount = "1", station = "Bench", ings = "")
        val recipe = dto.toDomain()
        assertTrue(recipe.ingredients.isEmpty())
    }

    @Test
    fun `parses three ingredient recipe with apostrophe in name`() {
        val dto = RecipeDto(
            result = "Terra Blade",
            amount = "1",
            station = "Mythril Anvil",
            ings = "${WRAP}True Night's Edge${WRAP}1${LIST}${WRAP}True Excalibur${WRAP}1${LIST}${WRAP}Broken Hero Sword${WRAP}1"
        )
        val recipe = dto.toDomain()
        assertEquals(3, recipe.ingredients.size)
        assertEquals(Ingredient("True Night's Edge", 1), recipe.ingredients[0])
        assertEquals(Ingredient("True Excalibur", 1), recipe.ingredients[1])
        assertEquals(Ingredient("Broken Hero Sword", 1), recipe.ingredients[2])
    }
}
