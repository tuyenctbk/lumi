package com.example.data.api

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

data class DictionaryPhonetic(
    val text: String? = null,
    val audio: String? = null
)

data class DictionaryDefinition(
    val definition: String? = null,
    val example: String? = null
)

data class DictionaryMeaning(
    val partOfSpeech: String? = null,
    val definitions: List<DictionaryDefinition>? = null
)

data class DictionaryEntry(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<DictionaryPhonetic>? = null,
    val meanings: List<DictionaryMeaning>? = null
)

data class TatoebaAudioInfo(
    val url: String? = null
)

data class TatoebaTranslationItem(
    val text: String? = null,
    val lang: String? = null
)

data class TatoebaSentence(
    val text: String? = null,
    val translations: List<List<TatoebaTranslationItem>>? = null,
    val audios: List<List<TatoebaAudioInfo>>? = null
)

data class TatoebaSearchResponse(
    val results: List<TatoebaSentence>? = null
)

interface FreeDictionaryApi {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordInfo(
        @Path("word") word: String
    ): List<DictionaryEntry>
}

interface TatoebaApi {
    @GET("en/api_v0/search")
    suspend fun searchSentences(
        @Query("query") query: String,
        @Query("from") from: String = "eng",
        @Query("to") to: String,
        @Query("limit") limit: Int = 5
    ): TatoebaSearchResponse
}

data class EnrichedWordDetails(
    val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val definition: String,
    val example: String,
    val audioUrl: String?
)

data class SentencePair(
    val sourceText: String,
    val translatedText: String,
    val audioUrl: String?
)

class FreeDictionaryRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val dictionaryApi: FreeDictionaryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FreeDictionaryApi::class.java)
    }

    private val tatoebaApi: TatoebaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://tatoeba.org/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TatoebaApi::class.java)
    }

    private val detailsCache = ConcurrentHashMap<String, EnrichedWordDetails>()
    private val sentenceCache = ConcurrentHashMap<String, List<SentencePair>>()

    fun mapToTatoebaLangCode(code: String): String = when (code.lowercase().trim()) {
        "es" -> "spa"
        "fr" -> "fra"
        "de" -> "deu"
        "it" -> "ita"
        "ja" -> "jpn"
        "ko" -> "kor"
        "zh" -> "cmn"
        "vi" -> "vie"
        "en" -> "eng"
        else -> code.lowercase().trim()
    }

    suspend fun fetchEnrichedWordDetails(word: String): EnrichedWordDetails? = withContext(Dispatchers.IO) {
        val cleanWord = word.lowercase().trim()
        if (cleanWord.isBlank()) return@withContext null
        detailsCache[cleanWord]?.let { return@withContext it }

        try {
            val entries = dictionaryApi.getWordInfo(cleanWord)
            val entry = entries.firstOrNull() ?: return@withContext null

            val phoneticText = entry.phonetic
                ?: entry.phonetics?.firstOrNull { !it.text.isNullOrBlank() }?.text
                ?: ""

            val audioUrl = entry.phonetics?.firstOrNull { !it.audio.isNullOrBlank() }?.audio

            val meaning = entry.meanings?.firstOrNull()
            val partOfSpeech = meaning?.partOfSpeech ?: "noun"
            val defObj = meaning?.definitions?.firstOrNull()
            val definition = defObj?.definition ?: "A friendly vocabulary term."
            val example = defObj?.example ?: "Look at the $cleanWord!"

            val result = EnrichedWordDetails(
                word = entry.word,
                phonetic = phoneticText,
                partOfSpeech = partOfSpeech,
                definition = definition,
                example = example,
                audioUrl = audioUrl
            )
            detailsCache[cleanWord] = result
            result
        } catch (e: Exception) {
            Log.w("FreeDictionaryRepo", "Failed to fetch dictionary info for $word: ${e.message}")
            null
        }
    }

    suspend fun fetchSentencePairs(word: String, targetLangCode: String): List<SentencePair> = withContext(Dispatchers.IO) {
        val cleanWord = word.lowercase().trim()
        if (cleanWord.isBlank()) return@withContext emptyList()
        val tatoebaLang = mapToTatoebaLangCode(targetLangCode)
        val cacheKey = "${cleanWord}_$tatoebaLang"

        sentenceCache[cacheKey]?.let { return@withContext it }

        try {
            val response = tatoebaApi.searchSentences(query = cleanWord, to = tatoebaLang, limit = 5)
            val pairs = mutableListOf<SentencePair>()
            response.results?.forEach { item ->
                val source = item.text ?: return@forEach
                val translatedList = item.translations?.flatten() ?: emptyList()
                val translation = translatedList.firstOrNull {
                    it.lang.equals(tatoebaLang, ignoreCase = true) || it.lang.equals(targetLangCode, ignoreCase = true)
                }?.text ?: translatedList.firstOrNull()?.text ?: ""
                val audioUrl = item.audios?.flatten()?.firstOrNull()?.url
                if (source.isNotBlank() && translation.isNotBlank()) {
                    pairs.add(SentencePair(source, translation, audioUrl))
                }
            }
            if (pairs.isNotEmpty()) {
                sentenceCache[cacheKey] = pairs
            }
            pairs
        } catch (e: Exception) {
            Log.w("FreeDictionaryRepo", "Tatoeba API search error for $word: ${e.message}")
            emptyList()
        }
    }
}
