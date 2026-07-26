package com.terrariawiki.features.items.ui

import app.cash.turbine.test
import com.terrariawiki.features.items.domain.ItemsRepository
import com.terrariawiki.features.items.domain.SearchResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ItemsRepository

    private val sampleResults = listOf(
        SearchResult("Terra Blade", 5976, "A powerful sword"),
        SearchResult("Terraspark Boots", 11231, "Boots for movement")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest(testDispatcher) {
        val vm = SearchViewModel(repository)
        assertEquals(SearchViewModel.UiState.Idle, vm.uiState.value)
    }

    @Test
    fun `search with empty query stays Idle`() = runTest(testDispatcher) {
        val vm = SearchViewModel(repository)
        vm.onQueryChange("")
        runCurrent()
        assertEquals(SearchViewModel.UiState.Idle, vm.uiState.value)
    }

    @Test
    fun `search with results emits Ready`() = runTest(testDispatcher) {
        coEvery { repository.searchAll("terra", 25) } returns Result.success(sampleResults)
        val vm = SearchViewModel(repository)

        vm.uiState.test {
            assertEquals(SearchViewModel.UiState.Idle, awaitItem())
            vm.onQueryChange("terra")
            advanceTimeBy(300)
            runCurrent()
            var state = awaitItem()
            while (state !is SearchViewModel.UiState.Ready) {
                state = awaitItem()
            }
            val ready = state as SearchViewModel.UiState.Ready
            assertEquals(2, ready.results.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search with no results emits Empty`() = runTest(testDispatcher) {
        coEvery { repository.searchAll("xyzzz", 25) } returns Result.success(emptyList())
        val vm = SearchViewModel(repository)

        vm.uiState.test {
            assertEquals(SearchViewModel.UiState.Idle, awaitItem())
            vm.onQueryChange("xyzzz")
            advanceTimeBy(300)
            runCurrent()
            var state = awaitItem()
            while (state !is SearchViewModel.UiState.Empty) {
                state = awaitItem()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search failure emits Error`() = runTest(testDispatcher) {
        coEvery { repository.searchAll("error", 25) } returns Result.failure(RuntimeException("boom"))
        val vm = SearchViewModel(repository)

        vm.uiState.test {
            assertEquals(SearchViewModel.UiState.Idle, awaitItem())
            vm.onQueryChange("error")
            advanceTimeBy(300)
            runCurrent()
            var state = awaitItem()
            while (state !is SearchViewModel.UiState.Error) {
                state = awaitItem()
            }
            assertEquals("boom", (state as SearchViewModel.UiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
