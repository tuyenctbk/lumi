package com.example.data.repository

import com.example.data.db.BadgeEntity
import com.example.data.db.DailyLearningStatsEntity
import com.example.data.db.LearningSessionEntity
import com.example.data.db.Lesson
import com.example.data.db.LumiDao
import com.example.data.db.Progress
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
                "zh" to "猫 (Māo)", "en" to "Cat", "vi" to "Con mèo"
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
                "zh" to "狗 (Gǒu)", "en" to "Dog", "vi" to "Con chó"
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
                "zh" to "狮子 (Shīzi)", "en" to "Lion", "vi" to "Sư tử"
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
                "zh" to "大象 (Dàxiàng)", "en" to "Elephant", "vi" to "Con voi"
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
                "zh" to "鸟 (Niǎo)", "en" to "Bird", "vi" to "Con chim"
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
                "zh" to "青蛙 (Qīngwā)", "en" to "Frog", "vi" to "Con ếch"
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
                "zh" to "兔子 (Tùzǐ)", "en" to "Rabbit", "vi" to "Con thỏ"
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
                "zh" to "鱼 (Yú)", "en" to "Fish", "vi" to "Con cá"
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
                "zh" to "苹果 (Píngguǒ)", "en" to "Apple", "vi" to "Quả táo"
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
                "zh" to "香蕉 (Xiāngjiāo)", "en" to "Banana", "vi" to "Quả chuối"
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
                "zh" to "牛奶 (Niúnǎi)", "en" to "Milk", "vi" to "Sữa"
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
                "zh" to "面包 (Miànbāo)", "en" to "Bread", "vi" to "Bánh mì"
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
                "zh" to "冰淇淋 (Bīngqílín)", "en" to "Ice Cream", "vi" to "Kem"
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
                "zh" to "草莓 (Cǎoméi)", "en" to "Strawberry", "vi" to "Quả dâu tây"
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
                "zh" to "跑 (Pǎo)", "en" to "Run", "vi" to "Chạy"
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
                "zh" to "跳 (Tiào)", "en" to "Jump", "vi" to "Nhảy"
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
                "zh" to "睡觉 (Shuìjiào)", "en" to "Sleep", "vi" to "Ngủ"
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
                "zh" to "吃 (Chī)", "en" to "Eat", "vi" to "Ăn"
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
                "zh" to "跳舞 (Tiàowǔ)", "en" to "Dance", "vi" to "Nhảy múa"
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
                "zh" to "红色 (Hóngsè)", "en" to "Red", "vi" to "Màu đỏ"
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
                "zh" to "蓝色 (Lánsè)", "en" to "Blue", "vi" to "Màu xanh dương"
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
                "zh" to "黄色 (Huángsè)", "en" to "Yellow", "vi" to "Màu vàng"
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
                "zh" to "绿色 (Lǜsè)", "en" to "Green", "vi" to "Màu xanh lá"
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
                "zh" to "星星 (Xīngxing)", "en" to "Star", "vi" to "Ngôi sao"
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
                "zh" to "爱心 (Àixīn)", "en" to "Heart", "vi" to "Trái tim"
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
                "zh" to "太阳 (Tàiyáng)", "en" to "Sun", "vi" to "Mặt trời"
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
                "zh" to "月亮 (Yuèliang)", "en" to "Moon", "vi" to "Mặt trăng"
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
                "zh" to "火箭 (Huǒjiàn)", "en" to "Rocket", "vi" to "Tên lửa"
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
                "zh" to "行星 (Xíngxīng)", "en" to "Planet", "vi" to "Hành tinh"
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
                "zh" to "房子 (Fángzi)", "en" to "House", "vi" to "Ngôi nhà"
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
                "zh" to "汽车 (Qìchē)", "en" to "Car", "vi" to "Xe ô tô"
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
                "zh" to "书 (Shū)", "en" to "Book", "vi" to "Quyển sách"
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
                "zh" to "球 (Qiú)", "en" to "Ball", "vi" to "Quả bóng"
            ),
            colorHex = 0xFF81C784
        ),

        // NUMBERS & COUNTING
        VocabularyItem(
            id = "one",
            englishWord = "One",
            category = LearningCategory.NUMBERS,
            emoji = "1️⃣",
            phonetic = "wʌn",
            soundPrompt = "Number 1!",
            translations = mapOf(
                "es" to "Uno", "fr" to "Un", "de" to "Eins",
                "it" to "Uno", "ja" to "いち (Ichi)", "ko" to "하나 (Hana)",
                "zh" to "一 (Yī)", "en" to "One", "vi" to "Số một"
            ),
            colorHex = 0xFFFF7043
        ),
        VocabularyItem(
            id = "two",
            englishWord = "Two",
            category = LearningCategory.NUMBERS,
            emoji = "2️⃣",
            phonetic = "tuː",
            soundPrompt = "Number 2!",
            translations = mapOf(
                "es" to "Dos", "fr" to "Deux", "de" to "Zwei",
                "it" to "Due", "ja" to "に (Ni)", "ko" to "둘 (Dul)",
                "zh" to "二 (Èr)", "en" to "Two", "vi" to "Số hai"
            ),
            colorHex = 0xFFFF8A65
        ),
        VocabularyItem(
            id = "three",
            englishWord = "Three",
            category = LearningCategory.NUMBERS,
            emoji = "3️⃣",
            phonetic = "θriː",
            soundPrompt = "Number 3!",
            translations = mapOf(
                "es" to "Tres", "fr" to "Trois", "de" to "Drei",
                "it" to "Tre", "ja" to "さん (San)", "ko" to "셋 (Set)",
                "zh" to "三 (Sān)", "en" to "Three", "vi" to "Số ba"
            ),
            colorHex = 0xFFFFA726
        ),
        VocabularyItem(
            id = "four",
            englishWord = "Four",
            category = LearningCategory.NUMBERS,
            emoji = "4️⃣",
            phonetic = "fɔːr",
            soundPrompt = "Number 4!",
            translations = mapOf(
                "es" to "Cuatro", "fr" to "Quatre", "de" to "Vier",
                "it" to "Quattro", "ja" to "よん (Yon)", "ko" to "넷 (Net)",
                "zh" to "四 (Sì)", "en" to "Four", "vi" to "Số bốn"
            ),
            colorHex = 0xFFFFB74D
        ),
        VocabularyItem(
            id = "five",
            englishWord = "Five",
            category = LearningCategory.NUMBERS,
            emoji = "5️⃣",
            phonetic = "faɪv",
            soundPrompt = "High five!",
            translations = mapOf(
                "es" to "Cinco", "fr" to "Cinq", "de" to "Fünf",
                "it" to "Cinque", "ja" to "ご (Go)", "ko" to "다섯 (Daseot)",
                "zh" to "五 (Wǔ)", "en" to "Five", "vi" to "Số năm"
            ),
            colorHex = 0xFFFFCC80
        ),

        // FAMILY & FRIENDS
        VocabularyItem(
            id = "mother",
            englishWord = "Mother",
            category = LearningCategory.FAMILY,
            emoji = "👩",
            phonetic = "ˈmʌð.ər",
            soundPrompt = "Mommy love!",
            translations = mapOf(
                "es" to "Madre", "fr" to "Mère", "de" to "Mutter",
                "it" to "Madre", "ja" to "おかあさん (Okaasan)", "ko" to "엄마 (Eomma)",
                "zh" to "妈妈 (Māma)", "en" to "Mother", "vi" to "Mẹ"
            ),
            colorHex = 0xFF26A69A
        ),
        VocabularyItem(
            id = "father",
            englishWord = "Father",
            category = LearningCategory.FAMILY,
            emoji = "👨",
            phonetic = "ˈfɑː.ðər",
            soundPrompt = "Daddy hug!",
            translations = mapOf(
                "es" to "Padre", "fr" to "Père", "de" to "Vater",
                "it" to "Padre", "ja" to "おとうさん (Otousan)", "ko" to "아빠 (Appa)",
                "zh" to "爸爸 (Bàba)", "en" to "Father", "vi" to "Bố"
            ),
            colorHex = 0xFF4DB6AC
        ),
        VocabularyItem(
            id = "baby",
            englishWord = "Baby",
            category = LearningCategory.FAMILY,
            emoji = "👶",
            phonetic = "ˈbeɪ.bi",
            soundPrompt = "Goo goo gaa gaa!",
            translations = mapOf(
                "es" to "Bebé", "fr" to "Bébé", "de" to "Baby",
                "it" to "Bambino", "ja" to "赤ん坊 (Akanbou)", "ko" to "아기 (Agi)",
                "zh" to "婴儿 (Yīng'ér)", "en" to "Baby", "vi" to "Em bé"
            ),
            colorHex = 0xFF80CBC4
        ),
        VocabularyItem(
            id = "sister",
            englishWord = "Sister",
            category = LearningCategory.FAMILY,
            emoji = "👧",
            phonetic = "ˈsɪs.tər",
            soundPrompt = "Sister power!",
            translations = mapOf(
                "es" to "Hermana", "fr" to "Sœur", "de" to "Schwester",
                "it" to "Sorella", "ja" to "おねえさん (Oneesan)", "ko" to "언니 (Eonni)",
                "zh" to "姐妹 (Jiěmèi)", "en" to "Sister", "vi" to "Chị/Em gái"
            ),
            colorHex = 0xFFB2DFDB
        ),
        VocabularyItem(
            id = "brother",
            englishWord = "Brother",
            category = LearningCategory.FAMILY,
            emoji = "👦",
            phonetic = "ˈbrʌð.ər",
            soundPrompt = "High-five brother!",
            translations = mapOf(
                "es" to "Hermano", "fr" to "Frère", "de" to "Bruder",
                "it" to "Fratello", "ja" to "おにいさん (Oniisan)", "ko" to "오빠 (Oppa)",
                "zh" to "兄弟 (Xiōngdì)", "en" to "Brother", "vi" to "Anh/Em trai"
            ),
            colorHex = 0xFF00897B
        ),

        // VEHICLES & TRAVEL
        VocabularyItem(
            id = "airplane",
            englishWord = "Airplane",
            category = LearningCategory.VEHICLES,
            emoji = "✈️",
            phonetic = "ˈeə.pleɪn",
            soundPrompt = "Whoosh in the sky!",
            translations = mapOf(
                "es" to "Avión", "fr" to "Avion", "de" to "Flugzeug",
                "it" to "Aereo", "ja" to "ひこうき (Hikouki)", "ko" to "비행기 (Bihaenggi)",
                "zh" to "飞机 (Fēijī)", "en" to "Airplane", "vi" to "Máy bay"
            ),
            colorHex = 0xFF7E57C2
        ),
        VocabularyItem(
            id = "train",
            englishWord = "Train",
            category = LearningCategory.VEHICLES,
            emoji = "🚆",
            phonetic = "treɪn",
            soundPrompt = "Choo choo!",
            translations = mapOf(
                "es" to "Tren", "fr" to "Train", "de" to "Zug",
                "it" to "Treno", "ja" to "でんしゃ (Densha)", "ko" to "기차 (Gicha)",
                "zh" to "火车 (Huǒchē)", "en" to "Train", "vi" to "Tàu hỏa"
            ),
            colorHex = 0xFF9575CD
        ),
        VocabularyItem(
            id = "bicycle",
            englishWord = "Bicycle",
            category = LearningCategory.VEHICLES,
            emoji = "🚲",
            phonetic = "ˈbaɪ.sɪ.kəl",
            soundPrompt = "Ring ring!",
            translations = mapOf(
                "es" to "Bicicleta", "fr" to "Vélo", "de" to "Fahrrad",
                "it" to "Bicicletta", "ja" to "じてんしゃ (Jitensha)", "ko" to "자전거 (Jajeongeor)",
                "zh" to "自行车 (Zìxíngchē)", "en" to "Bicycle", "vi" to "Xe đạp"
            ),
            colorHex = 0xFFB39DDB
        ),
        VocabularyItem(
            id = "bus",
            englishWord = "Bus",
            category = LearningCategory.VEHICLES,
            emoji = "🚌",
            phonetic = "bʌs",
            soundPrompt = "Wheels on the bus!",
            translations = mapOf(
                "es" to "Autobús", "fr" to "Bus", "de" to "Bus",
                "it" to "Autobus", "ja" to "バス (Basu)", "ko" to "버스 (Beoseu)",
                "zh" to "公交车 (Gōngjiāochē)", "en" to "Bus", "vi" to "Xe buýt"
            ),
            colorHex = 0xFF5E35B1
        ),
        VocabularyItem(
            id = "boat",
            englishWord = "Boat",
            category = LearningCategory.VEHICLES,
            emoji = "⛵",
            phonetic = "bəʊt",
            soundPrompt = "Sailing on water!",
            translations = mapOf(
                "es" to "Barco", "fr" to "Bateau", "de" to "Boot",
                "it" to "Barca", "ja" to "ふね (Fune)", "ko" to "배 (Bae)",
                "zh" to "船 (Chuán)", "en" to "Boat", "vi" to "Thuyền"
            ),
            colorHex = 0xFF512DA8
        ),

        // NATURE & WEATHER
        VocabularyItem(
            id = "tree",
            englishWord = "Tree",
            category = LearningCategory.NATURE,
            emoji = "🌳",
            phonetic = "triː",
            soundPrompt = "Tall green tree!",
            translations = mapOf(
                "es" to "Árbol", "fr" to "Arbre", "de" to "Baum",
                "it" to "Albero", "ja" to "き (Ki)", "ko" to "나무 (Namu)",
                "zh" to "树 (Shù)", "en" to "Tree", "vi" to "Cây"
            ),
            colorHex = 0xFF43A047
        ),
        VocabularyItem(
            id = "flower",
            englishWord = "Flower",
            category = LearningCategory.NATURE,
            emoji = "🌸",
            phonetic = "ˈflaʊ.ər",
            soundPrompt = "Pretty bloom!",
            translations = mapOf(
                "es" to "Flor", "fr" to "Fleur", "de" to "Blume",
                "it" to "Fiore", "ja" to "はな (Hana)", "ko" to "꽃 (Kkot)",
                "zh" to "花 (Huā)", "en" to "Flower", "vi" to "Bông hoa"
            ),
            colorHex = 0xFF66BB6A
        ),
        VocabularyItem(
            id = "mountain",
            englishWord = "Mountain",
            category = LearningCategory.NATURE,
            emoji = "⛰️",
            phonetic = "ˈmaʊn.tɪn",
            soundPrompt = "High peak!",
            translations = mapOf(
                "es" to "Montaña", "fr" to "Montagne", "de" to "Berg",
                "it" to "Montagna", "ja" to "やま (Yama)", "ko" to "산 (San)",
                "zh" to "山 (Shān)", "en" to "Mountain", "vi" to "Ngọn núi"
            ),
            colorHex = 0xFF81C784
        ),
        VocabularyItem(
            id = "rain",
            englishWord = "Rain",
            category = LearningCategory.NATURE,
            emoji = "🌧️",
            phonetic = "reɪn",
            soundPrompt = "Pitter patter rain!",
            translations = mapOf(
                "es" to "Lluvia", "fr" to "Pluie", "de" to "Regen",
                "it" to "Pioggia", "ja" to "あめ (Ame)", "ko" to "비 (Bi)",
                "zh" to "雨 (Yǔ)", "en" to "Rain", "vi" to "Mưa"
            ),
            colorHex = 0xFFA5D6A7
        ),
        VocabularyItem(
            id = "rainbow",
            englishWord = "Rainbow",
            category = LearningCategory.NATURE,
            emoji = "🌈",
            phonetic = "ˈreɪn.bəʊ",
            soundPrompt = "Colorful sky arc!",
            translations = mapOf(
                "es" to "Arcoíris", "fr" to "Arc-en-ciel", "de" to "Regenbogen",
                "it" to "Arcobaleno", "ja" to "にじ (Niji)", "ko" to "무지개 (Mujigae)",
                "zh" to "彩虹 (Cǎihóng)", "en" to "Rainbow", "vi" to "Cầu vồng"
            ),
            colorHex = 0xFF2E7D32
        ),

        // CLOTHES & DRESSING
        VocabularyItem(
            id = "shirt",
            englishWord = "Shirt",
            category = LearningCategory.CLOTHES,
            emoji = "👕",
            phonetic = "ʃɜːt",
            soundPrompt = "Comfy shirt!",
            translations = mapOf(
                "es" to "Camisa", "fr" to "Chemise", "de" to "Hemd",
                "it" to "Camicia", "ja" to "シャツ (Shirt)", "ko" to "셔츠 (Syeocheu)",
                "zh" to "衬衫 (Chènshān)", "en" to "Shirt", "vi" to "Áo sơ mi"
            ),
            colorHex = 0xFFF06292
        ),
        VocabularyItem(
            id = "shoes",
            englishWord = "Shoes",
            category = LearningCategory.CLOTHES,
            emoji = "👟",
            phonetic = "ʃuːz",
            soundPrompt = "Walk and run!",
            translations = mapOf(
                "es" to "Zapatos", "fr" to "Chaussures", "de" to "Schuhe",
                "it" to "Scarpe", "ja" to "くつ (Kutsu)", "ko" to "신발 (Sinbal)",
                "zh" to "鞋子 (Xiézi)", "en" to "Shoes", "vi" to "Đôi giày"
            ),
            colorHex = 0xFFF48FB1
        ),
        VocabularyItem(
            id = "hat",
            englishWord = "Hat",
            category = LearningCategory.CLOTHES,
            emoji = "🧢",
            phonetic = "hæt",
            soundPrompt = "Put on your hat!",
            translations = mapOf(
                "es" to "Sombrero", "fr" to "Chapeau", "de" to "Hut",
                "it" to "Cappello", "ja" to "ぼうし (Boushi)", "ko" to "모자 (Moja)",
                "zh" to "帽子 (Màozi)", "en" to "Hat", "vi" to "Cái mũ"
            ),
            colorHex = 0xFFE91E63
        ),

        // BODY & HEALTH
        VocabularyItem(
            id = "eyes",
            englishWord = "Eyes",
            category = LearningCategory.BODY,
            emoji = "👁️",
            phonetic = "aɪz",
            soundPrompt = "Blink blink!",
            translations = mapOf(
                "es" to "Ojos", "fr" to "Yeux", "de" to "Augen",
                "it" to "Occhi", "ja" to "め (Me)", "ko" to "눈 (Nun)",
                "zh" to "眼睛 (Yǎnjing)", "en" to "Eyes", "vi" to "Đôi mắt"
            ),
            colorHex = 0xFF1E88E5
        ),
        VocabularyItem(
            id = "hands",
            englishWord = "Hands",
            category = LearningCategory.BODY,
            emoji = "✋",
            phonetic = "hændz",
            soundPrompt = "Clap your hands!",
            translations = mapOf(
                "es" to "Manos", "fr" to "Mains", "de" to "Hände",
                "it" to "Mani", "ja" to "て (Te)", "ko" to "손 (Son)",
                "zh" to "手 (Shǒu)", "en" to "Hands", "vi" to "Bàn tay"
            ),
            colorHex = 0xFF42A5F5
        ),

        // NUMBERS 6-10
        VocabularyItem(
            id = "six",
            englishWord = "Six",
            category = LearningCategory.NUMBERS,
            emoji = "6️⃣",
            phonetic = "sɪks",
            soundPrompt = "Number 6!",
            translations = mapOf(
                "es" to "Seis", "fr" to "Six", "de" to "Sechs",
                "it" to "Sei", "ja" to "ろく (Roku)", "ko" to "여섯 (Yeoseot)",
                "zh" to "六 (Liù)", "en" to "Six", "vi" to "Số sáu"
            ),
            colorHex = 0xFFFF7043
        ),
        VocabularyItem(
            id = "seven",
            englishWord = "Seven",
            category = LearningCategory.NUMBERS,
            emoji = "7️⃣",
            phonetic = "ˈsɛv.ən",
            soundPrompt = "Lucky number 7!",
            translations = mapOf(
                "es" to "Siete", "fr" to "Sept", "de" to "Sieben",
                "it" to "Sette", "ja" to "なな (Nana)", "ko" to "일곱 (Ilgop)",
                "zh" to "七 (Qī)", "en" to "Seven", "vi" to "Số bảy"
            ),
            colorHex = 0xFFFF8A65
        ),
        VocabularyItem(
            id = "eight",
            englishWord = "Eight",
            category = LearningCategory.NUMBERS,
            emoji = "8️⃣",
            phonetic = "eɪt",
            soundPrompt = "Great number 8!",
            translations = mapOf(
                "es" to "Ocho", "fr" to "Huit", "de" to "Acht",
                "it" to "Otto", "ja" to "はち (Hachi)", "ko" to "여덟 (Yeodeol)",
                "zh" to "八 (Bā)", "en" to "Eight", "vi" to "Số tám"
            ),
            colorHex = 0xFFFFA726
        ),
        VocabularyItem(
            id = "nine",
            englishWord = "Nine",
            category = LearningCategory.NUMBERS,
            emoji = "9️⃣",
            phonetic = "naɪn",
            soundPrompt = "Number 9!",
            translations = mapOf(
                "es" to "Nueve", "fr" to "Neuf", "de" to "Neun",
                "it" to "Nove", "ja" to "きゅう (Kyū)", "ko" to "아홉 (Ahop)",
                "zh" to "九 (Jiǔ)", "en" to "Nine", "vi" to "Số chín"
            ),
            colorHex = 0xFFFFB74D
        ),
        VocabularyItem(
            id = "ten",
            englishWord = "Ten",
            category = LearningCategory.NUMBERS,
            emoji = "🔟",
            phonetic = "tɛn",
            soundPrompt = "Perfect 10!",
            translations = mapOf(
                "es" to "Diez", "fr" to "Dix", "de" to "Zehn",
                "it" to "Dieci", "ja" to "じゅう (Jū)", "ko" to "열 (Yeol)",
                "zh" to "十 (Shí)", "en" to "Ten", "vi" to "Số mười"
            ),
            colorHex = 0xFFFFCC80
        ),

        // MUSIC & INSTRUMENTS
        VocabularyItem(
            id = "piano",
            englishWord = "Piano",
            category = LearningCategory.MUSIC,
            emoji = "🎹",
            phonetic = "piˈæn.oʊ",
            soundPrompt = "Plink plank plonk!",
            translations = mapOf(
                "es" to "Piano", "fr" to "Piano", "de" to "Klavier",
                "it" to "Pianoforte", "ja" to "ピアノ (Piano)", "ko" to "피아노 (Piano)",
                "zh" to "钢琴 (Gāngqín)", "en" to "Piano", "vi" to "Đàn dương cầm"
            ),
            colorHex = 0xFF8E24AA
        ),
        VocabularyItem(
            id = "guitar",
            englishWord = "Guitar",
            category = LearningCategory.MUSIC,
            emoji = "🎸",
            phonetic = "ɡɪˈtɑːr",
            soundPrompt = "Strum strum strum!",
            translations = mapOf(
                "es" to "Guitarra", "fr" to "Guitare", "de" to "Gitarre",
                "it" to "Chitarra", "ja" to "ギター (Gitā)", "ko" to "기타 (Gita)",
                "zh" to "吉他 (Jítā)", "en" to "Guitar", "vi" to "Đàn ghi-ta"
            ),
            colorHex = 0xFFAB47BC
        ),
        VocabularyItem(
            id = "drum",
            englishWord = "Drum",
            category = LearningCategory.MUSIC,
            emoji = "🥁",
            phonetic = "drʌm",
            soundPrompt = "Boom boom tap!",
            translations = mapOf(
                "es" to "Tambor", "fr" to "Tambour", "de" to "Trommel",
                "it" to "Tamburo", "ja" to "たいこ (Taiko)", "ko" to "드럼 (Deureom)",
                "zh" to "鼓 (Gǔ)", "en" to "Drum", "vi" to "Cái trống"
            ),
            colorHex = 0xFFBA68C8
        ),
        VocabularyItem(
            id = "violin",
            englishWord = "Violin",
            category = LearningCategory.MUSIC,
            emoji = "🎻",
            phonetic = "ˌvaɪəˈlɪn",
            soundPrompt = "Sweet sweet strings!",
            translations = mapOf(
                "es" to "Violín", "fr" to "Violon", "de" to "Geige",
                "it" to "Violino", "ja" to "バイオリン (Baiorin)", "ko" to "바이올린 (Baiollin)",
                "zh" to "小提琴 (Xiǎotíqín)", "en" to "Violin", "vi" to "Đàn vĩ cầm"
            ),
            colorHex = 0xFFCE93D8
        ),
        VocabularyItem(
            id = "trumpet",
            englishWord = "Trumpet",
            category = LearningCategory.MUSIC,
            emoji = "🎺",
            phonetic = "ˈtrʌm.pɪt",
            soundPrompt = "Toot toot toot!",
            translations = mapOf(
                "es" to "Trompeta", "fr" to "Trompette", "de" to "Trompete",
                "it" to "Tromba", "ja" to "トランペット (Toranpetto)", "ko" to "트럼펫 (Teureompet)",
                "zh" to "小号 (Xiǎohào)", "en" to "Trumpet", "vi" to "Kèn trumpet"
            ),
            colorHex = 0xFF9C27B0
        ),

        // SPORTS & PLAY
        VocabularyItem(
            id = "soccer",
            englishWord = "Soccer",
            category = LearningCategory.SPORTS,
            emoji = "⚽",
            phonetic = "ˈsɒk.ər",
            soundPrompt = "Goal! Kick the ball!",
            translations = mapOf(
                "es" to "Fútbol", "fr" to "Football", "de" to "Fußball",
                "it" to "Calcio", "ja" to "サッカー (Sakkā)", "ko" to "축구 (Chukgu)",
                "zh" to "足球 (Zúqiú)", "en" to "Soccer", "vi" to "Bóng đá"
            ),
            colorHex = 0xFFFF5722
        ),
        VocabularyItem(
            id = "basketball",
            englishWord = "Basketball",
            category = LearningCategory.SPORTS,
            emoji = "🏀",
            phonetic = "ˈbɑː.skɪt.bɔːl",
            soundPrompt = "Swoosh! Dribble and shoot!",
            translations = mapOf(
                "es" to "Baloncesto", "fr" to "Basket", "de" to "Basketball",
                "it" to "Pallacanestro", "ja" to "バスケ (Basuke)", "ko" to "농구 (Nong-gu)",
                "zh" to "篮球 (Lánqiú)", "en" to "Basketball", "vi" to "Bóng rổ"
            ),
            colorHex = 0xFFFF7043
        ),
        VocabularyItem(
            id = "swimming",
            englishWord = "Swimming",
            category = LearningCategory.SPORTS,
            emoji = "🏊",
            phonetic = "ˈswɪm.ɪŋ",
            soundPrompt = "Splash splash glide!",
            translations = mapOf(
                "es" to "Natación", "fr" to "Natation", "de" to "Schwimmen",
                "it" to "Nuoto", "ja" to "すいえい (Suiei)", "ko" to "수영 (Suyeong)",
                "zh" to "游泳 (Yóuyǒng)", "en" to "Swimming", "vi" to "Bơi lội"
            ),
            colorHex = 0xFF0288D1
        ),
        VocabularyItem(
            id = "cycling",
            englishWord = "Cycling",
            category = LearningCategory.SPORTS,
            emoji = "🚴",
            phonetic = "ˈsaɪ.klɪŋ",
            soundPrompt = "Pedal fast!",
            translations = mapOf(
                "es" to "Ciclismo", "fr" to "Cyclisme", "de" to "Radfahren",
                "it" to "Ciclismo", "ja" to "サイクリング", "ko" to "자전거 (Jajeon-geo)",
                "zh" to "骑自行车 (Qí zìxíngchē)", "en" to "Cycling", "vi" to "Đi xe đạp"
            ),
            colorHex = 0xFF26A69A
        ),

        // FEELINGS & MOODS
        VocabularyItem(
            id = "happy",
            englishWord = "Happy",
            category = LearningCategory.FEELINGS,
            emoji = "😄",
            phonetic = "ˈhæp.i",
            soundPrompt = "Big bright smile!",
            translations = mapOf(
                "es" to "Feliz", "fr" to "Heureux", "de" to "Glücklich",
                "it" to "Felice", "ja" to "うれしい (Ureshii)", "ko" to "행복한 (Haengbokhan)",
                "zh" to "开心 (Kāixīn)", "en" to "Happy", "vi" to "Vui vẻ"
            ),
            colorHex = 0xFF00ACC1
        ),
        VocabularyItem(
            id = "excited",
            englishWord = "Excited",
            category = LearningCategory.FEELINGS,
            emoji = "🤩",
            phonetic = "ɪkˈsaɪ.tɪd",
            soundPrompt = "Yay! So fun!",
            translations = mapOf(
                "es" to "Emocionado", "fr" to "Enthousiaste", "de" to "Begeistert",
                "it" to "Emozionato", "ja" to "わくわく (Wakuwaku)", "ko" to "신나는 (Sinnaneun)",
                "zh" to "兴奋 (Xīngfèn)", "en" to "Excited", "vi" to "Hào hứng"
            ),
            colorHex = 0xFF26C6DA
        ),
        VocabularyItem(
            id = "curious",
            englishWord = "Curious",
            category = LearningCategory.FEELINGS,
            emoji = "🧐",
            phonetic = "ˈkjʊə.ri.əs",
            soundPrompt = "I wonder why!",
            translations = mapOf(
                "es" to "Curioso", "fr" to "Curieux", "de" to "Neugierig",
                "it" to "Curioso", "ja" to "こうきしん (Kōkishin)", "ko" to "호기심 (Hogisim)",
                "zh" to "好奇 (Hàoqí)", "en" to "Curious", "vi" to "Tò mò"
            ),
            colorHex = 0xFF00838F
        ),
        VocabularyItem(
            id = "brave",
            englishWord = "Brave",
            category = LearningCategory.FEELINGS,
            emoji = "🦁",
            phonetic = "breɪv",
            soundPrompt = "Strong and courageous!",
            translations = mapOf(
                "es" to "Valiente", "fr" to "Courageux", "de" to "Mutig",
                "it" to "Coraggioso", "ja" to "ゆうき (Yūki)", "ko" to "용감한 (Yong-gamhan)",
                "zh" to "勇敢 (Yǒnggǎn)", "en" to "Brave", "vi" to "Dũng cảm"
            ),
            colorHex = 0xFF0097A7
        ),

        // MORE ANIMALS
        VocabularyItem(
            id = "dolphin",
            englishWord = "Dolphin",
            category = LearningCategory.ANIMALS,
            emoji = "🐬",
            phonetic = "ˈdɒl.fɪn",
            soundPrompt = "Click click splash!",
            translations = mapOf(
                "es" to "Delfín", "fr" to "Dauphin", "de" to "Delfin",
                "it" to "Delfino", "ja" to "イルカ (Iruka)", "ko" to "돌고래 (Dolgorae)",
                "zh" to "海豚 (Hǎitún)", "en" to "Dolphin", "vi" to "Cá heo"
            ),
            colorHex = 0xFF039BE5
        ),
        VocabularyItem(
            id = "penguin",
            englishWord = "Penguin",
            category = LearningCategory.ANIMALS,
            emoji = "🐧",
            phonetic = "ˈpɛŋ.ɡwɪn",
            soundPrompt = "Waddle on the ice!",
            translations = mapOf(
                "es" to "Pingüino", "fr" to "Manchot", "de" to "Pinguin",
                "it" to "Pinguino", "ja" to "ペンギン (Pengin)", "ko" to "펭귄 (Peng-gwin)",
                "zh" to "企鹅 (Qǐ'é)", "en" to "Penguin", "vi" to "Chim cánh cụt"
            ),
            colorHex = 0xFF546E7A
        ),
        VocabularyItem(
            id = "butterfly",
            englishWord = "Butterfly",
            category = LearningCategory.ANIMALS,
            emoji = "🦋",
            phonetic = "ˈbʌt.ə.flaɪ",
            soundPrompt = "Flutter flutter high!",
            translations = mapOf(
                "es" to "Mariposa", "fr" to "Papillon", "de" to "Schmetterling",
                "it" to "Farfalla", "ja" to "ちょう (Chō)", "ko" to "나비 (Nabi)",
                "zh" to "蝴蝶 (Húdié)", "en" to "Butterfly", "vi" to "Con bướm"
            ),
            colorHex = 0xFF7E57C2
        ),

        // MORE FOOD
        VocabularyItem(
            id = "pizza",
            englishWord = "Pizza",
            category = LearningCategory.FOOD,
            emoji = "🍕",
            phonetic = "ˈpiːt.sə",
            soundPrompt = "Cheesy slice!",
            translations = mapOf(
                "es" to "Pizza", "fr" to "Pizza", "de" to "Pizza",
                "it" to "Pizza", "ja" to "ピザ (Piza)", "ko" to "피자 (Pija)",
                "zh" to "披萨 (Pīsà)", "en" to "Pizza", "vi" to "Bánh pizza"
            ),
            colorHex = 0xFFFF7043
        ),
        VocabularyItem(
            id = "cookie",
            englishWord = "Cookie",
            category = LearningCategory.FOOD,
            emoji = "🍪",
            phonetic = "ˈkʊk.i",
            soundPrompt = "Crunch crunch yummy!",
            translations = mapOf(
                "es" to "Galleta", "fr" to "Biscuit", "de" to "Keks",
                "it" to "Biscotto", "ja" to "クッキー (Kukkī)", "ko" to "쿠키 (Kuki)",
                "zh" to "饼干 (Bǐnggān)", "en" to "Cookie", "vi" to "Bánh quy"
            ),
            colorHex = 0xFF8D6E63
        ),
        VocabularyItem(
            id = "watermelon",
            englishWord = "Watermelon",
            category = LearningCategory.FOOD,
            emoji = "🍉",
            phonetic = "ˈwɔː.təˌmɛl.ən",
            soundPrompt = "Sweet juicy bite!",
            translations = mapOf(
                "es" to "Sandía", "fr" to "Pastèque", "de" to "Wassermelone",
                "it" to "Anguria", "ja" to "スイカ (Suika)", "ko" to "수박 (Subak)",
                "zh" to "西瓜 (Xīguā)", "en" to "Watermelon", "vi" to "Dưa hấu"
            ),
            colorHex = 0xFFE91E63
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
            val total = vDao.getTotalCountDirect("es")
            if (total == 0) {
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
        }

        // Seed Structured Lessons for each category and language
        val languages = listOf("es", "fr", "de", "it", "ja", "ko", "zh", "en", "vi")
        val lessonEntities = mutableListOf<Lesson>()
        for (category in LearningCategory.entries) {
            for (lang in languages) {
                val stage1Id = "lesson_${category.id}_${lang}_stage1"
                val stage2Id = "lesson_${category.id}_${lang}_stage2"
                val stage3Id = "lesson_${category.id}_${lang}_stage3"

                if (dao.getLessonById(stage1Id) == null) {
                    lessonEntities.add(
                        Lesson(
                            id = stage1Id,
                            title = "${category.title}: Stage 1 (Beginner)",
                            category = category.id,
                            languageCode = lang,
                            totalExercises = 5,
                            completedExercises = 0,
                            isCompleted = false,
                            score = 0
                        )
                    )
                }
                if (dao.getLessonById(stage2Id) == null) {
                    lessonEntities.add(
                        Lesson(
                            id = stage2Id,
                            title = "${category.title}: Stage 2 (Explorer)",
                            category = category.id,
                            languageCode = lang,
                            totalExercises = 6,
                            completedExercises = 0,
                            isCompleted = false,
                            score = 0
                        )
                    )
                }
                if (dao.getLessonById(stage3Id) == null) {
                    lessonEntities.add(
                        Lesson(
                            id = stage3Id,
                            title = "${category.title}: Stage 3 (Master)",
                            category = category.id,
                            languageCode = lang,
                            totalExercises = 8,
                            completedExercises = 0,
                            isCompleted = false,
                            score = 0
                        )
                    )
                }
            }
        }
        if (lessonEntities.isNotEmpty()) {
            dao.insertLessons(lessonEntities)
        }

        // Ensure default user preference is initialized
        if (dao.getUserPreferencesSnapshot() == null) {
            dao.saveUserPreferences(
                UserPreferencesEntity(
                    id = 1,
                    activeLanguageCode = "es",
                    dailyGoalMinutes = 10,
                    isSoundEnabled = true,
                    isNotificationsEnabled = true,
                    totalStarsEarned = 0,
                    physicalBreaksCount = 0
                )
            )
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

    // Lessons & Progress Room DB API
    fun getAllLessons(languageCode: String): Flow<List<Lesson>> = dao.getAllLessons(languageCode)
    suspend fun getLessonById(id: String): Lesson? = dao.getLessonById(id)
    suspend fun saveLesson(lesson: Lesson) = dao.insertLesson(lesson)
    suspend fun saveLessons(lessons: List<Lesson>) = dao.insertLessons(lessons)
    suspend fun updateLesson(lesson: Lesson) = dao.updateLesson(lesson)
    fun getCompletedLessonsCount(languageCode: String): Flow<Int> = dao.getCompletedLessonsCount(languageCode)

    fun getAllProgressHistory(languageCode: String): Flow<List<Progress>> = dao.getAllProgressHistory(languageCode)
    fun getProgressForLesson(lessonId: String): Flow<List<Progress>> = dao.getProgressForLesson(lessonId)
    suspend fun recordProgress(progress: Progress) = dao.recordProgress(progress)
    suspend fun recordProgressList(list: List<Progress>) = dao.recordProgressList(list)

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
