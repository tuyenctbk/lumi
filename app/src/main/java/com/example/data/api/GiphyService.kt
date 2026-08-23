package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GiphyApi {
    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 10,
        @Query("rating") rating: String = "g",
        @Query("lang") lang: String = "en"
    ): GiphyResponse
}

class GiphyRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val api: GiphyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.giphy.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GiphyApi::class.java)
    }

    // Curated high quality kid-friendly animated action GIFs for verbs
    private val curatedVerbGifs = mapOf(
        "run" to "https://media.giphy.com/media/l0MYEqEzwMWFCg8rm/giphy.gif",
        "jump" to "https://media.giphy.com/media/l41lI4bYmcsPJX9Go/giphy.gif",
        "dance" to "https://media.giphy.com/media/blSTtZehjAZ8I/giphy.gif",
        "swim" to "https://media.giphy.com/media/3o7TKSjRrfIPjeiVyM/giphy.gif",
        "eat" to "https://media.giphy.com/media/12uXi1GXBibALC/giphy.gif",
        "sleep" to "https://media.giphy.com/media/3o6Zt6KHxJTbXCnSvu/giphy.gif",
        "read" to "https://media.giphy.com/media/3o7qDEq2bMbcbPRQ2c/giphy.gif",
        "wave" to "https://media.giphy.com/media/3o7TKtnuHOHHUjR38Y/giphy.gif",
        "clap" to "https://media.giphy.com/media/artj92V8o75VPL7AeQ/giphy.gif",
        "laugh" to "https://media.giphy.com/media/3o7btUg31R0A29MNa0/giphy.gif"
    )

    suspend fun getGifForAction(actionWord: String): String? = withContext(Dispatchers.IO) {
        val apiKey = try {
            val keyField = BuildConfig::class.java.getField("GIPHY_API_KEY")
            keyField.get(null) as? String
        } catch (_: Exception) {
            null
        }

        val cleanKey = actionWord.lowercase().trim()

        if (!apiKey.isNullOrBlank() && apiKey != "your_giphy_api_key") {
            try {
                val response = api.searchGifs(
                    apiKey = apiKey,
                    query = "$cleanKey cartoon kid",
                    rating = "g",
                    limit = 5
                )
                val gifUrl = response.data.firstOrNull()?.images?.fixedHeight?.url
                    ?: response.data.firstOrNull()?.images?.original?.url
                if (!gifUrl.isNullOrBlank()) {
                    return@withContext gifUrl
                }
            } catch (e: Exception) {
                Log.w("GiphyRepository", "Giphy search error for $actionWord, using fallback", e)
            }
        }

        curatedVerbGifs[cleanKey] ?: "https://media.giphy.com/media/l41lI4bYmcsPJX9Go/giphy.gif"
    }
}
