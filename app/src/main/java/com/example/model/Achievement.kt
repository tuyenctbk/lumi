package com.example.model

/**
 * Achievement definition model representing digital milestones for children.
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: String, // "Words", "Streaks", "Quests", "Games"
    val targetGoal: Int,
    val progressExtractor: (masteredWords: Int, streakDays: Int, physicalBreaks: Int, totalSessions: Int) -> Int
)

object AchievementCatalog {
    val ALL_ACHIEVEMENTS = listOf(
        Achievement(
            id = "first_step",
            title = "First Words Explorer",
            description = "Answer your first question correctly!",
            iconEmoji = "🌟",
            category = "Words",
            targetGoal = 1,
            progressExtractor = { mastered, _, _, _ -> mastered.coerceAtLeast(1) }
        ),
        Achievement(
            id = "words_5",
            title = "Vocabulary Scout",
            description = "Master 5 new vocabulary words!",
            iconEmoji = "🔍",
            category = "Words",
            targetGoal = 5,
            progressExtractor = { mastered, _, _, _ -> mastered }
        ),
        Achievement(
            id = "words_10",
            title = "10 Words Master",
            description = "Master 10 vocabulary words across all categories!",
            iconEmoji = "🎓",
            category = "Words",
            targetGoal = 10,
            progressExtractor = { mastered, _, _, _ -> mastered }
        ),
        Achievement(
            id = "words_20",
            title = "Polyglot Prodigy",
            description = "Master 20 vocabulary words!",
            iconEmoji = "👑",
            category = "Words",
            targetGoal = 20,
            progressExtractor = { mastered, _, _, _ -> mastered }
        ),
        Achievement(
            id = "streak_3",
            title = "3-Day Learning Spark",
            description = "Practice 3 days in a row!",
            iconEmoji = "🔥",
            category = "Streaks",
            targetGoal = 3,
            progressExtractor = { _, streak, _, _ -> streak }
        ),
        Achievement(
            id = "streak_7",
            title = "7-Day Streak Superstar",
            description = "Reach a 7-day learning streak!",
            iconEmoji = "⚡",
            category = "Streaks",
            targetGoal = 7,
            progressExtractor = { _, streak, _, _ -> streak }
        ),
        Achievement(
            id = "streak_10",
            title = "10-Day Streak Legend",
            description = "Maintain a 10-day consecutive learning streak!",
            iconEmoji = "🏆",
            category = "Streaks",
            targetGoal = 10,
            progressExtractor = { _, streak, _, _ -> streak }
        ),
        Achievement(
            id = "words_50",
            title = "50 Words Polyglot",
            description = "Master 50 vocabulary words!",
            iconEmoji = "👑",
            category = "Words",
            targetGoal = 50,
            progressExtractor = { mastered, _, _, _ -> mastered }
        ),
        Achievement(
            id = "words_100",
            title = "100 Words Lexicon Legend",
            description = "Master 100 vocabulary words across all categories!",
            iconEmoji = "💎",
            category = "Words",
            targetGoal = 100,
            progressExtractor = { mastered, _, _, _ -> mastered }
        ),
        Achievement(
            id = "break_3",
            title = "Active Movement Champ",
            description = "Complete 3 off-screen physical movement breaks!",
            iconEmoji = "🤸",
            category = "Quests",
            targetGoal = 3,
            progressExtractor = { _, _, breaks, _ -> breaks }
        ),
        Achievement(
            id = "find_it_detective",
            title = "Find It Detective",
            description = "Play and complete the Find It audio matching game!",
            iconEmoji = "🔎",
            category = "Games",
            targetGoal = 1,
            progressExtractor = { _, _, _, sessions -> sessions.coerceAtLeast(1) }
        )
    )
}
