package com.terrariawiki.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object TerrariaApiConfig {
    const val HOST = "terraria.wiki.gg"
    const val BASE_PATH = "/api.php"
    const val HOST_IMAGE_BASE = "https://terraria.wiki.gg/wiki/Special:Redirect/file"
    const val IMAGES_BASE = "https://terraria.wiki.gg/images"
    const val USER_AGENT =
        "TerrariaWikiApp/0.1.0 " +
            "(https://github.com/antonioalarconvirseda/terrariawiki; " +
            "antonioalarconvirseda@hotmail.com)"
}

fun buildItemImageUrl(filename: String): String {
    val underscored = filename.replace(' ', '_')
    val encoded = java.net.URLEncoder.encode(underscored, "UTF-8")
        .replace("%28", "(")
        .replace("%29", ")")
    return "${TerrariaApiConfig.IMAGES_BASE}/$encoded"
}

private val terrariaJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    encodeDefaults = true
}

fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(terrariaJson)
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 15_000
    }

    install(Logging) {
        level = LogLevel.INFO
        logger = object : Logger {
            override fun log(message: String) {
                android.util.Log.d("TerrariaHttp", message)
            }
        }
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = TerrariaApiConfig.HOST
        }
        header("User-Agent", TerrariaApiConfig.USER_AGENT)
    }
}
