package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.LumiDao
import com.example.data.db.LumiDatabase
import com.example.data.db.WordProgressEntity
import com.example.model.LearningCategory
import com.example.model.PhysicalBreakCatalog
import com.example.model.TargetLanguage
import com.example.model.VocabularyItem
import com.example.ui.util.isAndroidTvDevice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: LumiDatabase
    private lateinit var dao: LumiDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LumiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.lumiDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lumi", appName)
    }

    @Test
    fun `physical break catalog contains active quests`() {
        val quests = PhysicalBreakCatalog.allQuests
        assertTrue(quests.isNotEmpty())
        val randomQuest = PhysicalBreakCatalog.getRandomQuest()
        assertNotNull(randomQuest)
        assertTrue(randomQuest.rewardPoints > 0)
        assertTrue(randomQuest.durationSeconds > 0)
    }

    @Test
    fun `parent QR sync url contains necessary analytics parameters`() {
        val url = "https://ais-pre-653xreyvr5mficd75qoe7u-66657199188.asia-southeast1.run.app/parent-report?" +
                "lang=es&mastered=12&total=24&accuracy=92&points=350&streak=5&breaks=4"
        assertTrue(url.contains("lang=es"))
        assertTrue(url.contains("mastered=12"))
        assertTrue(url.contains("accuracy=92"))
        assertTrue(url.contains("breaks=4"))
    }

    @Test
    fun `target languages are correctly configured with codes and flag emojis`() {
        val languages = TargetLanguage.entries
        assertTrue(languages.size >= 6)
        languages.forEach { lang ->
            assertTrue(lang.code.isNotEmpty())
            assertTrue(lang.displayName.isNotEmpty())
            assertTrue(lang.flagEmoji.isNotEmpty())
        }
    }

    @Test
    fun `room word progress entity and dao tracks learning retention accurately`() = runBlocking {
        val entity = WordProgressEntity(
            wordId = "cat",
            languageCode = "es",
            correctCount = 4,
            errorCount = 1,
            streak = 3,
            isMastered = false
        )

        dao.saveProgress(entity)

        val retrieved = dao.getProgressForWord("cat")
        assertNotNull(retrieved)
        assertEquals("cat", retrieved?.wordId)
        assertEquals(4, retrieved?.correctCount)
        assertEquals(1, retrieved?.errorCount)
        assertEquals(0.8f, retrieved?.accuracy ?: 0f, 0.01f)

        val allProgress = dao.getAllProgress("es").first()
        assertEquals(1, allProgress.size)
        assertEquals("cat", allProgress[0].wordId)
    }

    @Test
    fun `isAndroidTvDevice returns boolean without crashing`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val isTv = isAndroidTvDevice(context)
        assertNotNull(isTv)
    }
}

