package com.terrariawiki.features.bosses.domain


class GetBossByNameUseCase(
    private val repository: BossesRepository
) {
    suspend operator fun invoke(name: String): Result<Boss?> =
        repository.getByName(name)
}
