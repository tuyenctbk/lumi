package com.example

import android.app.Application
import com.example.audio.SoundFxHelper
import com.example.audio.SoundManager
import com.example.audio.SpeechHelper
import com.example.data.api.FreesoundRepository
import com.example.data.api.GiphyRepository
import com.example.data.api.PixabayRepository
import com.example.data.db.LumiDatabase
import com.example.data.repository.VocabularyRepository
import kotlinx.coroutines.launch

class LumiApplication : Application() {
    lateinit var database: LumiDatabase
        private set

    lateinit var repository: VocabularyRepository
        private set

    lateinit var speechHelper: SpeechHelper
        private set

    lateinit var soundManager: SoundManager
        private set

    lateinit var freesoundRepository: FreesoundRepository
        private set

    lateinit var giphyRepository: GiphyRepository
        private set

    lateinit var pixabayRepository: PixabayRepository
        private set

    override fun onCreate() {
        super.onCreate()
        SoundFxHelper.initialize(this)
        com.example.util.HapticFeedbackHelper.initialize(this)
        soundManager = SoundManager.getInstance(this)
        database = LumiDatabase.getInstance(this)
        repository = VocabularyRepository(database.lumiDao(), database.vocabularyDao())
        speechHelper = SpeechHelper(this)
        freesoundRepository = FreesoundRepository()
        giphyRepository = GiphyRepository()
        pixabayRepository = PixabayRepository()

        // Asynchronously seed initial vocabulary into Room DB
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                repository.seedInitialDataIfNeeded()
            } catch (_: Exception) {}
        }

        try {
            com.example.service.LumiQuestSchedulerService.createNotificationChannel(this)
            com.example.service.LumiQuestSchedulerService.scheduleNextQuest(this)
            com.example.service.LumiDailyReminderScheduler.createNotificationChannel(this)
            com.example.service.LumiDailyReminderScheduler.scheduleDailyReminder(this, 18, 0)
        } catch (_: Exception) {}
    }

    override fun onTerminate() {
        super.onTerminate()
        speechHelper.shutdown()
        soundManager.stopActiveSound()
    }
}
