package com.terrariawiki.features.bosses.ui

import com.terrariawiki.features.bosses.data.BossesRepository
import com.terrariawiki.features.bosses.domain.Boss
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class BossListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: BossesRepository
    private val bossesFlow = MutableStateFlow<List<Boss>>(emptyList())

    private val sampleBosses = listOf(
        sampleBoss("Betsy"),
        sampleBoss("Deerclops")
    )

    private fun sampleBoss(name: String) = Boss(
        name = name,
        types = listOf("boss"),
        imageFilename = "$name.png",
        life = "50000",
        defense = "38",
        damage = "80",
        knockback = "100%",
        bannerName = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.observeBosses() } returns bossesFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh emits Ready with bosses on success`() = runTest(testDispatcher) {
        coEvery { repository.refresh() } coAnswers {
            bossesFlow.value = sampleBosses
            Result.success(Unit)
        }
        val vm = BossListViewModel(repository)

        val state = vm.uiState.value
        assertTrue("expected Ready, got $state", state is BossListViewModel.UiState.Ready)
        assertEquals(2, (state as BossListViewModel.UiState.Ready).bosses.size)
    }

    @Test
    fun `refresh emits Error on failure`() = runTest(testDispatcher) {
        coEvery { repository.refresh() } returns Result.failure(RuntimeException("fail"))
        val vm = BossListViewModel(repository)

        val state = vm.uiState.value
        assertTrue("expected Error, got $state", state is BossListViewModel.UiState.Error)
        assertEquals("fail", (state as BossListViewModel.UiState.Error).message)
    }

    @Test
    fun `refresh emits Empty when no bosses returned`() = runTest(testDispatcher) {
        coEvery { repository.refresh() } returns Result.success(Unit)
        val vm = BossListViewModel(repository)

        val state = vm.uiState.value
        assertTrue("expected Empty, got $state", state is BossListViewModel.UiState.Empty)
    }
}
