package com.terrariawiki.features.bosses.domain

import com.terrariawiki.features.bosses.data.BossesRepository
import kotlinx.coroutines.flow.StateFlow

class GetBossesUseCase(
    private val repository: BossesRepository
) {
    operator fun invoke(): StateFlow<List<Boss>> = repository.observeBosses()

    suspend fun refresh(): Result<Unit> = repository.refresh()
}
