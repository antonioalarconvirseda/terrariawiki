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
        Item("Wood", listOf("block", "material"), 0, null, null, null, null, null, null, "Wood", 2702, "Wood.png"),
        Item("Stone Block", listOf("block"), 0, null, null, null, null, null, null, "StoneBlock", 2703, null),
        Item("Terra Blade", listOf("weapon", "melee"), 5, "Legendaria", 190, null, 6.5f, 16, null, "TerraBlade", 2704, null)
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
        val vm = ItemDetailViewModel(getByName)

        vm.load("Wood")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is ItemDetailViewModel.UiState.Ready)
        assertEquals("Wood", (state as ItemDetailViewModel.UiState.Ready).item.name)
    }

    @Test
    fun `ItemDetailViewModel emits Error when item is null`() = runTest(testDispatcher) {
        coEvery { getByName("Nope") } returns Result.success(null)
        val vm = ItemDetailViewModel(getByName)

        vm.load("Nope")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is ItemDetailViewModel.UiState.Error)
    }
}
