package com.terrariawiki.features.items.ui

import app.cash.turbine.test
import com.terrariawiki.features.items.data.ItemsRepository
import com.terrariawiki.features.items.domain.Item
import com.terrariawiki.features.items.domain.ItemCategory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ItemsRepository

    private val sampleItems = listOf(
        sampleItem("Wood Sword", 1),
        sampleItem("Copper Sword", 2),
        sampleItem("Iron Sword", 3)
    )

    private val moreItems = listOf(
        sampleItem("Gold Sword", 4),
        sampleItem("Platinum Sword", 5)
    )

    private fun sampleItem(name: String, idx: Int) = Item(
        name = name,
        types = listOf("weapon"),
        rarity = 0,
        tooltip = null,
        damage = 10, defense = null, knockback = null, useTime = 20,
        critical = 4, velocity = 12f, autoSwing = true,
        sellRaw = null, buyRaw = null,
        internalName = "Internal_$idx", wikiId = idx,
        imageFilename = "$name.png",
        listCategories = listOf("Melee weapons"),
        stack = "1", hardmode = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.observeByCategory(any()) } returns MutableStateFlow(sampleItems)
        every { repository.hasMoreFor(any()) } returns MutableStateFlow(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh emits Ready with items on success`() = runTest(testDispatcher) {
        coEvery { repository.refreshByCategory(any()) } returns Result.success(Unit)
        val vm = CategoryViewModel(ItemCategory.WEAPONS, repository)

        vm.uiState.test {
            var state = awaitItem()
            while (state !is CategoryViewModel.UiState.Ready &&
                state !is CategoryViewModel.UiState.Error
            ) {
                state = awaitItem()
            }
            assertTrue("expected Ready, got $state", state is CategoryViewModel.UiState.Ready)
            val ready = state as CategoryViewModel.UiState.Ready
            assertEquals(3, ready.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh emits Error on failure`() = runTest(testDispatcher) {
        coEvery { repository.refreshByCategory(any()) } returns Result.failure(RuntimeException("fail"))
        val vm = CategoryViewModel(ItemCategory.ARMOR, repository)

        vm.uiState.test {
            var state = awaitItem()
            while (state !is CategoryViewModel.UiState.Error) state = awaitItem()
            val err = state as CategoryViewModel.UiState.Error
            assertEquals("fail", err.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadMore appends items and toggles isLoadingMore`() = runTest(testDispatcher) {
        coEvery { repository.refreshByCategory(any()) } returns Result.success(Unit)
        coEvery { repository.loadMoreByCategory(any()) } returns Result.success(moreItems)
        val vm = CategoryViewModel(ItemCategory.WEAPONS, repository)

        vm.loadMore()
        assertEquals(false, vm.isLoadingMore.value)
    }
}
