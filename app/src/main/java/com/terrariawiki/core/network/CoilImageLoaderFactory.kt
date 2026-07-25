package com.terrariawiki.core.network

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import java.util.concurrent.atomic.AtomicLong
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

object CoilConfig {
    const val LOG_TAG = "CoilHttp"
    const val MAX_CONCURRENT_PER_HOST = 10
    const val MAX_TOTAL_REQUESTS = 20
    const val CACHE_BYTES = 50L * 1024 * 1024
    const val TOKEN_PERIOD_MS = 100L
    const val MAX_RETRIES_429 = 1
    const val RETRY_SLEEP_MS = 500L
}

class TokenBucketInterceptor(
    private val periodMs: Long = CoilConfig.TOKEN_PERIOD_MS
) : Interceptor {
    private val nextSlot = AtomicLong(0L)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val now = System.currentTimeMillis()
        var reserved = 0L
        while (true) {
            val current = nextSlot.get()
            val candidate = maxOf(current, now) + periodMs
            if (nextSlot.compareAndSet(current, candidate)) {
                reserved = candidate
                break
            }
        }
        val waitMs = (reserved - now).coerceAtLeast(0L)
        if (waitMs > 0) {
            Thread.sleep(waitMs)
        }
        return chain.proceed(request)
    }
}

class UserAgentInterceptor(
    private val userAgent: String = TerrariaApiConfig.USER_AGENT,
    private val maxRetries429: Int = CoilConfig.MAX_RETRIES_429,
    private val retrySleepMs: Long = CoilConfig.RETRY_SLEEP_MS
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = if (original.header("User-Agent") == null) {
            original.newBuilder().header("User-Agent", userAgent).build()
        } else {
            original
        }
        var response = chain.proceed(request)
        Log.d(CoilConfig.LOG_TAG, "${response.code} ${request.url.toString().take(120)}")
        var attempts = 0
        while (response.code == 429 && attempts < maxRetries429) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull()
            val sleepMs = retryAfter?.times(1000)?.coerceAtMost(retrySleepMs) ?: retrySleepMs
            Log.w(
                CoilConfig.LOG_TAG,
                "429 rate-limited, retrying in ${sleepMs}ms ${request.url.toString().take(80)}"
            )
            response.close()
            Thread.sleep(sleepMs)
            response = chain.proceed(request)
            Log.d(CoilConfig.LOG_TAG, "retry -> ${response.code} ${request.url.toString().take(120)}")
            attempts++
        }
        return response
    }
}

fun createCoilImageLoader(context: Context): ImageLoader {
    val dispatcher = Dispatcher().apply {
        maxRequests = CoilConfig.MAX_TOTAL_REQUESTS
        maxRequestsPerHost = CoilConfig.MAX_CONCURRENT_PER_HOST
    }
    val okHttpClient = OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .addInterceptor(TokenBucketInterceptor())
        .addInterceptor(UserAgentInterceptor())
        .build()
    return ImageLoader.Builder(context)
        .okHttpClient { okHttpClient }
        .memoryCache {
            MemoryCache.Builder(context).maxSizePercent(0.25).build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizeBytes(CoilConfig.CACHE_BYTES)
                .build()
        }
        .crossfade(true)
        .respectCacheHeaders(true)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()
}
