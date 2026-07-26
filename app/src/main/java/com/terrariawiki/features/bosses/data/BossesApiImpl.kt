package com.terrariawiki.features.bosses.data

import com.terrariawiki.core.network.TerrariaApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class BossesApiImpl(
    private val client: HttpClient
) : BossesApi {

    override suspend fun queryBosses(): CargoResponse<BossDto> = client.get(TerrariaApiConfig.BASE_PATH) {
        parameter("action", "cargoquery")
        parameter("tables", "NPCs")
        parameter("fields", FIELDS)
        parameter("where", "type HOLDS 'boss'")
        parameter("limit", 100)
        parameter("format", "json")
    }.body()

    override suspend fun getByName(name: String): CargoResponse<BossDto> =
        client.get(TerrariaApiConfig.BASE_PATH) {
            parameter("action", "cargoquery")
            parameter("tables", "NPCs")
            parameter("fields", FIELDS)
            parameter("where", "nameraw='${name.replace("'", "''")}'")
            parameter("limit", 1)
            parameter("format", "json")
        }.body()

    companion object {
        const val FIELDS = "nameraw,type,image,life,defense,damage,knockback,banner,bannername"
    }
}
