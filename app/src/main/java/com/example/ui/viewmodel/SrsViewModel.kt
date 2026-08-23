package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.WordProgressEntity
import com.example.data.repository.VocabularyRepository
import com.example.model.LearningCategory
import com.example.model.TargetLanguage
import com.example.model.VocabularyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

/**
 * Data class representing spaced repetition statistics for a vocabulary item.
 */
data class SrsWordStat(
    val item: VocabularyItem,
    val targetWord: String,
    val correctCount: Int,
    val errorCount: Int,
    val totalAttempts: Int,
    val retentionRate: Float, // 0.0 to 1.0 (0% to 100%)
    val isMastered: Boolean,
    val intervalDays: Int,
    val nextReviewAt: Long,
    val isDueForReview: Boolean
)

/**
 * SrsViewModel
 *
 * Spaced Repetition (SRS) ViewModel tracking user retention curves and prioritizing
 * words with lower retention rates or higher error counts in future learning sessions.
 */
class SrsViewModel(
    private val repository: VocabularyRepository,
    private val targetLanguageFlow: StateFlow<TargetLanguage>
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val wordProgressList: StateFlow<List<WordProgressEntity>> = targetLanguageFlow
        .flatMapLatest { lang -> repository.getWordProgressStream(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Complete SRS Stats list for all vocabulary items in the active language,
     * computed with retention rates.
     */
    val srsWordStats: StateFlow<List<SrsWordStat>> = combine(
        wordProgressList,
        targetLanguageFlow
    ) { progressList, lang ->
        val progressMap = progressList.associateBy { it.wordId }
        val now = System.currentTimeMillis()

        repository.allVocabulary.map { item ->
            val prog = progressMap[item.id]
            val correct = prog?.correctCount ?: 0
            val errors = prog?.errorCount ?: 0
            val attempts = correct + errors

            // Retention rate computation:
            // Base formula: correct / attempts. If never practiced, retention is 0.5 (neutral).
            // Apply time decay if review is overdue.
            val rawRate = if (attempts == 0) {
                0.5f
            } else {
                correct.toFloat() / attempts
            }

            // Time decay penalty: if overdue by multiple days, retention decays exponentially
            val daysOverdue = if (prog != null && now > prog.nextReviewAt) {
                ((now - prog.nextReviewAt) / (24L * 3600L * 1000L)).coerceAtLeast(0)
            } else {
                0L
            }
            val decayFactor = exp(-0.08 * daysOverdue).toFloat()
            val finalRetention = if (attempts == 0) 0.5f else (rawRate * decayFactor).coerceIn(0.05f, 1.0f)

            val isDue = prog == null || prog.nextReviewAt <= now || (!prog.isMastered && errors > 0)
            val targetTranslation = item.translations[lang.code] ?: item.englishWord

            SrsWordStat(
                item = item,
                targetWord = targetTranslation,
                correctCount = correct,
                errorCount = errors,
                totalAttempts = attempts,
                retentionRate = finalRetention,
                isMastered = prog?.isMastered ?: false,
                intervalDays = prog?.intervalDays ?: 1,
                nextReviewAt = prog?.nextReviewAt ?: now,
                isDueForReview = isDue
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Words that have low retention (< 65%) or high error counts, prioritized first.
     */
    val lowRetentionWords: StateFlow<List<SrsWordStat>> = srsWordStats.combine(targetLanguageFlow) { stats, _ ->
        stats.filter { it.totalAttempts > 0 && (!it.isMastered || it.retentionRate < 0.70f) }
            .sortedBy { it.retentionRate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Average retention rate across all practiced words (0% - 100%).
     */
    val averageRetentionRate: StateFlow<Int> = srsWordStats.combine(targetLanguageFlow) { stats, _ ->
        val practiced = stats.filter { it.totalAttempts > 0 }
        if (practiced.isEmpty()) {
            100
        } else {
            (practiced.map { it.retentionRate }.average() * 100).toInt().coerceIn(0, 100)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    /**
     * Returns a curated list of vocabulary items prioritizing:
     * 1. Words with lowest retention rates (< 60%)
     * 2. Words due for SRS review
     * 3. Unpracticed new words
     * 4. Mastered words for refreshing
     */
    fun getCuratedSrsSession(
        category: LearningCategory? = null,
        count: Int = 4
    ): List<VocabularyItem> {
        val pool = srsWordStats.value.filter {
            category == null || it.item.category == category
        }

        // Group into priority buckets
        val lowRetention = pool.filter { it.totalAttempts > 0 && it.retentionRate < 0.70f }
            .sortedBy { it.retentionRate }
            .map { it.item }

        val dueForReview = pool.filter { it.isDueForReview && it.item !in lowRetention }
            .sortedBy { it.retentionRate }
            .map { it.item }

        val unpracticed = pool.filter { it.totalAttempts == 0 }
            .shuffled()
            .map { it.item }

        val mastered = pool.filter { it.isMastered && it.item !in lowRetention && it.item !in dueForReview }
            .shuffled()
            .map { it.item }

        val curated = mutableListOf<VocabularyItem>()
        curated.addAll(lowRetention)
        curated.addAll(dueForReview.filter { it !in curated })
        curated.addAll(unpracticed.filter { it !in curated })
        curated.addAll(mastered.filter { it !in curated })

        return curated.take(count)
    }

    /**
     * Records an SRS review attempt.
     */
    fun recordReview(wordId: String, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.recordAnswer(wordId, targetLanguageFlow.value.code, isCorrect)
        }
    }

    class Factory(
        private val repository: VocabularyRepository,
        private val targetLanguageFlow: StateFlow<TargetLanguage>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SrsViewModel(repository, targetLanguageFlow) as T
        }
    }
}
