package com.terrariawiki.features.items.domain

data class SearchResult(
    val title: String,
    val pageId: Int,
    val snippet: String
)
