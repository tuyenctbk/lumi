package com.example.util

import com.example.data.db.WordProgressEntity

data class DifficultyConfig(
    val levelNumber: Int,
    val levelName: String,
    val badgeEmoji: String,
    val optionsCount: Int,
    val timeLimitSeconds: Int,
    val distractorComplexity: String,
    val allowReverseQuestions: Boolean,
    val allowSpellingChallenge: Boolean,
    val description: String
)

/**
 * DynamicDifficultyManager
 *
 * Evaluates the user's proficiency score in the Room database based on mastered vocabulary count,
 * historical quiz accuracy, and streak length, dynamically adjusting quiz parameters (options, timer, complexity).
 */
object DynamicDifficultyManager {

    fun calculateProficiencyScore(
        masteredCount: Int,
        totalTrackedWords: Int,
        overallAccuracy: Float,
        streakCount: Int
    ): Int {
        val totalWords = totalTrackedWords.coerceAtLeast(10)
        val masteredRatio = (masteredCount.toFloat() / totalWords.toFloat()).coerceIn(0f, 1f)
        
        val masteredPoints = (masteredRatio * 50f).toInt()
        val accuracyPoints = (overallAccuracy.coerceIn(0f, 1f) * 30f).toInt()
        val streakPoints = (minOf(streakCount, 10) * 2)

        return (masteredPoints + accuracyPoints + streakPoints).coerceIn(0, 100)
    }

    fun getDifficultyConfig(proficiencyScore: Int): DifficultyConfig {
        return when {
            proficiencyScore < 25 -> DifficultyConfig(
                levelNumber = 1,
                levelName = "Beginner Explorer",
                badgeEmoji = "🌱",
                optionsCount = 3,
                timeLimitSeconds = 30,
                distractorComplexity = "Simple Distinct Options",
                allowReverseQuestions = false,
                allowSpellingChallenge = false,
                description = "Gentle pace with 3 distinct options and generous timer."
            )
            proficiencyScore < 55 -> DifficultyConfig(
                levelNumber = 2,
                levelName = "Intermediate Learner",
                badgeEmoji = "⚡",
                optionsCount = 4,
                timeLimitSeconds = 20,
                distractorComplexity = "Category-based Distractors",
                allowReverseQuestions = true,
                allowSpellingChallenge = false,
                description = "4 choices with category distractors & bi-directional translations."
            )
            proficiencyScore < 82 -> DifficultyConfig(
                levelNumber = 3,
                levelName = "Advanced Scholar",
                badgeEmoji = "🎓",
                optionsCount = 4,
                timeLimitSeconds = 15,
                distractorComplexity = "Similar Phonetic Distractors",
                allowReverseQuestions = true,
                allowSpellingChallenge = true,
                description = "Faster audio prompts, similar phonetic choices & spell tests."
            )
            else -> DifficultyConfig(
                levelNumber = 4,
                levelName = "Polyglot Master",
                badgeEmoji = "👑",
                optionsCount = 5,
                timeLimitSeconds = 10,
                distractorComplexity = "Challenging Near-Synonyms",
                allowReverseQuestions = true,
                allowSpellingChallenge = true,
                description = "5 high-speed choice choices, tight timer & master challenges."
            )
        }
    }
}
