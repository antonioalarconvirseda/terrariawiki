package com.terrariawiki.features.bosses.data

interface BossesApi {
    suspend fun queryBosses(): CargoResponse<BossDto>

    suspend fun getByName(name: String): CargoResponse<BossDto>
}
