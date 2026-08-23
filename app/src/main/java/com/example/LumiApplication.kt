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
        soundManager = SoundManager.getInstance(this)
        database = LumiDatabase.getInstance(this)
        repository = VocabularyRepository(database.lumiDao())
        speechHelper = SpeechHelper(this)
        freesoundRepository = FreesoundRepository()
        giphyRepository = GiphyRepository()
        pixabayRepository = PixabayRepository()

        try {
            com.example.service.LumiQuestSchedulerService.createNotificationChannel(this)
            com.example.service.LumiQuestSchedulerService.scheduleNextQuest(this)
        } catch (_: Exception) {}
    }

    override fun onTerminate() {
        super.onTerminate()
        speechHelper.shutdown()
        soundManager.stopActiveSound()
    }
}
