package com.terrariawiki.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageUrlTest {

    @Test
    fun `builds direct CDN URL with simple filename`() {
        val url = buildItemImageUrl("Wood.png")
        assertEquals("https://terraria.wiki.gg/images/Wood.png", url)
    }

    @Test
    fun `encodes spaces as %20 not plus`() {
        val url = buildItemImageUrl("Terra Blade.png")
        assertTrue(
            "url should encode spaces as %20 but got: $url",
            url.contains("%20")
        )
        assertTrue(
            "url should NOT contain literal + for spaces but got: $url",
            !url.contains("+")
        )
        assertTrue(
            "url should target the CDN path /images/ but got: $url",
            url.contains("/images/")
        )
        assertTrue(
            "url should NOT use Special:Redirect (rate-limited) but got: $url",
            !url.contains("Special:Redirect")
        )
    }

    @Test
    fun `encodes apostrophes as %27 for items like Abigail`() {
        val url = buildItemImageUrl("Abigail's Flower.png")
        assertTrue(
            "url should encode apostrophe as %27 but got: $url",
            url.contains("%27")
        )
        assertTrue(
            "url should NOT contain literal ' but got: $url",
            !url.contains("'")
        )
    }
}
