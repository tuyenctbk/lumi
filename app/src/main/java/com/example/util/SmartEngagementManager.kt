package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

enum class SmartSuggestionType {
    SHARE_APP,
    RATE_APP,
    UPDATE_APP
}

/**
 * SmartEngagementManager
 *
 * Manages onboarding status, engagement metrics (launch count, completed games),
 * and calculates optimal times to trigger auto popups for Share App, Rate App, and Update App.
 */
class SmartEngagementManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("lumi_smart_engagement", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LAUNCH_COUNT = "app_launch_count"
        private const val KEY_COMPLETED_GAMES_COUNT = "completed_games_count"
        private const val KEY_HAS_RATED = "has_rated_app"
        private const val KEY_HAS_SHARED = "has_shared_app"
        private const val KEY_LAST_PROMPT_TIME = "last_prompt_time"
        private const val KEY_LAST_DISMISSED_TIME = "last_dismissed_time"
        private const val KEY_LAST_PROMPT_GAMES_COUNT = "last_prompt_games_count"
        private const val KEY_LAST_PROMPT_LAUNCH_COUNT = "last_prompt_launch_count"
        private const val KEY_RATE_DISMISS_COUNT = "rate_dismiss_count"
        private const val KEY_SHARE_DISMISS_COUNT = "share_dismiss_count"
        private const val KEY_SIMULATED_UPDATE_AVAILABLE = "simulated_update_available"

        // Minimum time interval between any auto popups (7 days = 604,800,000 ms to avoid user disruption)
        private const val MIN_PROMPT_INTERVAL_MS = 7 * 24 * 60 * 60 * 1000L
        private const val MIN_GAMES_DELTA_BETWEEN_PROMPTS = 5
        private const val MIN_LAUNCHES_DELTA_BETWEEN_PROMPTS = 3
        private const val MAX_DISMISS_COUNT = 2
    }

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var launchCount: Int
        get() = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        private set(value) = prefs.edit().putInt(KEY_LAUNCH_COUNT, value).apply()

    var completedGamesCount: Int
        get() = prefs.getInt(KEY_COMPLETED_GAMES_COUNT, 0)
        private set(value) = prefs.edit().putInt(KEY_COMPLETED_GAMES_COUNT, value).apply()

    var hasRated: Boolean
        get() = prefs.getBoolean(KEY_HAS_RATED, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_RATED, value).apply()

    var hasShared: Boolean
        get() = prefs.getBoolean(KEY_HAS_SHARED, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_SHARED, value).apply()

    var rateDismissCount: Int
        get() = prefs.getInt(KEY_RATE_DISMISS_COUNT, 0)
        private set(value) = prefs.edit().putInt(KEY_RATE_DISMISS_COUNT, value).apply()

    var shareDismissCount: Int
        get() = prefs.getInt(KEY_SHARE_DISMISS_COUNT, 0)
        private set(value) = prefs.edit().putInt(KEY_SHARE_DISMISS_COUNT, value).apply()

    fun recordAppLaunch() {
        launchCount += 1
    }

    fun recordGameCompleted() {
        completedGamesCount += 1
    }

    fun setUpdateAvailable(isAvailable: Boolean) {
        prefs.edit().putBoolean(KEY_SIMULATED_UPDATE_AVAILABLE, isAvailable).apply()
    }

    fun recordPromptShown(suggestionType: SmartSuggestionType) {
        prefs.edit()
            .putLong(KEY_LAST_PROMPT_TIME, System.currentTimeMillis())
            .putInt(KEY_LAST_PROMPT_GAMES_COUNT, completedGamesCount)
            .putInt(KEY_LAST_PROMPT_LAUNCH_COUNT, launchCount)
            .apply()
    }

    fun recordPromptDismissed(suggestionType: SmartSuggestionType) {
        val now = System.currentTimeMillis()
        val editor = prefs.edit()
            .putLong(KEY_LAST_DISMISSED_TIME, now)
            .putLong(KEY_LAST_PROMPT_TIME, now)
            .putInt(KEY_LAST_PROMPT_GAMES_COUNT, completedGamesCount)
            .putInt(KEY_LAST_PROMPT_LAUNCH_COUNT, launchCount)

        if (suggestionType == SmartSuggestionType.RATE_APP) {
            editor.putInt(KEY_RATE_DISMISS_COUNT, rateDismissCount + 1)
        } else if (suggestionType == SmartSuggestionType.SHARE_APP) {
            editor.putInt(KEY_SHARE_DISMISS_COUNT, shareDismissCount + 1)
        }
        editor.apply()
    }

    fun recordPromptActionTaken() {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putLong(KEY_LAST_PROMPT_TIME, now)
            .putInt(KEY_LAST_PROMPT_GAMES_COUNT, completedGamesCount)
            .putInt(KEY_LAST_PROMPT_LAUNCH_COUNT, launchCount)
            .apply()
    }

    /**
     * Calculates if now is the "best time" to automatically prompt the user with
     * a Rate App, Share App, or Update App suggestion.
     */
    fun calculateBestTimeSuggestion(): SmartSuggestionType? {
        val now = System.currentTimeMillis()
        val lastPrompt = prefs.getLong(KEY_LAST_PROMPT_TIME, 0L)
        val lastDismissed = prefs.getLong(KEY_LAST_DISMISSED_TIME, 0L)

        // 1. Ensure we don't spam the user (must pass min 7 days interval since last prompt/dismiss)
        if (now - lastPrompt < MIN_PROMPT_INTERVAL_MS || now - lastDismissed < MIN_PROMPT_INTERVAL_MS) {
            return null
        }

        // 2. Require meaningful new activity since last prompt
        if (lastPrompt > 0L) {
            val lastPromptGames = prefs.getInt(KEY_LAST_PROMPT_GAMES_COUNT, 0)
            val lastPromptLaunches = prefs.getInt(KEY_LAST_PROMPT_LAUNCH_COUNT, 0)
            val gamesDelta = completedGamesCount - lastPromptGames
            val launchesDelta = launchCount - lastPromptLaunches

            if (gamesDelta < MIN_GAMES_DELTA_BETWEEN_PROMPTS || launchesDelta < MIN_LAUNCHES_DELTA_BETWEEN_PROMPTS) {
                return null
            }
        }

        val updateAvailable = prefs.getBoolean(KEY_SIMULATED_UPDATE_AVAILABLE, false)

        // On Android TV leanback devices, suppress mobile sharing / rate prompts and only show updates if available
        val isTv = context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK) ||
                   context.packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_TELEVISION) ||
                   (context.resources.configuration.uiMode.and(android.content.res.Configuration.UI_MODE_TYPE_MASK)) == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION

        if (isTv) {
            return if (updateAvailable) SmartSuggestionType.UPDATE_APP else null
        }

        // 3. Check Update App Suggestion: Only when an actual update is flagged as available
        if (updateAvailable) {
            return SmartSuggestionType.UPDATE_APP
        }

        // 4. Check Rate App Suggestion: Requires high engagement, not previously rated, and < max dismissals (max 2)
        if (!hasRated && rateDismissCount < MAX_DISMISS_COUNT && completedGamesCount >= 5 && launchCount >= 4) {
            return SmartSuggestionType.RATE_APP
        }

        // 5. Check Share App Suggestion: Requires even higher engagement, not previously shared, and < max dismissals (max 2)
        if (!hasShared && shareDismissCount < MAX_DISMISS_COUNT && completedGamesCount >= 8 && launchCount >= 6) {
            return SmartSuggestionType.SHARE_APP
        }

        return null
    }

    // =============================
    // Native Android Intent Actions
    // =============================

    fun executeShareApp() {
        hasShared = true
        recordPromptActionTaken()

        val appPackageName = context.packageName
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "Lumi - Fun Learning App for Kids"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "🌟 Discover Lumi! A fun, interactive multilingual companion for kids and families. Learn words, play games, and explore together!\n\nDownload on Google Play:\nhttps://play.google.com/store/apps/details?id=$appPackageName"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Lumi with friends").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun executeRateApp() {
        hasRated = true
        recordPromptActionTaken()

        val appPackageName = context.packageName
        val marketUri = Uri.parse("market://details?id=$appPackageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun executeUpdateApp() {
        setUpdateAvailable(false)
        recordPromptActionTaken()

        val appPackageName = context.packageName
        val marketUri = Uri.parse("market://details?id=$appPackageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
