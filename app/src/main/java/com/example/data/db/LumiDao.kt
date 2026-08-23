package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LumiDao {
    @Query("SELECT * FROM word_progress WHERE languageCode = :langCode")
    fun getAllProgress(langCode: String): Flow<List<WordProgressEntity>>

    @Query("SELECT * FROM word_progress WHERE wordId = :wordId LIMIT 1")
    suspend fun getProgressForWord(wordId: String): WordProgressEntity?

    @Query("SELECT * FROM word_progress WHERE languageCode = :langCode AND nextReviewAt <= :currentTime")
    fun getWordsDueForReview(langCode: String, currentTime: Long): Flow<List<WordProgressEntity>>

    @Query("SELECT * FROM word_progress WHERE languageCode = :langCode AND isMastered = 1")
    fun getMasteredWords(langCode: String): Flow<List<WordProgressEntity>>

    @Query("SELECT * FROM word_progress WHERE languageCode = :langCode AND errorCount > 0 ORDER BY errorCount DESC")
    fun getWordsWithErrors(langCode: String): Flow<List<WordProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: WordProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(list: List<WordProgressEntity>)

    @Query("DELETE FROM word_progress WHERE wordId = :wordId")
    suspend fun deleteProgress(wordId: String)

    @Query("DELETE FROM word_progress WHERE languageCode = :langCode")
    suspend fun clearProgressForLanguage(langCode: String)

    @Query("SELECT * FROM badges ORDER BY unlockedAt DESC")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockBadge(badge: BadgeEntity)

    @Query("SELECT COUNT(*) FROM badges")
    fun getBadgeCount(): Flow<Int>

    @Query("SELECT * FROM learning_sessions ORDER BY timestamp DESC LIMIT 30")
    fun getRecentSessions(): Flow<List<LearningSessionEntity>>

    @Insert
    suspend fun logSession(session: LearningSessionEntity)

    @Query("SELECT COUNT(*) FROM word_progress WHERE isMastered = 1 AND languageCode = :langCode")
    fun getMasteredCount(langCode: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM word_progress WHERE languageCode = :langCode")
    fun getTotalTrackedWordsCount(langCode: String): Flow<Int>
}
