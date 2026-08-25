package com.example.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.LumiApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * BroadcastReceiver for daily language lesson streak reminder alarms.
 */
class LumiDailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as? LumiApplication
        val repository = app?.repository

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val todayStat = repository?.getDailyStat(todayKey)
                val allStats = repository?.getAllDailyStatsSnapshot() ?: emptyList()
                val currentStreak = repository?.calculateConsecutiveStreak(allStats)?.coerceAtLeast(1) ?: 1

                // If user hasn't practiced at least 1 lesson/word today, notify them
                val practicedToday = (todayStat?.wordsPracticed ?: 0) > 0
                if (!practicedToday) {
                    LumiDailyReminderScheduler.showDailyStreakReminderNotification(
                        context = context,
                        currentStreak = currentStreak,
                        targetLanguageName = "Spanish"
                    )
                }

                // Reschedule for next day
                LumiDailyReminderScheduler.scheduleDailyReminder(context)
            } catch (e: Exception) {
                // Fallback direct notification
                LumiDailyReminderScheduler.showDailyStreakReminderNotification(
                    context = context,
                    currentStreak = 1,
                    targetLanguageName = "Spanish"
                )
            }
        }
    }
}

/**
 * LumiDailyReminderScheduler
 *
 * System for scheduling and delivering daily language lesson streak reminders.
 */
object LumiDailyReminderScheduler {

    const val CHANNEL_ID = "lumi_daily_streak_channel"
    const val CHANNEL_NAME = "Daily Language Streak Reminders"
    private const val NOTIFICATION_ID = 5050
    private const val REQUEST_CODE = 9002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds learners to complete their daily 3-minute language adventure to maintain streaks"
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedules an exact daily alarm at the given hour and minute (default: 18:00 / 6:00 PM).
     */
    fun scheduleDailyReminder(context: Context, hourOfDay: Int = 18, minute: Int = 0) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, LumiDailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    /**
     * Shows a rich streak reminder notification prompting the user to complete their daily lesson.
     */
    fun showDailyStreakReminderNotification(
        context: Context,
        currentStreak: Int,
        targetLanguageName: String
    ) {
        createNotificationChannel(context)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "daily_lesson")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val title = "🔥 Keep your $currentStreak-day streak alive!"
        val content = "Lumi is waiting with today's 3-minute $targetLanguageName adventure! Tap to play & earn bonus stars ⭐"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_big_on)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$content\n\n🎯 Goal: 5 words today for full streak reward!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Cancels any scheduled daily reminders.
     */
    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LumiDailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
