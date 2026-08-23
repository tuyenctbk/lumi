package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * VocabularyItemEntity
 *
 * Room Database entity to store learned words, progress, retention metrics,
 * and spaced repetition (SRS) decay curves.
 */
@Entity(tableName = "vocabulary_items")
data class VocabularyItemEntity(
    @PrimaryKey val id: String,
    val englishWord: String,
    val categoryId: String,
    val emoji: String,
    val phonetic: String = "",
    val soundPrompt: String = "",
    val translation: String = "",
    val languageCode: String = "es",
    val imageUrl: String = "",
    val colorHex: Long = 0xFF4CAF50,
    
    // Retention & Progress Metrics
    val correctCount: Int = 0,
    val errorCount: Int = 0,
    val streak: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Float = 2.5f,
    val lastReviewedAt: Long = System.currentTimeMillis(),
    val nextReviewAt: Long = System.currentTimeMillis(),
    val isMastered: Boolean = false,
    val isFavorite: Boolean = false
) {
    val totalAttempts: Int
        get() = correctCount + errorCount

    val accuracyRate: Float
        get() = if (totalAttempts == 0) 1.0f else correctCount.toFloat() / totalAttempts
}

/**
 * VocabularyDao
 *
 * Data Access Object for querying, updating, and analyzing vocabulary progress
 * and spaced repetition scheduling.
 */
@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :languageCode ORDER BY englishWord ASC")
    fun getAllVocabulary(languageCode: String): Flow<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE id = :id LIMIT 1")
    suspend fun getVocabularyById(id: String): VocabularyItemEntity?

    @Query("SELECT * FROM vocabulary_items WHERE categoryId = :categoryId AND languageCode = :languageCode")
    fun getVocabularyByCategory(categoryId: String, languageCode: String): Flow<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :languageCode AND nextReviewAt <= :currentTime ORDER BY nextReviewAt ASC")
    fun getDueVocabulary(languageCode: String, currentTime: Long): Flow<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :languageCode AND isMastered = 1")
    fun getMasteredVocabulary(languageCode: String): Flow<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE languageCode = :languageCode AND errorCount > 0 ORDER BY errorCount DESC")
    fun getChallengingVocabulary(languageCode: String): Flow<List<VocabularyItemEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary_items WHERE languageCode = :languageCode AND isMastered = 1")
    fun getMasteredCount(languageCode: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_items WHERE languageCode = :languageCode")
    fun getTotalCount(languageCode: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(item: VocabularyItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabularyList(items: List<VocabularyItemEntity>)

    @Update
    suspend fun updateVocabulary(item: VocabularyItemEntity)

    @Delete
    suspend fun deleteVocabulary(item: VocabularyItemEntity)

    @Query("DELETE FROM vocabulary_items WHERE languageCode = :languageCode")
    suspend fun clearVocabularyForLanguage(languageCode: String)
}
