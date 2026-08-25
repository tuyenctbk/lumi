package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.LumiApplication
import com.example.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * DailyReminderWorker
 *
 * WorkManager task executing daily push notification checks to encourage consistent language practice.
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? LumiApplication
        val repository = app?.repository

        return try {
            val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayStat = repository?.getDailyStat(todayKey)
            val allStats = repository?.getAllDailyStatsSnapshot() ?: emptyList()
            val streakDays = repository?.calculateConsecutiveStreak(allStats) ?: 1

            val practicedToday = (todayStat?.wordsPracticed ?: 0) > 0

            // If user hasn't completed a lesson today, trigger the daily push notification
            if (!practicedToday) {
                showNotification(applicationContext, streakDays)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(context: Context, streakDays: Int) {
        val channelId = "lumi_daily_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Lesson Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily reminders to keep your streak glowing with Lumi"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔥 Keep your $streakDays-Day streak glowing!")
            .setContentText("Lumi is waiting for your 3-minute language adventure today! Tap to start.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(2002, notification)
    }

    companion object {
        const val WORK_NAME = "LumiDailyLanguageReminderWork"

        fun scheduleDailyReminder(context: Context) {
            val workManager = WorkManager.getInstance(context.applicationContext)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val reminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                24, TimeUnit.HOURS,
                1, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                reminderRequest
            )
        }
    }
}
