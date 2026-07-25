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
    fun `replaces spaces with underscores (not %20) for CDN path`() {
        val url = buildItemImageUrl("Terra Blade.png")
        assertTrue(
            "url should use underscores for spaces but got: $url",
            url.contains("Terra_Blade.png")
        )
        assertTrue(
            "url should NOT contain literal %20 but got: $url",
            !url.contains("%20")
        )
        assertTrue(
            "url should NOT contain + for spaces but got: $url",
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
    fun `encodes apostrophes as percent 27 with underscores`() {
        val url = buildItemImageUrl("Abigail's Flower.png")
        assertTrue(
            "url should encode apostrophe as %27 but got: $url",
            url.contains("Abigail%27s_Flower.png")
        )
        assertTrue(
            "url should NOT contain literal ' but got: $url",
            !url.contains("'")
        )
    }

    @Test
    fun `keeps parentheses literal (not percent 28 or 29)`() {
        val url = buildItemImageUrl("Music Box (Title).png")
        assertTrue(
            "url should keep parentheses literal but got: $url",
            url.contains("Music_Box_(Title).png")
        )
        assertTrue(
            "url should NOT contain %28 but got: $url",
            !url.contains("%28")
        )
        assertTrue(
            "url should NOT contain %29 but got: $url",
            !url.contains("%29")
        )
    }
}
