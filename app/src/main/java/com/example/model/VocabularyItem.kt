package com.example.model

enum class LearningCategory(
    val id: String,
    val title: String,
    val emoji: String,
    val colorHex: Long,
    val secondaryColorHex: Long
) {
    ANIMALS("animals", "Animals & Nature", "🦁", 0xFF4CAF50, 0xFF81C784),
    FOOD("food", "Yummy Food", "🍎", 0xFFFF9800, 0xFFFFB74D),
    ACTIONS("actions", "Action Verbs", "🏃", 0xFFE91E63, 0xFFF06292),
    COLORS("colors", "Colors & Shapes", "🎨", 0xFF9C27B0, 0xFFBA68C8),
    SPACE("space", "Space & Stars", "🚀", 0xFF3F51B5, 0xFF7986CB),
    HOME("home", "Everyday Objects", "🏠", 0xFF00BCD4, 0xFF4DD0E1)
}

enum class TargetLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String,
    val localeTag: String
) {
    SPANISH("es", "Spanish (Español)", "🇪🇸", "es-ES"),
    FRENCH("fr", "French (Français)", "🇫🇷", "fr-FR"),
    GERMAN("de", "German (Deutsch)", "🇩🇪", "de-DE"),
    ITALIAN("it", "Italian (Italiano)", "🇮🇹", "it-IT"),
    JAPANESE("ja", "Japanese (日本語)", "🇯🇵", "ja-JP"),
    KOREAN("ko", "Korean (한국어)", "🇰🇷", "ko-KR"),
    MANDARIN("zh", "Chinese (中文)", "🇨🇳", "zh-CN"),
    ENGLISH("en", "English", "🇬🇧", "en-US");

    companion object {
        fun fromCode(code: String): TargetLanguage = entries.find { it.code == code } ?: SPANISH
    }
}

data class VocabularyItem(
    val id: String,
    val englishWord: String,
    val category: LearningCategory,
    val emoji: String,
    val phonetic: String = "",
    val soundPrompt: String = "",
    val translations: Map<String, String>, // LangCode -> Translated Word
    val pronunciations: Map<String, String> = emptyMap(),
    val imageUrl: String = "",
    val colorHex: Long = 0xFF6200EE
)

enum class MascotMood {
    IDLE,
    HOVER,
    HAPPY,
    ENCOURAGING,
    SUPERSTAR,
    THINKING,
    TALKING,
    CELEBRATING
}
