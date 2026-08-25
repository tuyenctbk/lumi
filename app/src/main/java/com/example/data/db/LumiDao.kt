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

    @Query("SELECT * FROM badges")
    suspend fun getAllBadgesSnapshot(): List<BadgeEntity>

    @Query("SELECT * FROM badges WHERE id = :id LIMIT 1")
    suspend fun getBadgeById(id: String): BadgeEntity?

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

    // Daily Learning Stats
    @Query("SELECT * FROM daily_learning_stats ORDER BY dateString DESC LIMIT 7")
    fun getRecent7DaysStats(): Flow<List<DailyLearningStatsEntity>>

    @Query("SELECT * FROM daily_learning_stats ORDER BY dateString DESC")
    fun getAllDailyStats(): Flow<List<DailyLearningStatsEntity>>

    @Query("SELECT * FROM daily_learning_stats ORDER BY dateString DESC")
    suspend fun getAllDailyStatsSnapshot(): List<DailyLearningStatsEntity>

    @Query("SELECT * FROM daily_learning_stats WHERE dateString = :dateString LIMIT 1")
    suspend fun getDailyStatForDate(dateString: String): DailyLearningStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDailyStat(stat: DailyLearningStatsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStatsList(list: List<DailyLearningStatsEntity>)

    // User Preferences Queries
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getUserPreferencesFlow(): Flow<UserPreferencesEntity?>

    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    suspend fun getUserPreferencesSnapshot(): UserPreferencesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPreferences(preferences: UserPreferencesEntity)

    // Lesson Queries & Operations
    @Query("SELECT * FROM lessons WHERE languageCode = :langCode ORDER BY lastAccessedAt DESC")
    fun getAllLessons(langCode: String): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: String): Lesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Update
    suspend fun updateLesson(lesson: Lesson)

    @Query("SELECT COUNT(*) FROM lessons WHERE languageCode = :langCode AND isCompleted = 1")
    fun getCompletedLessonsCount(langCode: String): Flow<Int>

    // Progress Queries & Operations
    @Query("SELECT * FROM progress WHERE languageCode = :langCode ORDER BY completedAt DESC")
    fun getAllProgressHistory(langCode: String): Flow<List<Progress>>

    @Query("SELECT * FROM progress WHERE lessonId = :lessonId ORDER BY completedAt DESC")
    fun getProgressForLesson(lessonId: String): Flow<List<Progress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordProgress(progress: Progress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordProgressList(list: List<Progress>)
}
