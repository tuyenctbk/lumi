package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.model.LearningCategory
import com.example.model.VocabularyItem
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

data class DatamuseWord(
    val word: String,
    val score: Int? = null
)

interface DatamuseApi {
    @GET("words")
    suspend fun getWordsForTopic(
        @Query("ml") topic: String,
        @Query("max") max: Int = 12
    ): List<DatamuseWord>
}

class OpenApiLessonRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val datamuseApi: DatamuseApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.datamuse.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DatamuseApi::class.java)
    }

    // Prebuilt AI dynamic topic packs as fallback and instant offline access
    private val curatedAiPacks = mapOf(
        "dinosaurs" to listOf(
            VocabularyItem("dino_t-rex", "T-Rex", LearningCategory.ANIMALS, "🦖", "tiː rɛks", "Roar!", mapOf("es" to "Tirano-Rex", "fr" to "T-Rex", "de" to "T-Rex", "it" to "T-Rex", "ja" to "ティラノサウルス", "ko" to "티라노사우루스", "zh" to "霸王龙", "en" to "T-Rex", "vi" to "Khủng long bạo chúa"), colorHex = 0xFFE64A19),
            VocabularyItem("dino_triceratops", "Triceratops", LearningCategory.ANIMALS, "🦕", "traɪˈsɛr.ə.tɒps", "Three horns!", mapOf("es" to "Triceratops", "fr" to "Tricératops", "de" to "Triceratops", "it" to "Triceratopo", "ja" to "トリケラトプス", "ko" to "트리케라톱스", "zh" to "三角龙", "en" to "Triceratops", "vi" to "Khủng long ba sừng"), colorHex = 0xFFF57C00),
            VocabularyItem("dino_fossil", "Fossil", LearningCategory.ANIMALS, "🦴", "ˈfɒs.əl", "Ancient bone!", mapOf("es" to "Fósil", "fr" to "Fossile", "de" to "Fossil", "it" to "Fossile", "ja" to "化石 (Kaseki)", "ko" to "화석 (Hwaseok)", "zh" to "化石 (Huàshí)", "en" to "Fossil", "vi" to "Hóa thạch"), colorHex = 0xFF8D6E63),
            VocabularyItem("dino_egg", "Dino Egg", LearningCategory.ANIMALS, "🥚", "ˈdaɪ.nəʊ ɛɡ", "Crack open!", mapOf("es" to "Huevo de dinosaurio", "fr" to "Œuf de dino", "de" to "Dino-Ei", "it" to "Uovo di dino", "ja" to "恐竜のたまご", "ko" to "공룡 알", "zh" to "恐龙蛋", "en" to "Dino Egg", "vi" to "Trứng khủng long"), colorHex = 0xFFFFB74D),
            VocabularyItem("dino_pterodactyl", "Pterodactyl", LearningCategory.ANIMALS, "🦅", "ˌtɛr.əˈdæk.tɪl", "Flying dino!", mapOf("es" to "Pterodáctilo", "fr" to "Ptérodactyle", "de" to "Pterodactylus", "it" to "Pterodattilo", "ja" to "プテラノドン", "ko" to "프테라노돈", "zh" to "翼龙", "en" to "Pterodactyl", "vi" to "Thằn lằn sấm"), colorHex = 0xFF009688),
            VocabularyItem("dino_volcano", "Volcano", LearningCategory.ANIMALS, "🌋", "vɒlˈkeɪ.nəʊ", "Lava boom!", mapOf("es" to "Volcán", "fr" to "Volcan", "de" to "Vulkan", "it" to "Vulcano", "ja" to "かざん (Kazan)", "ko" to "화산 (Hwasan)", "zh" to "火山 (Huǒshān)", "en" to "Volcano", "vi" to "Núi lửa"), colorHex = 0xFFD84315),
            VocabularyItem("dino_footprint", "Footprint", LearningCategory.ANIMALS, "🐾", "ˈfʊt.prɪnt", "Big stomp!", mapOf("es" to "Huella", "fr" to "Empreinte", "de" to "Fußabdruck", "it" to "Impronta", "ja" to "あしあと (Ashiato)", "ko" to "발자국 (Baljaguk)", "zh" to "脚印 (Jiǎoyìn)", "en" to "Footprint", "vi" to "Dấu chân"), colorHex = 0xFF795548),
            VocabularyItem("dino_brachiosaurus", "Brachiosaurus", LearningCategory.ANIMALS, "🦕", "ˌbræk.i.əˈsɔː.rəs", "Long neck!", mapOf("es" to "Braquiosaurio", "fr" to "Brachiosaure", "de" to "Brachiosaurus", "it" to "Brachiosauro", "ja" to "ブラキオサウルス", "ko" to "브ラキオサウルス", "zh" to "腕龙", "en" to "Brachiosaurus", "vi" to "Khủng long cổ dài"), colorHex = 0xFF4CAF50)
        ),
        "ocean" to listOf(
            VocabularyItem("ocean_dolphin", "Dolphin", LearningCategory.ANIMALS, "🐬", "ˈdɒl.fɪn", "Click click splash!", mapOf("es" to "Delfín", "fr" to "Dauphin", "de" to "Delfin", "it" to "Delfino", "ja" to "イルカ (Iruka)", "ko" to "돌고래 (Dolgorae)", "zh" to "海豚 (Hǎitún)", "en" to "Dolphin", "vi" to "Cá heo"), colorHex = 0xFF0288D1),
            VocabularyItem("ocean_whale", "Whale", LearningCategory.ANIMALS, "🐋", "weɪl", "Big blowhole!", mapOf("es" to "Ballena", "fr" to "Baleine", "de" to "Wal", "it" to "Balena", "ja" to "クジラ (Kujira)", "ko" to "고래 (Gorae)", "zh" to "鲸鱼 (Jīngyú)", "en" to "Whale", "vi" to "Cá voi"), colorHex = 0xFF1565C0),
            VocabularyItem("ocean_octopus", "Octopus", LearningCategory.ANIMALS, "🐙", "ˈɒk.tə.pəs", "Eight arms!", mapOf("es" to "Pulpo", "fr" to "Pieuvre", "de" to "Oktopus", "it" to "Polpo", "ja" to "タコ (Tako)", "ko" to "문어 (Muneo)", "zh" to "章鱼 (Zhāngyú)", "en" to "Octopus", "vi" to "Bạch tuộc"), colorHex = 0xFFE91E63),
            VocabularyItem("ocean_shark", "Shark", LearningCategory.ANIMALS, "🦈", "ʃɑːk", "Fin in the water!", mapOf("es" to "Tiburón", "fr" to "Requin", "de" to "Hai", "it" to "Squalo", "ja" to "サメ (Same)", "ko" to "상어 (Sangeo)", "zh" to "鲨鱼 (Shāyú)", "en" to "Shark", "vi" to "Cá mập"), colorHex = 0xFF546E7A),
            VocabularyItem("ocean_crab", "Crab", LearningCategory.ANIMALS, "🦀", "kræb", "Pinch pinch!", mapOf("es" to "Cangrejo", "fr" to "Crabe", "de" to "Krabbe", "it" to "Granchio", "ja" to "カニ (Kani)", "ko" to "게 (Ge)", "zh" to "螃蟹 (Pángxiè)", "en" to "Crab", "vi" to "Con cua"), colorHex = 0xFFFF5722),
            VocabularyItem("ocean_coral", "Coral", LearningCategory.NATURE, "🪸", "ˈkɒr.əl", "Reef home!", mapOf("es" to "Coral", "fr" to "Corail", "de" to "Koralle", "it" to "Corallo", "ja" to "サンゴ (Sango)", "ko" to "산호 (Sanho)", "zh" to "珊瑚 (Shānhú)", "en" to "Coral", "vi" to "San hô"), colorHex = 0xFFF06292),
            VocabularyItem("ocean_turtle", "Sea Turtle", LearningCategory.ANIMALS, "🐢", "siː ˈtɜː.təl", "Swim smooth!", mapOf("es" to "Tortuga marina", "fr" to "Tortue de mer", "de" to "Meeresschildkröte", "it" to "Tartaruga marina", "ja" to "ウミガメ", "ko" to "바다거북", "zh" to "海龟", "en" to "Sea Turtle", "vi" to "Rùa biển"), colorHex = 0xFF43A047),
            VocabularyItem("ocean_starfish", "Starfish", LearningCategory.ANIMALS, "⭐", "ˈstɑː.fɪʃ", "Ocean star!", mapOf("es" to "Estrella de mar", "fr" to "Étoile de mer", "de" to "Seestern", "it" to "Stella marina", "ja" to "ヒトデ (Hitode)", "ko" to "불가사리", "zh" to "海星", "en" to "Starfish", "vi" to "Sao biển"), colorHex = 0xFFFFD54F)
        ),
        "music" to listOf(
            VocabularyItem("music_piano", "Piano", LearningCategory.HOME, "🎹", "piˈæn.əʊ", "Black & white keys!", mapOf("es" to "Piano", "fr" to "Piano", "de" to "Klavier", "it" to "Pianoforte", "ja" to "ピアノ (Piano)", "ko" to "피아노 (Piano)", "zh" to "钢琴 (Gāngqín)", "en" to "Piano", "vi" to "Đàn piano"), colorHex = 0xFF37474F),
            VocabularyItem("music_guitar", "Guitar", LearningCategory.HOME, "🎸", "ɡɪˈtɑːr", "Strum the strings!", mapOf("es" to "Guitarra", "fr" to "Guitare", "de" to "Gitarre", "it" to "Chitarra", "ja" to "ギター (Gitā)", "ko" to "기타 (Gita)", "zh" to "吉他 (Jítā)", "en" to "Guitar", "vi" to "Đàn ghi-ta"), colorHex = 0xFFFF8F00),
            VocabularyItem("music_drums", "Drums", LearningCategory.HOME, "🥁", "drʌmz", "Boom-tap-tap!", mapOf("es" to "Tambores", "fr" to "Batterie", "de" to "Trommeln", "it" to "Batteria", "ja" to "ドラム (Doramu)", "ko" to "드럼 (Deureom)", "zh" to "架子鼓 (Jiàzigǔ)", "en" to "Drums", "vi" to "Bộ trống"), colorHex = 0xFFC62828),
            VocabularyItem("music_trumpet", "Trumpet", LearningCategory.HOME, "🎺", "ˈtrʌm.pɪt", "Toot toot!", mapOf("es" to "Trompeta", "fr" to "Trompette", "de" to "Trompete", "it" to "Tromba", "ja" to "トランペット", "ko" to "트럼펫", "zh" to "小号 (Xiǎohào)", "en" to "Trumpet", "vi" to "Kèn trumpet"), colorHex = 0xFFFFD600),
            VocabularyItem("music_violin", "Violin", LearningCategory.HOME, "🎻", "ˌvaɪəˈlɪn", "Sweet melodies!", mapOf("es" to "Violín", "fr" to "Violon", "de" to "Geige", "it" to "Violino", "ja" to "バイオリン", "ko" to "바이올린", "zh" to "小提琴 (Xiǎotíqín)", "en" to "Violin", "vi" to "Đàn vĩ cầm"), colorHex = 0xFF8D6E63)
        )
    )

    suspend fun generateCustomAiLesson(topic: String): List<VocabularyItem> = withContext(Dispatchers.IO) {
        val cleanTopic = topic.lowercase().trim()

        // 1. Check curated AI packs
        for ((key, items) in curatedAiPacks) {
            if (cleanTopic.contains(key) || key.contains(cleanTopic)) {
                return@withContext items
            }
        }

        // 2. Query Datamuse Open API for related words
        try {
            val apiWords = datamuseApi.getWordsForTopic(cleanTopic, 10)
            if (apiWords.isNotEmpty()) {
                val generatedItems = apiWords.take(8).mapIndexed { index, item ->
                    val capitalWord = item.word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    VocabularyItem(
                        id = "ai_${cleanTopic}_$index",
                        englishWord = capitalWord,
                        category = LearningCategory.ACTIONS,
                        emoji = getEmojiForWord(item.word),
                        phonetic = "/${item.word}/",
                        soundPrompt = "Discover $capitalWord!",
                        translations = mapOf(
                            "es" to capitalWord, "fr" to capitalWord, "de" to capitalWord,
                            "it" to capitalWord, "ja" to capitalWord, "ko" to capitalWord,
                            "zh" to capitalWord, "en" to capitalWord, "vi" to capitalWord
                        ),
                        colorHex = 0xFF5C6BC0
                    )
                }
                if (generatedItems.isNotEmpty()) {
                    return@withContext generatedItems
                }
            }
        } catch (e: Exception) {
            Log.e("OpenApiLessonRepository", "Datamuse API error: ${e.message}")
        }

        // Fallback: Return Dinosaur pack
        return@withContext curatedAiPacks["dinosaurs"] ?: emptyList()
    }

    private fun getEmojiForWord(word: String): String {
        return when {
            word.contains("dino") || word.contains("rex") -> "🦖"
            word.contains("star") || word.contains("sun") -> "⭐"
            word.contains("fish") || word.contains("water") -> "🐟"
            word.contains("car") || word.contains("drive") -> "🚗"
            word.contains("tree") || word.contains("plant") -> "🌱"
            word.contains("fly") || word.contains("bird") -> "🕊️"
            else -> "✨"
        }
    }
}
