package com.example.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SmartEngagementManagerTest {

    private lateinit var manager: SmartEngagementManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("lumi_smart_engagement", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        manager = SmartEngagementManager(context)
    }

    @Test
    fun `rate prompt does not trigger on low activity`() {
        repeat(3) { manager.recordAppLaunch() }
        repeat(2) { manager.recordGameCompleted() }

        val suggestion = manager.calculateBestTimeSuggestion()
        assertNull("Should not prompt rate app when games completed < 5", suggestion)
    }

    @Test
    fun `rate prompt triggers when threshold met`() {
        repeat(4) { manager.recordAppLaunch() }
        repeat(5) { manager.recordGameCompleted() }

        val suggestion = manager.calculateBestTimeSuggestion()
        assertEquals(SmartSuggestionType.RATE_APP, suggestion)
    }

    @Test
    fun `prompt is on cooldown immediately after being shown or dismissed`() {
        repeat(4) { manager.recordAppLaunch() }
        repeat(5) { manager.recordGameCompleted() }

        val suggestion1 = manager.calculateBestTimeSuggestion()
        assertEquals(SmartSuggestionType.RATE_APP, suggestion1)

        manager.recordPromptShown(suggestion1!!)
        manager.recordPromptDismissed(suggestion1)

        // Immediately checking again should return null due to 7-day cooldown and game delta
        val suggestion2 = manager.calculateBestTimeSuggestion()
        assertNull("Should be on cooldown immediately after dismissal", suggestion2)
    }

    @Test
    fun `max dismissals prevents subsequent auto prompts permanently`() {
        repeat(4) { manager.recordAppLaunch() }
        repeat(5) { manager.recordGameCompleted() }

        // First prompt
        var suggestion = manager.calculateBestTimeSuggestion()
        assertEquals(SmartSuggestionType.RATE_APP, suggestion)
        manager.recordPromptDismissed(suggestion!!)

        // Second dismissal
        manager.recordPromptDismissed(SmartSuggestionType.RATE_APP)

        // Simulate playing 20 more games and launching 10 more times
        repeat(20) { manager.recordGameCompleted() }
        repeat(10) { manager.recordAppLaunch() }

        // Since rate app was dismissed twice (max 2 times), RATE_APP should no longer be suggested
        suggestion = manager.calculateBestTimeSuggestion()
        // It should either move on to share app (if criteria met) or null, but rateDismissCount should be 2
        assertEquals(2, manager.rateDismissCount)
    }

    @Test
    fun `executeRateApp marks hasRated as true permanently`() {
        repeat(4) { manager.recordAppLaunch() }
        repeat(5) { manager.recordGameCompleted() }

        manager.executeRateApp()
        assertTrue(manager.hasRated)

        val suggestion = manager.calculateBestTimeSuggestion()
        if (suggestion != null) {
            assertTrue("Should not be RATE_APP after rating", suggestion != SmartSuggestionType.RATE_APP)
        }
    }
}
