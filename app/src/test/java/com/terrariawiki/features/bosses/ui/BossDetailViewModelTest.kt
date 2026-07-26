package com.terrariawiki.features.bosses.ui

import com.terrariawiki.features.bosses.domain.Boss
import com.terrariawiki.features.bosses.domain.GetBossByNameUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class BossDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getBossByName: GetBossByNameUseCase

    private val sampleBoss = Boss(
        name = "Betsy",
        types = listOf("boss"),
        imageFilename = "Betsy.png",
        life = "50000",
        defense = "38",
        damage = "80",
        knockback = "100%",
        bannerName = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getBossByName = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits Ready when boss is found`() = runTest(testDispatcher) {
        coEvery { getBossByName("Betsy") } returns Result.success(sampleBoss)
        val vm = BossDetailViewModel(getBossByName)

        vm.load("Betsy")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is BossDetailViewModel.UiState.Ready)
        assertEquals("Betsy", (state as BossDetailViewModel.UiState.Ready).boss.name)
    }

    @Test
    fun `emits Error when boss is null`() = runTest(testDispatcher) {
        coEvery { getBossByName("Nope") } returns Result.success(null)
        val vm = BossDetailViewModel(getBossByName)

        vm.load("Nope")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is BossDetailViewModel.UiState.Error)
    }

    @Test
    fun `emits Error on failure`() = runTest(testDispatcher) {
        coEvery { getBossByName("Betsy") } returns Result.failure(RuntimeException("network down"))
        val vm = BossDetailViewModel(getBossByName)

        vm.load("Betsy")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is BossDetailViewModel.UiState.Error)
        assertEquals("network down", (state as BossDetailViewModel.UiState.Error).message)
    }
}
