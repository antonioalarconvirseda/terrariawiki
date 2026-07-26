package com.terrariawiki.features.bosses.domain

import com.terrariawiki.features.bosses.data.BossesRepository

class GetBossByNameUseCase(
    private val repository: BossesRepository
) {
    suspend operator fun invoke(name: String): Result<Boss?> =
        repository.getByName(name)
}
