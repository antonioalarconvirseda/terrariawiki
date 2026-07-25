package com.terrariawiki.features.items.ui

import app.cash.turbine.test
import com.terrariawiki.features.items.domain.GetItemByNameUseCase
import com.terrariawiki.features.items.domain.GetItemsUseCase
import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.domain.SearchItemsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ItemsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getItems: GetItemsUseCase
    private lateinit var searchItems: SearchItemsUseCase
    private lateinit var getByName: GetItemByNameUseCase

    private val sampleItems = listOf(
        Item(
            name = "Wood",
            types = listOf("block", "material"),
            rarity = 0,
            tooltip = null,
            damage = null, defense = null, knockback = null, useTime = null,
            critical = null, velocity = null, autoSwing = false,
            sellRaw = null, buyRaw = null,
            internalName = "Wood", wikiId = 2702,
            imageFilename = "Wood.png",
            listCategories = emptyList(),
            stack = null, hardmode = false
        ),
        Item(
            name = "Stone Block",
            types = listOf("block"),
            rarity = 0,
            tooltip = null,
            damage = null, defense = null, knockback = null, useTime = null,
            critical = null, velocity = null, autoSwing = false,
            sellRaw = null, buyRaw = null,
            internalName = "StoneBlock", wikiId = 2703,
            imageFilename = null,
            listCategories = emptyList(),
            stack = null, hardmode = false
        ),
        Item(
            name = "Terra Blade",
            types = listOf("weapon", "melee"),
            rarity = 5,
            tooltip = "Legendaria",
            damage = 190, defense = null, knockback = 6.5f, useTime = 16,
            critical = 4, velocity = 12f, autoSwing = true,
            sellRaw = "20 GC", buyRaw = null,
            internalName = "TerraBlade", wikiId = 2704,
            imageFilename = "Terra Blade.png",
            listCategories = listOf("broadswords", "Melee weapons"),
            stack = "1", hardmode = true
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getItems = mockk()
        searchItems = mockk()
        getByName = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun stubItemsLoaded() {
        val itemsFlow = MutableStateFlow(sampleItems)
        coEvery { getItems.invoke() } returns itemsFlow
        coEvery { getItems.refresh() } returns Result.success(Unit)
    }

    @Test
    fun `refresh emits Ready with items on success`() = runTest(testDispatcher) {
        stubItemsLoaded()
        val vm = ItemsViewModel(getItems, searchItems)

        vm.uiState.test {
            // initial Loading then transitions
            var state = awaitItem()
            while (state !is ItemsViewModel.UiState.Ready && state !is ItemsViewModel.UiState.Error) {
                state = awaitItem()
            }
            assertTrue("expected Ready, got $state", state is ItemsViewModel.UiState.Ready)
            val ready = state as ItemsViewModel.UiState.Ready
            assertEquals(3, ready.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh emits Error on failure`() = runTest(testDispatcher) {
        coEvery { getItems.invoke() } returns flowOf(emptyList())
        coEvery { getItems.refresh() } returns Result.failure(RuntimeException("network down"))
        val vm = ItemsViewModel(getItems, searchItems)

        vm.uiState.test {
            var state = awaitItem()
            while (state !is ItemsViewModel.UiState.Error) {
                state = awaitItem()
            }
            val err = state as ItemsViewModel.UiState.Error
            assertEquals("network down", err.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onQueryChange updates search state`() = runTest(testDispatcher) {
        stubItemsLoaded()
        val vm = ItemsViewModel(getItems, searchItems)
        advanceUntilIdle()

        vm.searchState.test {
            awaitItem()  // initial empty
            vm.onQueryChange("Wood")
            val updated = awaitItem()
            assertEquals("Wood", updated.query)
            assertTrue(updated.isSearching)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearQuery resets search state`() = runTest(testDispatcher) {
        stubItemsLoaded()
        val vm = ItemsViewModel(getItems, searchItems)
        advanceUntilIdle()

        vm.onQueryChange("test")
        vm.clearQuery()

        val state = vm.searchState.value
        assertEquals("", state.query)
        assertEquals(false, state.isSearching)
    }

    @Test
    fun `ItemDetailViewModel emits Ready when item is found`() = runTest(testDispatcher) {
        coEvery { getByName("Wood") } returns Result.success(sampleItems[0])
        val repo = mockk<com.terrariawiki.features.items.data.ItemsRepository>(relaxed = true)
        coEvery { repo.getRecipes(any()) } returns Result.success(emptyList())
        val vm = ItemDetailViewModel(getByName, repo)

        vm.load("Wood")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is ItemDetailViewModel.UiState.Ready)
        assertEquals("Wood", (state as ItemDetailViewModel.UiState.Ready).item.name)
    }

    @Test
    fun `ItemDetailViewModel emits Error when item is null`() = runTest(testDispatcher) {
        coEvery { getByName("Nope") } returns Result.success(null)
        val repo = mockk<com.terrariawiki.features.items.data.ItemsRepository>(relaxed = true)
        val vm = ItemDetailViewModel(getByName, repo)

        vm.load("Nope")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is ItemDetailViewModel.UiState.Error)
    }
}
