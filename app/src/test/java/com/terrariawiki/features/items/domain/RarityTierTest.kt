package com.terrariawiki.features.items.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class RarityTierTest {

    @Test
    fun `fromLevel maps master tier`() {
        assertEquals(RarityTier.MASTER, RarityTier.fromLevel(-1))
    }

    @Test
    fun `fromLevel maps common tier`() {
        assertEquals(RarityTier.COMMON, RarityTier.fromLevel(0))
    }

    @Test
    fun `fromLevel maps expert tier`() {
        assertEquals(RarityTier.EXPERT, RarityTier.fromLevel(11))
    }

    @Test
    fun `fromLevel falls back to common for unmapped level`() {
        assertEquals(RarityTier.COMMON, RarityTier.fromLevel(99))
    }
}
