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

interface PixabayApi {
    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("image_type") imageType: String = "photo",
        @Query("safesearch") safeSearch: Boolean = true,
        @Query("per_page") perPage: Int = 10,
        @Query("orientation") orientation: String = "horizontal"
    ): PixabayResponse
}

class PixabayRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val api: PixabayApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://pixabay.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PixabayApi::class.java)
    }

    // High quality safe photo assets for vocabulary words
    private val curatedPhotos = mapOf(
        "cat" to "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&auto=format&fit=crop&q=80",
        "dog" to "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&auto=format&fit=crop&q=80",
        "lion" to "https://images.unsplash.com/photo-1546182990-dffeafbe841d?w=600&auto=format&fit=crop&q=80",
        "elephant" to "https://images.unsplash.com/photo-1557050543-4d5f4e07ef46?w=600&auto=format&fit=crop&q=80",
        "bird" to "https://images.unsplash.com/photo-1444464666168-49d633b86797?w=600&auto=format&fit=crop&q=80",
        "frog" to "https://images.unsplash.com/photo-1558857563-b37cf53f1917?w=600&auto=format&fit=crop&q=80",
        "rabbit" to "https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=600&auto=format&fit=crop&q=80",
        "fish" to "https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=600&auto=format&fit=crop&q=80",
        "apple" to "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=600&auto=format&fit=crop&q=80",
        "banana" to "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=600&auto=format&fit=crop&q=80",
        "milk" to "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=600&auto=format&fit=crop&q=80",
        "bread" to "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600&auto=format&fit=crop&q=80",
        "ice_cream" to "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=600&auto=format&fit=crop&q=80",
        "strawberry" to "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=600&auto=format&fit=crop&q=80",
        "sun" to "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
        "moon" to "https://images.unsplash.com/photo-1532693322450-2cb5c511067d?w=600&auto=format&fit=crop&q=80",
        "star" to "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=600&auto=format&fit=crop&q=80",
        "rocket" to "https://images.unsplash.com/photo-1517976487502-5c8e76c117d9?w=600&auto=format&fit=crop&q=80",
        "house" to "https://images.unsplash.com/photo-1518780664697-55e3ad937233?w=600&auto=format&fit=crop&q=80",
        "car" to "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600&auto=format&fit=crop&q=80",
        "chair" to "https://images.unsplash.com/photo-1503602642458-232111445657?w=600&auto=format&fit=crop&q=80",
        "book" to "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop&q=80"
    )

    suspend fun getImageForWord(wordKey: String): String? = withContext(Dispatchers.IO) {
        val apiKey = try {
            val keyField = BuildConfig::class.java.getField("PIXABAY_API_KEY")
            keyField.get(null) as? String
        } catch (_: Exception) {
            null
        }

        val cleanKey = wordKey.lowercase().trim()

        if (!apiKey.isNullOrBlank() && apiKey != "your_pixabay_api_key") {
            try {
                val response = api.searchImages(
                    apiKey = apiKey,
                    query = cleanKey,
                    safeSearch = true,
                    perPage = 6
                )
                val photoUrl = response.hits.firstOrNull()?.webformatURL
                    ?: response.hits.firstOrNull()?.largeImageURL
                if (!photoUrl.isNullOrBlank()) {
                    return@withContext photoUrl
                }
            } catch (e: Exception) {
                Log.w("PixabayRepository", "Pixabay search error for $wordKey, using fallback", e)
            }
        }

        curatedPhotos[cleanKey] ?: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&auto=format&fit=crop&q=80"
    }
}
