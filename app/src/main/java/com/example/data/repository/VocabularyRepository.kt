package com.example.data.repository

import com.example.data.db.BadgeEntity
import com.example.data.db.DailyLearningStatsEntity
import com.example.data.db.LearningSessionEntity
import com.example.data.db.LumiDao
import com.example.data.db.VocabularyDao
import com.example.data.db.VocabularyItemEntity
import com.example.data.db.WordProgressEntity
import com.example.data.db.UserPreferencesEntity
import com.example.model.LearningCategory
import com.example.model.TargetLanguage
import com.example.model.VocabularyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VocabularyRepository(
    private val dao: LumiDao,
    private val vocabularyDao: VocabularyDao? = null
) {

    val allVocabulary: List<VocabularyItem> = listOf(
        // ANIMALS
        VocabularyItem(
            id = "cat",
            englishWord = "Cat",
            category = LearningCategory.ANIMALS,
            emoji = "🐱",
            phonetic = "kæt",
            soundPrompt = "Meow!",
            translations = mapOf(
                "es" to "Gato", "fr" to "Chat", "de" to "Katze",
                "it" to "Gatto", "ja" to "ねこ (Neko)", "ko" to "고양이 (Goyangi)",
                "zh" to "猫 (Māo)", "en" to "Cat"
            ),
            colorHex = 0xFFFFAB91
        ),
        VocabularyItem(
            id = "dog",
            englishWord = "Dog",
            category = LearningCategory.ANIMALS,
            emoji = "🐶",
            phonetic = "dɔːɡ",
            soundPrompt = "Woof woof!",
            translations = mapOf(
                "es" to "Perro", "fr" to "Chien", "de" to "Hund",
                "it" to "Cane", "ja" to "いぬ (Inu)", "ko" to "개 (Gae)",
                "zh" to "狗 (Gǒu)", "en" to "Dog"
            ),
            colorHex = 0xFFFFCC80
        ),
        VocabularyItem(
            id = "lion",
            englishWord = "Lion",
            category = LearningCategory.ANIMALS,
            emoji = "🦁",
            phonetic = "ˈlaɪ.ən",
            soundPrompt = "Roaaar!",
            translations = mapOf(
                "es" to "León", "fr" to "Lion", "de" to "Löwe",
                "it" to "Leone", "ja" to "ライオン (Raion)", "ko" to "사자 (Saja)",
                "zh" to "狮子 (Shīzi)", "en" to "Lion"
            ),
            colorHex = 0xFFFFE082
        ),
        VocabularyItem(
            id = "elephant",
            englishWord = "Elephant",
            category = LearningCategory.ANIMALS,
            emoji = "🐘",
            phonetic = "ˈel.ɪ.fənt",
            soundPrompt = "Pawoo!",
            translations = mapOf(
                "es" to "Elefante", "fr" to "Éléphant", "de" to "Elefant",
                "it" to "Elefante", "ja" to "ぞう (Zou)", "ko" to "코끼리 (Kokkiri)",
                "zh" to "大象 (Dàxiàng)", "en" to "Elephant"
            ),
            colorHex = 0xFFB0BEC5
        ),
        VocabularyItem(
            id = "bird",
            englishWord = "Bird",
            category = LearningCategory.ANIMALS,
            emoji = "🐦",
            phonetic = "bɜːd",
            soundPrompt = "Chirp chirp!",
            translations = mapOf(
                "es" to "Pájaro", "fr" to "Oiseau", "de" to "Vogel",
                "it" to "Uccello", "ja" to "とり (Tori)", "ko" to "새 (Sae)",
                "zh" to "鸟 (Niǎo)", "en" to "Bird"
            ),
            colorHex = 0xFF81D4FA
        ),
        VocabularyItem(
            id = "frog",
            englishWord = "Frog",
            category = LearningCategory.ANIMALS,
            emoji = "🐸",
            phonetic = "frɒɡ",
            soundPrompt = "Ribbit ribbit!",
            translations = mapOf(
                "es" to "Rana", "fr" to "Grenouille", "de" to "Frosch",
                "it" to "Rana", "ja" to "かえる (Kaeru)", "ko" to "개구리 (Gaeguri)",
                "zh" to "青蛙 (Qīngwā)", "en" to "Frog"
            ),
            colorHex = 0xFFA5D6A7
        ),
        VocabularyItem(
            id = "rabbit",
            englishWord = "Rabbit",
            category = LearningCategory.ANIMALS,
            emoji = "🐰",
            phonetic = "ˈræb.ɪt",
            soundPrompt = "Hop hop!",
            translations = mapOf(
                "es" to "Conejo", "fr" to "Lapin", "de" to "Hase",
                "it" to "Coniglio", "ja" to "うさぎ (Usagi)", "ko" to "토끼 (Tokki)",
                "zh" to "兔子 (Tùzǐ)", "en" to "Rabbit"
            ),
            colorHex = 0xFFF48FB1
        ),
        VocabularyItem(
            id = "fish",
            englishWord = "Fish",
            category = LearningCategory.ANIMALS,
            emoji = "🐠",
            phonetic = "fɪʃ",
            soundPrompt = "Glub glub!",
            translations = mapOf(
                "es" to "Pez", "fr" to "Poisson", "de" to "Fisch",
                "it" to "Pesce", "ja" to "さかな (Sakana)", "ko" to "물고기 (Mulgogi)",
                "zh" to "鱼 (Yú)", "en" to "Fish"
            ),
            colorHex = 0xFF80DEEA
        ),

        // FOOD
        VocabularyItem(
            id = "apple",
            englishWord = "Apple",
            category = LearningCategory.FOOD,
            emoji = "🍎",
            phonetic = "ˈæp.əl",
            soundPrompt = "Crunch!",
            translations = mapOf(
                "es" to "Manzana", "fr" to "Pomme", "de" to "Apfel",
                "it" to "Mela", "ja" to "りんご (Ringo)", "ko" to "사과 (Sagwa)",
                "zh" to "苹果 (Píngguǒ)", "en" to "Apple"
            ),
            colorHex = 0xFFEF9A9A
        ),
        VocabularyItem(
            id = "banana",
            englishWord = "Banana",
            category = LearningCategory.FOOD,
            emoji = "🍌",
            phonetic = "bəˈnɑː.nə",
            soundPrompt = "Yum yum!",
            translations = mapOf(
                "es" to "Plátano", "fr" to "Banane", "de" to "Banane",
                "it" to "Banana", "ja" to "バナナ (Banana)", "ko" to "바나나 (Banana)",
                "zh" to "香蕉 (Xiāngjiāo)", "en" to "Banana"
            ),
            colorHex = 0xFFFFF59D
        ),
        VocabularyItem(
            id = "milk",
            englishWord = "Milk",
            category = LearningCategory.FOOD,
            emoji = "🥛",
            phonetic = "mɪlk",
            soundPrompt = "Sip sip!",
            translations = mapOf(
                "es" to "Leche", "fr" to "Lait", "de" to "Milch",
                "it" to "Latte", "ja" to "ぎゅうにゅう (Gyuunyuu)", "ko" to "우유 (Uyu)",
                "zh" to "牛奶 (Niúnǎi)", "en" to "Milk"
            ),
            colorHex = 0xFFE0E0E0
        ),
        VocabularyItem(
            id = "bread",
            englishWord = "Bread",
            category = LearningCategory.FOOD,
            emoji = "🍞",
            phonetic = "bred",
            soundPrompt = "Toasty!",
            translations = mapOf(
                "es" to "Pan", "fr" to "Pain", "de" to "Brot",
                "it" to "Pane", "ja" to "パン (Pan)", "ko" to "빵 (Ppang)",
                "zh" to "面包 (Miànbāo)", "en" to "Bread"
            ),
            colorHex = 0xFFFFCC80
        ),
        VocabularyItem(
            id = "ice_cream",
            englishWord = "Ice Cream",
            category = LearningCategory.FOOD,
            emoji = "🍦",
            phonetic = "ˈaɪs ˌkriːm",
            soundPrompt = "Sweet & cold!",
            translations = mapOf(
                "es" to "Helado", "fr" to "Glace", "de" to "Eis",
                "it" to "Gelato", "ja" to "アイス (Aisu)", "ko" to "아이스크림 (Aiseukeurim)",
                "zh" to "冰淇淋 (Bīngqílín)", "en" to "Ice Cream"
            ),
            colorHex = 0xFFF8BBD0
        ),
        VocabularyItem(
            id = "strawberry",
            englishWord = "Strawberry",
            category = LearningCategory.FOOD,
            emoji = "🍓",
            phonetic = "ˈstrɔː.bər.i",
            soundPrompt = "Juicy berry!",
            translations = mapOf(
                "es" to "Fresa", "fr" to "Fraise", "de" to "Erdbeere",
                "it" to "Fragola", "ja" to "いちご (Ichigo)", "ko" to "딸기 (Ttalgi)",
                "zh" to "草莓 (Cǎoméi)", "en" to "Strawberry"
            ),
            colorHex = 0xFFFF8A80
        ),

        // ACTIONS / VERBS
        VocabularyItem(
            id = "run",
            englishWord = "Run",
            category = LearningCategory.ACTIONS,
            emoji = "🏃",
            phonetic = "rʌn",
            soundPrompt = "Zoom zoom!",
            translations = mapOf(
                "es" to "Correr", "fr" to "Courir", "de" to "Rennen",
                "it" to "Correre", "ja" to "はしる (Hashiru)", "ko" to "달리다 (Dallida)",
                "zh" to "跑 (Pǎo)", "en" to "Run"
            ),
            colorHex = 0xFFFF8A80
        ),
        VocabularyItem(
            id = "jump",
            englishWord = "Jump",
            category = LearningCategory.ACTIONS,
            emoji = "🦘",
            phonetic = "dʒʌmp",
            soundPrompt = "Boing boing!",
            translations = mapOf(
                "es" to "Saltar", "fr" to "Sauter", "de" to "Springen",
                "it" to "Saltare", "ja" to "とぶ (Tobu)", "ko" to "뛰다 (Ttwida)",
                "zh" to "跳 (Tiào)", "en" to "Jump"
            ),
            colorHex = 0xFFFFD54F
        ),
        VocabularyItem(
            id = "sleep",
            englishWord = "Sleep",
            category = LearningCategory.ACTIONS,
            emoji = "😴",
            phonetic = "sliːp",
            soundPrompt = "Zzz...",
            translations = mapOf(
                "es" to "Dormir", "fr" to "Dormir", "de" to "Schlafen",
                "it" to "Dormire", "ja" to "ねる (Neru)", "ko" to "자다 (Jada)",
                "zh" to "睡觉 (Shuìjiào)", "en" to "Sleep"
            ),
            colorHex = 0xFFB39DDB
        ),
        VocabularyItem(
            id = "eat",
            englishWord = "Eat",
            category = LearningCategory.ACTIONS,
            emoji = "😋",
            phonetic = "iːt",
            soundPrompt = "Nom nom nom!",
            translations = mapOf(
                "es" to "Comer", "fr" to "Manger", "de" to "Essen",
                "it" to "Mangiare", "ja" to "たべる (Taberu)", "ko" to "먹다 (Meokda)",
                "zh" to "吃 (Chī)", "en" to "Eat"
            ),
            colorHex = 0xFFFFAB91
        ),
        VocabularyItem(
            id = "dance",
            englishWord = "Dance",
            category = LearningCategory.ACTIONS,
            emoji = "💃",
            phonetic = "dɑːns",
            soundPrompt = "Cha-cha-cha!",
            translations = mapOf(
                "es" to "Bailar", "fr" to "Danser", "de" to "Tanzen",
                "it" to "Ballare", "ja" to "おどる (Odoru)", "ko" to "춤추다 (Chumchuda)",
                "zh" to "跳舞 (Tiàowǔ)", "en" to "Dance"
            ),
            colorHex = 0xFFCE93D8
        ),

        // COLORS & SHAPES
        VocabularyItem(
            id = "red",
            englishWord = "Red",
            category = LearningCategory.COLORS,
            emoji = "🔴",
            phonetic = "red",
            soundPrompt = "Fiery red!",
            translations = mapOf(
                "es" to "Rojo", "fr" to "Rouge", "de" to "Rot",
                "it" to "Rosso", "ja" to "あか (Aka)", "ko" to "빨간색 (Ppalgansaek)",
                "zh" to "红色 (Hóngsè)", "en" to "Red"
            ),
            colorHex = 0xFFFF5252
        ),
        VocabularyItem(
            id = "blue",
            englishWord = "Blue",
            category = LearningCategory.COLORS,
            emoji = "🔵",
            phonetic = "bluː",
            soundPrompt = "Ocean blue!",
            translations = mapOf(
                "es" to "Azul", "fr" to "Bleu", "de" to "Blau",
                "it" to "Blu", "ja" to "あお (Ao)", "ko" to "파란색 (Paransaek)",
                "zh" to "蓝色 (Lánsè)", "en" to "Blue"
            ),
            colorHex = 0xFF448AFF
        ),
        VocabularyItem(
            id = "yellow",
            englishWord = "Yellow",
            category = LearningCategory.COLORS,
            emoji = "🟡",
            phonetic = "ˈjel.əʊ",
            soundPrompt = "Sunny yellow!",
            translations = mapOf(
                "es" to "Amarillo", "fr" to "Jaune", "de" to "Gelb",
                "it" to "Giallo", "ja" to "きいろ (Kiiro)", "ko" to "노란색 (Noransaek)",
                "zh" to "黄色 (Huángsè)", "en" to "Yellow"
            ),
            colorHex = 0xFFFFD700
        ),
        VocabularyItem(
            id = "green",
            englishWord = "Green",
            category = LearningCategory.COLORS,
            emoji = "🟢",
            phonetic = "ɡriːn",
            soundPrompt = "Forest green!",
            translations = mapOf(
                "es" to "Verde", "fr" to "Vert", "de" to "Grün",
                "it" to "Verde", "ja" to "みどり (Midori)", "ko" to "초록색 (Choroksaek)",
                "zh" to "绿色 (Lǜsè)", "en" to "Green"
            ),
            colorHex = 0xFF69F0AE
        ),
        VocabularyItem(
            id = "star_shape",
            englishWord = "Star",
            category = LearningCategory.COLORS,
            emoji = "⭐",
            phonetic = "stɑːr",
            soundPrompt = "Twinkle twinkle!",
            translations = mapOf(
                "es" to "Estrella", "fr" to "Étoile", "de" to "Stern",
                "it" to "Stella", "ja" to "ほし (Hoshi)", "ko" to "별 (Byeol)",
                "zh" to "星星 (Xīngxing)", "en" to "Star"
            ),
            colorHex = 0xFFFFE57F
        ),
        VocabularyItem(
            id = "heart_shape",
            englishWord = "Heart",
            category = LearningCategory.COLORS,
            emoji = "💖",
            phonetic = "hɑːt",
            soundPrompt = "Love and heart!",
            translations = mapOf(
                "es" to "Corazón", "fr" to "Cœur", "de" to "Herz",
                "it" to "Cuore", "ja" to "ハート (Hāto)", "ko" to "하트 (Hateu)",
                "zh" to "爱心 (Àixīn)", "en" to "Heart"
            ),
            colorHex = 0xFFFF4081
        ),

        // SPACE & WONDERS
        VocabularyItem(
            id = "sun",
            englishWord = "Sun",
            category = LearningCategory.SPACE,
            emoji = "☀️",
            phonetic = "sʌn",
            soundPrompt = "Warm and bright!",
            translations = mapOf(
                "es" to "Sol", "fr" to "Soleil", "de" to "Sonne",
                "it" to "Sole", "ja" to "たいよう (Taiyou)", "ko" to "태양 (Taeyang)",
                "zh" to "太阳 (Tàiyáng)", "en" to "Sun"
            ),
            colorHex = 0xFFFFCA28
        ),
        VocabularyItem(
            id = "moon",
            englishWord = "Moon",
            category = LearningCategory.SPACE,
            emoji = "🌙",
            phonetic = "muːn",
            soundPrompt = "Night moon!",
            translations = mapOf(
                "es" to "Luna", "fr" to "Lune", "de" to "Mond",
                "it" to "Luna", "ja" to "つき (Tsuki)", "ko" to "달 (Dal)",
                "zh" to "月亮 (Yuèliang)", "en" to "Moon"
            ),
            colorHex = 0xFFFFF9C4
        ),
        VocabularyItem(
            id = "rocket",
            englishWord = "Rocket",
            category = LearningCategory.SPACE,
            emoji = "🚀",
            phonetic = "ˈrɒk.ɪt",
            soundPrompt = "Blast off in 3, 2, 1!",
            translations = mapOf(
                "es" to "Cohete", "fr" to "Fusée", "de" to "Rakete",
                "it" to "Razzo", "ja" to "ロケット (Roketto)", "ko" to "로켓 (Roket)",
                "zh" to "火箭 (Huǒjiàn)", "en" to "Rocket"
            ),
            colorHex = 0xFFFF5252
        ),
        VocabularyItem(
            id = "planet",
            englishWord = "Planet",
            category = LearningCategory.SPACE,
            emoji = "🪐",
            phonetic = "ˈplæn.ɪt",
            soundPrompt = "Cosmic wonder!",
            translations = mapOf(
                "es" to "Planeta", "fr" to "Planète", "de" to "Planet",
                "it" to "Pianeta", "ja" to "わくせい (Wakusei)", "ko" to "행성 (Haengseong)",
                "zh" to "行星 (Xíngxīng)", "en" to "Planet"
            ),
            colorHex = 0xFF90CAF9
        ),

        // HOME & EVERYDAY OBJECTS
        VocabularyItem(
            id = "house",
            englishWord = "House",
            category = LearningCategory.HOME,
            emoji = "🏠",
            phonetic = "haʊs",
            soundPrompt = "Cozy home!",
            translations = mapOf(
                "es" to "Casa", "fr" to "Maison", "de" to "Haus",
                "it" to "Casa", "ja" to "いえ (Ie)", "ko" to "집 (Jip)",
                "zh" to "房子 (Fángzi)", "en" to "House"
            ),
            colorHex = 0xFF80CBC4
        ),
        VocabularyItem(
            id = "car",
            englishWord = "Car",
            category = LearningCategory.HOME,
            emoji = "🚗",
            phonetic = "kɑːr",
            soundPrompt = "Beep beep!",
            translations = mapOf(
                "es" to "Coche", "fr" to "Voiture", "de" to "Auto",
                "it" to "Macchina", "ja" to "くるま (Kuruma)", "ko" to "자동차 (Jadongcha)",
                "zh" to "汽车 (Qìchē)", "en" to "Car"
            ),
            colorHex = 0xFFEF5350
        ),
        VocabularyItem(
            id = "book",
            englishWord = "Book",
            category = LearningCategory.HOME,
            emoji = "📖",
            phonetic = "bʊk",
            soundPrompt = "Story time!",
            translations = mapOf(
                "es" to "Libro", "fr" to "Livre", "de" to "Buch",
                "it" to "Libro", "ja" to "ほん (Hon)", "ko" to "책 (Chaek)",
                "zh" to "书 (Shū)", "en" to "Book"
            ),
            colorHex = 0xFFFFB74D
        ),
        VocabularyItem(
            id = "ball",
            englishWord = "Ball",
            category = LearningCategory.HOME,
            emoji = "⚽",
            phonetic = "bɔːl",
            soundPrompt = "Bounce and kick!",
            translations = mapOf(
                "es" to "Pelota", "fr" to "Ballon", "de" to "Ball",
                "it" to "Palla", "ja" to "ボール (Bōru)", "ko" to "공 (Gong)",
                "zh" to "球 (Qiú)", "en" to "Ball"
            ),
            colorHex = 0xFF81C784
        )
    )

    fun getWordsByCategory(category: LearningCategory): List<VocabularyItem> {
        return allVocabulary.filter { it.category == category }
    }

    fun getWordById(id: String): VocabularyItem? {
        return allVocabulary.find { it.id == id }
    }

    fun getWordProgressStream(langCode: String): Flow<List<WordProgressEntity>> {
        return dao.getAllProgress(langCode)
    }

    suspend fun recordAnswer(wordId: String, langCode: String, isCorrect: Boolean): List<BadgeEntity> {
        val existing = dao.getProgressForWord(wordId) ?: WordProgressEntity(
            wordId = wordId,
            languageCode = langCode
        )

        val newCorrect = if (isCorrect) existing.correctCount + 1 else existing.correctCount
        val newError = if (!isCorrect) existing.errorCount + 1 else existing.errorCount
        val isMastered = newCorrect >= 3 && (newCorrect.toFloat() / (newCorrect + newError)) >= 0.75f

        // SRS SuperMemo interval update
        val newInterval = if (isCorrect) {
            when (existing.intervalDays) {
                1 -> 3
                3 -> 7
                else -> (existing.intervalDays * 2.2).toInt()
            }
        } else {
            1
        }

        val updated = existing.copy(
            correctCount = newCorrect,
            errorCount = newError,
            lastReviewedAt = System.currentTimeMillis(),
            nextReviewAt = System.currentTimeMillis() + (newInterval * 24L * 3600L * 1000L),
            isMastered = isMastered,
            intervalDays = newInterval
        )
        dao.saveProgress(updated)

        val newlyUnlockedBadges = mutableListOf<BadgeEntity>()

        // Check for badge unlocks
        if (isMastered) {
            val badge = BadgeEntity(
                id = "master_${wordId}",
                title = "Word Star!",
                description = "Mastered word #${wordId}",
                iconEmoji = "⭐"
            )
            if (dao.getBadgeById(badge.id) == null) {
                dao.unlockBadge(badge)
                newlyUnlockedBadges.add(badge)
            }
        }
        if (newCorrect == 1) {
            val badge = BadgeEntity(
                id = "first_step",
                title = "First Words Explorer",
                description = "Answered your first question correctly!",
                iconEmoji = "🌟"
            )
            if (dao.getBadgeById(badge.id) == null) {
                dao.unlockBadge(badge)
                newlyUnlockedBadges.add(badge)
            }
        }
        return newlyUnlockedBadges
    }

    suspend fun evaluateAndUnlockAchievements(
        masteredCount: Int,
        streakDays: Int,
        physicalBreaks: Int,
        totalSessions: Int
    ): List<BadgeEntity> {
        val newlyUnlocked = mutableListOf<BadgeEntity>()
        val catalog = com.example.model.AchievementCatalog.ALL_ACHIEVEMENTS
        for (achievement in catalog) {
            val progress = achievement.progressExtractor(masteredCount, streakDays, physicalBreaks, totalSessions)
            if (progress >= achievement.targetGoal) {
                val existing = dao.getBadgeById(achievement.id)
                if (existing == null) {
                    val badge = BadgeEntity(
                        id = achievement.id,
                        title = achievement.title,
                        description = achievement.description,
                        iconEmoji = achievement.iconEmoji
                    )
                    dao.unlockBadge(badge)
                    newlyUnlocked.add(badge)
                }
            }
        }
        return newlyUnlocked
    }

    suspend fun logSession(gameType: String, wordsPracticed: Int, accuracy: Float, durationSeconds: Int): DailyLearningStatsEntity {
        dao.logSession(
            LearningSessionEntity(
                gameType = gameType,
                wordsPracticed = wordsPracticed,
                accuracy = accuracy,
                durationSeconds = durationSeconds
            )
        )
        return recordDailyActivity(wordsPracticed, accuracy, durationSeconds)
    }

    suspend fun recordDailyActivity(wordsPracticed: Int, accuracy: Float, durationSeconds: Int): DailyLearningStatsEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayLabelFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val now = Date()
        val dateString = dateFormat.format(now)
        val dayLabel = dayLabelFormat.format(now)

        val existing = dao.getDailyStatForDate(dateString)
        val newWords = (existing?.wordsPracticed ?: 0) + wordsPracticed
        val newMinutes = (existing?.minutesPracticed ?: 0) + (durationSeconds / 60).coerceAtLeast(1)
        val newSessions = (existing?.sessionsCompleted ?: 0) + 1
        val isGoalMet = newWords >= 10 || (existing?.isGoalMet == true)

        val updated = DailyLearningStatsEntity(
            dateString = dateString,
            dayLabel = dayLabel,
            wordsPracticed = newWords,
            correctCount = (existing?.correctCount ?: 0) + (wordsPracticed * accuracy).toInt(),
            minutesPracticed = newMinutes,
            sessionsCompleted = newSessions,
            accuracy = accuracy,
            isGoalMet = isGoalMet,
            timestamp = System.currentTimeMillis()
        )
        dao.saveDailyStat(updated)
        return updated
    }

    suspend fun seedInitialDataIfNeeded() {
        // Seed Vocabulary Items into Room DB if empty
        vocabularyDao?.let { vDao ->
            val total = vDao.getTotalCount("es")
            // Seed all default vocabulary items for all languages
            val entities = mutableListOf<VocabularyItemEntity>()
            for (item in allVocabulary) {
                for ((lang, trans) in item.translations) {
                    entities.add(
                        VocabularyItemEntity(
                            id = "${item.id}_${lang}",
                            englishWord = item.englishWord,
                            categoryId = item.category.id,
                            emoji = item.emoji,
                            phonetic = item.phonetic,
                            soundPrompt = item.soundPrompt,
                            translation = trans,
                            languageCode = lang,
                            colorHex = item.colorHex
                        )
                    )
                }
            }
            vDao.insertVocabularyList(entities)
        }

        // Seed 7-day initial stats for a realistic streak preview
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayLabelFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val mockDays = listOf(
            Triple(12, 10, 0.92f),
            Triple(16, 14, 0.95f),
            Triple(10, 8, 0.88f),
            Triple(14, 12, 0.94f),
            Triple(18, 15, 0.96f),
            Triple(20, 18, 0.98f),
            Triple(15, 13, 0.95f)
        )

        val initialStats = mutableListOf<DailyLearningStatsEntity>()
        for (i in 6 downTo 0) {
            val dateCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val dateStr = dateFormat.format(dateCal.time)
            val dayLbl = dayLabelFormat.format(dateCal.time)
            val stat = mockDays[6 - i]

            val existing = dao.getDailyStatForDate(dateStr)
            if (existing == null) {
                initialStats.add(
                    DailyLearningStatsEntity(
                        dateString = dateStr,
                        dayLabel = dayLbl,
                        wordsPracticed = stat.first,
                        minutesPracticed = stat.second,
                        accuracy = stat.third,
                        sessionsCompleted = 2,
                        isGoalMet = true,
                        timestamp = dateCal.timeInMillis
                    )
                )
            }
        }
        if (initialStats.isNotEmpty()) {
            dao.insertDailyStatsList(initialStats)
        }
    }

    fun getAllBadges(): Flow<List<BadgeEntity>> = dao.getAllBadges()
    fun getRecentSessions(): Flow<List<LearningSessionEntity>> = dao.getRecentSessions()
    fun getMasteredCount(langCode: String): Flow<Int> = dao.getMasteredCount(langCode)
    fun getRecent7DaysStats(): Flow<List<DailyLearningStatsEntity>> = dao.getRecent7DaysStats()
    fun getAllDailyStats(): Flow<List<DailyLearningStatsEntity>> = dao.getAllDailyStats()
    suspend fun getDailyStat(dateString: String): DailyLearningStatsEntity? = dao.getDailyStatForDate(dateString)
    suspend fun getAllDailyStatsSnapshot(): List<DailyLearningStatsEntity> = dao.getAllDailyStatsSnapshot()
    fun getStoredVocabularyFlow(langCode: String): Flow<List<VocabularyItemEntity>>? =
        vocabularyDao?.getAllVocabulary(langCode)

    // User Preferences
    fun getUserPreferencesFlow(): Flow<UserPreferencesEntity?> = dao.getUserPreferencesFlow()
    suspend fun getUserPreferencesSnapshot(): UserPreferencesEntity? = dao.getUserPreferencesSnapshot()
    suspend fun saveUserPreferences(preferences: UserPreferencesEntity) = dao.saveUserPreferences(preferences)

    fun calculateConsecutiveStreak(statsList: List<DailyLearningStatsEntity>): Int {
        if (statsList.isEmpty()) return 0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        var streak = 0

        // Check today
        val todayStr = dateFormat.format(cal.time)
        val todayStat = statsList.find { it.dateString == todayStr }
        val hasActivityToday = todayStat != null && todayStat.wordsPracticed > 0

        if (hasActivityToday) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            // Check yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateFormat.format(cal.time)
            val yesterdayStat = statsList.find { it.dateString == yesterdayStr }
            if (yesterdayStat == null || yesterdayStat.wordsPracticed == 0) {
                return 0
            }
        }

        // Count previous consecutive days
        while (true) {
            val dateStr = dateFormat.format(cal.time)
            val stat = statsList.find { it.dateString == dateStr }
            if (stat != null && stat.wordsPracticed > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
