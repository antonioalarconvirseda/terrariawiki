package com.terrariawiki.features.bosses.data

import com.terrariawiki.features.bosses.domain.Boss
import com.terrariawiki.features.bosses.domain.BossesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BossesRepositoryImpl(
    private val api: BossesApi
) : BossesRepository {

    private val _bosses = MutableStateFlow<List<Boss>>(emptyList())
    private val cacheMutex = Mutex()

    override fun observeBosses(): StateFlow<List<Boss>> = _bosses.asStateFlow()

    override suspend fun refresh(): Result<Unit> = runCatching {
        val response = api.queryBosses()
        val bosses = response.cargoquery.map { it.title.toDomain() }
        cacheMutex.withLock {
            _bosses.value = bosses
        }
    }

    override suspend fun getByName(name: String): Result<Boss?> = runCatching {
        _bosses.value.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: api.getByName(name).cargoquery.firstOrNull()?.title?.toDomain()
    }
}
