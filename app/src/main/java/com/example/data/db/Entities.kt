package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * WordProgressEntity
 *
 * Room Database entity tracking individual vocabulary learning progress,
 * error frequencies, and SuperMemo SM-2 style Spaced Repetition (SRS) variables.
 */
@Entity(tableName = "word_progress")
data class WordProgressEntity(
    @PrimaryKey val wordId: String,
    val languageCode: String,
    val correctCount: Int = 0,
    val errorCount: Int = 0,
    val streak: Int = 0,
    val lastReviewedAt: Long = System.currentTimeMillis(),
    val nextReviewAt: Long = System.currentTimeMillis(),
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 1,
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false
) {
    /**
     * Calculates the historical accuracy / retention rate for this item.
     */
    val accuracy: Float
        get() {
            val total = correctCount + errorCount
            return if (total == 0) 1.0f else correctCount.toFloat() / total
        }
}

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val categoryId: String = ""
)

@Entity(tableName = "learning_sessions")
data class LearningSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val gameType: String,
    val wordsPracticed: Int,
    val accuracy: Float,
    val durationSeconds: Int
)
