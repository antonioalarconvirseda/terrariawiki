package com.terrariawiki.features.items.data

import com.terrariawiki.features.items.domain.SearchResult

fun SearchHit.toDomain(): SearchResult = SearchResult(
    title = title,
    pageId = pageid,
    snippet = snippet.stripHtml()
)
