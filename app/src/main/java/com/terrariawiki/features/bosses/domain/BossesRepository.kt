package com.terrariawiki.features.bosses.domain

import kotlinx.coroutines.flow.StateFlow

interface BossesRepository {
    fun observeBosses(): StateFlow<List<Boss>>
    suspend fun refresh(): Result<Unit>
    suspend fun getByName(name: String): Result<Boss?>
}
