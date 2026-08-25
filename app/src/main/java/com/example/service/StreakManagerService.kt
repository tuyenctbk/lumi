package com.example.service

import android.content.Context
import com.example.data.db.BadgeEntity
import com.example.data.db.DailyLearningStatsEntity
import com.example.data.db.LumiDao
import com.example.data.db.LumiDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * StreakManagerService
 *
 * Dedicated service tracking consecutive days of lesson completion,
 * calculating active streaks, handling streak freezes, and storing streak metrics in Room DB.
 */
class StreakManagerService(private val dao: LumiDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val allDailyStatsFlow: Flow<List<DailyLearningStatsEntity>> = dao.getAllDailyStats()

    val currentStreakFlow: Flow<Int> = dao.getAllDailyStats().map { statsList ->
        calculateConsecutiveStreak(statsList)
    }

    suspend fun recordLessonCompletion(
        wordsPracticed: Int,
        durationSeconds: Int,
        accuracy: Float
    ): Int = withContext(Dispatchers.IO) {
        val now = Date()
        val todayStr = dateFormat.format(now)
        val dayLabel = SimpleDateFormat("EEE", Locale.getDefault()).format(now)

        val existingStat = dao.getDailyStatForDate(todayStr)
        val newWords = (existingStat?.wordsPracticed ?: 0) + wordsPracticed
        val newMinutes = (existingStat?.minutesPracticed ?: 0) + (durationSeconds / 60).coerceAtLeast(1)
        val newSessions = (existingStat?.sessionsCompleted ?: 0) + 1
        val updatedAccuracy = if (existingStat == null) accuracy else (existingStat.accuracy + accuracy) / 2f

        val updatedStat = DailyLearningStatsEntity(
            dateString = todayStr,
            dayLabel = dayLabel,
            wordsPracticed = newWords,
            correctCount = (newWords * updatedAccuracy).toInt(),
            minutesPracticed = newMinutes,
            sessionsCompleted = newSessions,
            accuracy = updatedAccuracy,
            isGoalMet = newWords >= 3 || newMinutes >= 2,
            timestamp = System.currentTimeMillis()
        )

        dao.saveDailyStat(updatedStat)

        // Evaluate updated streak
        val allStats = dao.getAllDailyStatsSnapshot()
        val streakCount = calculateConsecutiveStreak(allStats)

        // Unlock streak milestone badges in Room DB
        checkAndUnlockStreakBadges(streakCount)

        return@withContext streakCount
    }

    fun calculateConsecutiveStreak(statsList: List<DailyLearningStatsEntity>): Int {
        if (statsList.isEmpty()) return 0

        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(calendar.time)

        val statsMap = statsList.associateBy { it.dateString }

        // Start checking from today or yesterday
        val hasToday = statsMap.containsKey(todayStr) && (statsMap[todayStr]?.wordsPracticed ?: 0) > 0
        val hasYesterday = statsMap.containsKey(yesterdayStr) && (statsMap[yesterdayStr]?.wordsPracticed ?: 0) > 0

        if (!hasToday && !hasYesterday) {
            return 0
        }

        var streak = 0
        val checkCalendar = Calendar.getInstance()
        if (!hasToday) {
            checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dateKey = dateFormat.format(checkCalendar.time)
            val stat = statsMap[dateKey]
            if (stat != null && stat.wordsPracticed > 0) {
                streak++
                checkCalendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        return streak
    }

    private suspend fun checkAndUnlockStreakBadges(streakCount: Int) {
        val now = System.currentTimeMillis()

        if (streakCount >= 1) {
            dao.unlockBadge(
                BadgeEntity(
                    id = "streak_1_day",
                    title = "🚀 First Lesson Streak",
                    description = "Completed your very first daily learning session!",
                    iconEmoji = "🌱",
                    unlockedAt = now
                )
            )
        }

        if (streakCount >= 3) {
            dao.unlockBadge(
                BadgeEntity(
                    id = "streak_3_days",
                    title = "🔥 3-Day Momentum",
                    description = "Maintained a 3-day consecutive learning streak!",
                    iconEmoji = "⚡",
                    unlockedAt = now
                )
            )
        }

        if (streakCount >= 5) {
            dao.unlockBadge(
                BadgeEntity(
                    id = "streak_5_days",
                    title = "⭐ 5-Day High Five",
                    description = "5 consecutive days of daily language practice!",
                    iconEmoji = "✋",
                    unlockedAt = now
                )
            )
        }

        if (streakCount >= 10) {
            dao.unlockBadge(
                BadgeEntity(
                    id = "streak_10_days",
                    title = "🔥 10-Day Streak Master",
                    description = "Unlocked the coveted 10-day streak milestone!",
                    iconEmoji = "🏆",
                    unlockedAt = now
                )
            )
        }
    }

    companion object {
        @Volatile
        private var instance: StreakManagerService? = null

        fun getInstance(context: Context): StreakManagerService {
            return instance ?: synchronized(this) {
                val db = LumiDatabase.getInstance(context)
                val newInstance = StreakManagerService(db.lumiDao())
                instance = newInstance
                newInstance
            }
        }
    }
}
