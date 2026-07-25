package com.terrariawiki.features.items.ui

import com.terrariawiki.features.items.data.ItemsRepository
import com.terrariawiki.features.items.domain.SearchResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        assertEquals(SearchViewModel.UiState.Idle, vm.uiState.value)
    }

    @Test
    fun `search with results emits Ready`() = runTest(testDispatcher) {
        coEvery { repository.searchAll("terra", 25) } returns Result.success(sampleResults)
        val vm = SearchViewModel(repository)

        vm.onQueryChange("terra")
        val state = vm.uiState.value
        assertTrue("expected Ready, got $state", state is SearchViewModel.UiState.Ready)
        val ready = state as SearchViewModel.UiState.Ready
        assertEquals(2, ready.results.size)
    }

    @Test
    fun `search with no results emits Empty`() = runTest(testDispatcher) {
        coEvery { repository.searchAll("xyzzz", 25) } returns Result.success(emptyList())
        val vm = SearchViewModel(repository)

        vm.onQueryChange("xyzzz")
        val state = vm.uiState.value
        assertTrue("expected Empty, got $state", state is SearchViewModel.UiState.Empty)
    }

    @Test
    fun `search failure emits Error`() = runTest(testDispatcher) {
        coEvery { repository.searchAll("error", 25) } returns Result.failure(RuntimeException("boom"))
        val vm = SearchViewModel(repository)

        vm.onQueryChange("error")
        val state = vm.uiState.value
        assertTrue("expected Error, got $state", state is SearchViewModel.UiState.Error)
        assertEquals("boom", (state as SearchViewModel.UiState.Error).message)
    }
}
