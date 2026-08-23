package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ==========================================
// Freesound Models
// ==========================================

@JsonClass(generateAdapter = true)
data class FreesoundResponse(
    val count: Int = 0,
    val results: List<FreesoundSoundItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FreesoundSoundItem(
    val id: Long = 0,
    val name: String = "",
    val previews: FreesoundPreviews? = null,
    val duration: Float = 0f,
    val username: String = ""
)

@JsonClass(generateAdapter = true)
data class FreesoundPreviews(
    @Json(name = "preview-hq-mp3") val previewHqMp3: String? = null,
    @Json(name = "preview-lq-mp3") val previewLqMp3: String? = null,
    @Json(name = "preview-hq-ogg") val previewHqOgg: String? = null,
    @Json(name = "preview-lq-ogg") val previewLqOgg: String? = null
)

// ==========================================
// Giphy Models
// ==========================================

@JsonClass(generateAdapter = true)
data class GiphyResponse(
    val data: List<GiphyGifItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GiphyGifItem(
    val id: String = "",
    val title: String = "",
    val images: GiphyImages? = null
)

@JsonClass(generateAdapter = true)
data class GiphyImages(
    @Json(name = "fixed_height") val fixedHeight: GiphyImageData? = null,
    @Json(name = "downsized_medium") val downsizedMedium: GiphyImageData? = null,
    val original: GiphyImageData? = null
)

@JsonClass(generateAdapter = true)
data class GiphyImageData(
    val url: String = "",
    val width: String = "",
    val height: String = ""
)

// ==========================================
// Pixabay Models
// ==========================================

@JsonClass(generateAdapter = true)
data class PixabayResponse(
    val total: Int = 0,
    val totalHits: Int = 0,
    val hits: List<PixabayImageItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PixabayImageItem(
    val id: Long = 0,
    val pageURL: String = "",
    val webformatURL: String = "",
    val largeImageURL: String = "",
    val previewURL: String = "",
    val tags: String = "",
    val user: String = ""
)
