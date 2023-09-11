package com.condor.splashbox.data.photo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoSearchResult(
    val total: Int,
    val total_pages: Int,
    val results: List<Photo>
)