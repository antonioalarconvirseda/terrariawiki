package com.terrariawiki.core.network

import java.util.Collections
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TokenBucketInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var arrivedTimes: MutableList<Long>
    private lateinit var arrivalInterceptor: Interceptor
    private lateinit var tokenBucket: TokenBucketInterceptor
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        server = MockWebServer().apply { start() }
        arrivedTimes = Collections.synchronizedList(mutableListOf())
        tokenBucket = TokenBucketInterceptor(periodMs = 100L)
        arrivalInterceptor = Interceptor { chain ->
            arrivedTimes.add(System.currentTimeMillis())
            chain.proceed(chain.request())
        }
        client = OkHttpClient.Builder()
            .addInterceptor(tokenBucket)
            .addInterceptor(arrivalInterceptor)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `single request does not sleep meaningfully`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val request = Request.Builder().url(server.url("/test")).build()

        val start = System.currentTimeMillis()
        val response = client.newCall(request).execute()
        val elapsed = System.currentTimeMillis() - start

        assertEquals(200, response.code)
        assertTrue("single request should take well under 200ms, was ${elapsed}ms", elapsed < 200L)
    }

    @Test
    fun `two consecutive requests arrive spaced at least 80ms apart`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("A"))
        server.enqueue(MockResponse().setResponseCode(200).setBody("B"))

        val exec = Executors.newFixedThreadPool(2)
        try {
            val f1 = exec.submit {
                client.newCall(Request.Builder().url(server.url("/a")).build()).execute().close()
            }
            val f2 = exec.submit {
                client.newCall(Request.Builder().url(server.url("/b")).build()).execute().close()
            }
            f1.get(10, TimeUnit.SECONDS)
            f2.get(10, TimeUnit.SECONDS)
        } finally {
            exec.shutdown()
        }

        assertEquals(2, arrivedTimes.size)
        val spacing = arrivedTimes[1] - arrivedTimes[0]
        assertTrue(
            "second request should arrive at least 80ms after first, spacing was ${spacing}ms",
            spacing >= 80L
        )
    }

    @Test
    fun `five parallel requests take at least 400ms total`() {
        repeat(5) { server.enqueue(MockResponse().setResponseCode(200).setBody("ok")) }
        val exec = Executors.newFixedThreadPool(5)
        try {
            val start = System.currentTimeMillis()
            val futures = (0 until 5).map {
                exec.submit {
                    val resp = client.newCall(
                        Request.Builder().url(server.url("/r$it")).build()
                    ).execute()
                    resp.close()
                }
            }
            futures.forEach { it.get(10, TimeUnit.SECONDS) }
            val elapsed = System.currentTimeMillis() - start

            assertEquals(5, arrivedTimes.size)
            assertTrue("5 throttled requests should take at least 400ms, was ${elapsed}ms", elapsed >= 400L)
        } finally {
            exec.shutdown()
        }
    }

    @Test
    fun `passes 200 response through unchanged`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("payload"))
        val request = Request.Builder().url(server.url("/pass")).build()
        val response = client.newCall(request).execute()

        assertEquals(200, response.code)
        assertEquals("payload", response.body?.string())
        assertEquals(1, server.requestCount)
    }
}
