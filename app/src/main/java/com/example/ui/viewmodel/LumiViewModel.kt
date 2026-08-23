package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundFxHelper
import com.example.audio.SpeechHelper
import com.example.data.api.FreesoundRepository
import com.example.data.api.GiphyRepository
import com.example.data.api.PixabayRepository
import com.example.data.db.BadgeEntity
import com.example.data.db.LearningSessionEntity
import com.example.data.db.WordProgressEntity
import com.example.data.repository.VocabularyRepository
import com.example.model.LearningCategory
import com.example.model.MascotMood
import com.example.model.PhysicalBreakCatalog
import com.example.model.PhysicalBreakQuest
import com.example.model.TargetLanguage
import com.example.model.VocabularyItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LumiViewModel(
    private val repository: VocabularyRepository,
    private val speechHelper: SpeechHelper,
    val freesoundRepository: FreesoundRepository = FreesoundRepository(),
    val giphyRepository: GiphyRepository = GiphyRepository(),
    val pixabayRepository: PixabayRepository = PixabayRepository()
) : ViewModel() {

    private val _targetLanguage = MutableStateFlow(TargetLanguage.SPANISH)
    val targetLanguage: StateFlow<TargetLanguage> = _targetLanguage.asStateFlow()

    private val _mascotMood = MutableStateFlow(MascotMood.IDLE)
    val mascotMood: StateFlow<MascotMood> = _mascotMood.asStateFlow()

    private val _mascotSpeechBubble = MutableStateFlow<String?>(null)
    val mascotSpeechBubble: StateFlow<String?> = _mascotSpeechBubble.asStateFlow()

    private val _bilingualMode = MutableStateFlow(false)
    val bilingualMode: StateFlow<Boolean> = _bilingualMode.asStateFlow()

    val ttsOfflineMode: StateFlow<Boolean> = speechHelper.isOfflineMode

    private val _activeCategory = MutableStateFlow<LearningCategory?>(null)
    val activeCategory: StateFlow<LearningCategory?> = _activeCategory.asStateFlow()

    private val _points = MutableStateFlow(40)
    val points: StateFlow<Int> = _points.asStateFlow()

    private val _streakDays = MutableStateFlow(3)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = speechHelper.isSpeaking

    val badges: StateFlow<List<BadgeEntity>> = repository.getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<LearningSessionEntity>> = repository.getRecentSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val wordProgressList: StateFlow<List<WordProgressEntity>> = _targetLanguage
        .flatMapLatest { lang -> repository.getWordProgressStream(lang.code) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SRS: List of missed words that need re-practicing
    val missedWords: StateFlow<List<VocabularyItem>> = combine(
        wordProgressList,
        _targetLanguage
    ) { progressList, _ ->
        val progressMap = progressList.associateBy { it.wordId }
        repository.allVocabulary
            .filter { item ->
                val p = progressMap[item.id]
                p != null && p.errorCount > 0 && !p.isMastered
            }
            .sortedByDescending { item -> progressMap[item.id]?.errorCount ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SRS: List of mastered words
    val masteredWords: StateFlow<List<VocabularyItem>> = combine(
        wordProgressList,
        _targetLanguage
    ) { progressList, _ ->
        val masteredIds = progressList.filter { it.isMastered }.map { it.wordId }.toSet()
        repository.allVocabulary.filter { it.id in masteredIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==========================================
    // Physical Break & Sedentary Reduction State
    // ==========================================
    private val _activePhysicalBreak = MutableStateFlow<PhysicalBreakQuest?>(null)
    val activePhysicalBreak: StateFlow<PhysicalBreakQuest?> = _activePhysicalBreak.asStateFlow()

    private val _isPhysicalBreakVisible = MutableStateFlow(false)
    val isPhysicalBreakVisible: StateFlow<Boolean> = _isPhysicalBreakVisible.asStateFlow()

    private val _physicalBreaksCompleted = MutableStateFlow(1)
    val physicalBreaksCompleted: StateFlow<Int> = _physicalBreaksCompleted.asStateFlow()

    private var periodicBreakJob: Job? = null
    private var questionsAnsweredCounter = 0

    init {
        // Welcome voice greeting on launch
        viewModelScope.launch {
            delay(600)
            speakLumi("Hi! I'm Lumi! Let's explore the world map together!")
        }

        // Start periodic physical break timer (checks every 2.5 minutes of active session)
        startPeriodicPhysicalBreakTimer()
    }

    private fun startPeriodicPhysicalBreakTimer() {
        periodicBreakJob?.cancel()
        periodicBreakJob = viewModelScope.launch {
            while (true) {
                delay(150_000L) // 2.5 minutes interval
                if (!_isPhysicalBreakVisible.value) {
                    triggerPhysicalActivitySuggestion()
                }
            }
        }
    }

    fun triggerPhysicalActivitySuggestion(quest: PhysicalBreakQuest? = null) {
        val nextQuest = quest ?: PhysicalBreakCatalog.getRandomQuest(_activePhysicalBreak.value?.id)
        _activePhysicalBreak.value = nextQuest
        _isPhysicalBreakVisible.value = true

        // Play alert & speak prompt
        SoundFxHelper.playPop()
        setMascotMood(MascotMood.HAPPY, 4000)
        _mascotSpeechBubble.value = nextQuest.emoji
        speechHelper.speakLumi(nextQuest.spokenPrompt)
    }

    fun completePhysicalActivity(quest: PhysicalBreakQuest) {
        _isPhysicalBreakVisible.value = false
        _physicalBreaksCompleted.value += 1
        _points.value += quest.rewardPoints
        SoundFxHelper.playCelebrationFanfare()
        setMascotMood(MascotMood.SUPERSTAR, 3500)
        _mascotSpeechBubble.value = "🌟"
        speechHelper.speakLumi("Fantastic moving! You earned ${quest.rewardPoints} bonus stars!")

        viewModelScope.launch {
            repository.logSession(
                gameType = "physical_break",
                wordsPracticed = 1,
                accuracy = 1.0f,
                durationSeconds = quest.durationSeconds
            )
        }
    }

    fun dismissPhysicalActivity() {
        _isPhysicalBreakVisible.value = false
        _mascotSpeechBubble.value = null
    }

    fun setLanguage(language: TargetLanguage) {
        _targetLanguage.value = language
        SoundFxHelper.playPop()
        speakLumi("We are learning ${language.displayName} now! Let's go!")
    }

    fun setActiveCategory(category: LearningCategory?) {
        _activeCategory.value = category
    }

    fun toggleBilingualMode() {
        _bilingualMode.value = !_bilingualMode.value
        SoundFxHelper.playPop()
    }

    fun toggleTtsOfflineMode() {
        val next = !speechHelper.isOfflineMode.value
        speechHelper.setOfflineMode(next)
        SoundFxHelper.playPop()
        if (next) {
            speakLumi("Offline voice mode activated! Lumi can speak with local phonetics!")
        } else {
            speakLumi("Online network voice mode enabled!")
        }
    }

    fun setTtsOfflineMode(enabled: Boolean) {
        speechHelper.setOfflineMode(enabled)
    }

    fun setMascotMood(mood: MascotMood, durationMs: Long = 3000) {
        _mascotMood.value = mood
        if (mood != MascotMood.IDLE) {
            viewModelScope.launch {
                delay(durationMs)
                if (_mascotMood.value == mood) {
                    _mascotMood.value = MascotMood.IDLE
                }
            }
        }
    }

    fun speakWord(item: VocabularyItem) {
        val targetWord = item.translations[_targetLanguage.value.code] ?: item.englishWord
        setMascotMood(MascotMood.TALKING, 2000)
        _mascotSpeechBubble.value = item.emoji
        speechHelper.speakWord(targetWord, _targetLanguage.value)
        viewModelScope.launch {
            delay(2500)
            if (_mascotSpeechBubble.value == item.emoji) {
                _mascotSpeechBubble.value = null
            }
        }
    }

    fun speakLumi(text: String, mood: MascotMood = MascotMood.HAPPY) {
        setMascotMood(mood, 3000)
        _mascotSpeechBubble.value = "✨"
        speechHelper.speakLumi(text)
        viewModelScope.launch {
            delay(3000)
            if (_mascotSpeechBubble.value == "✨") {
                _mascotSpeechBubble.value = null
            }
        }
    }

    /**
     * Plays high-pitched, positive audio feedback when answering correctly
     */
    fun onAnswerGiven(wordId: String, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.recordAnswer(wordId, _targetLanguage.value.code, isCorrect)
            val masteredCount = wordProgressList.value.count { it.isMastered }
            repository.evaluateAndUnlockAchievements(
                masteredCount = masteredCount,
                streakDays = streakDays.value,
                physicalBreaks = _physicalBreaksCompleted.value,
                totalSessions = recentSessions.value.size
            )
            if (isCorrect) {
                _points.value += 10
                SoundFxHelper.playCorrectChime()
                setMascotMood(MascotMood.HAPPY, 2500)
            } else {
                SoundFxHelper.playWrongOops()
                setMascotMood(MascotMood.ENCOURAGING, 2500)
            }

            // Every 5 answers, suggest a physical active break to reduce TV sedentary time
            questionsAnsweredCounter++
            if (questionsAnsweredCounter % 5 == 0 && !_isPhysicalBreakVisible.value) {
                delay(2000)
                triggerPhysicalActivitySuggestion()
            }
        }
    }

    /**
     * Plays triumphant fanfare and awards stars when completing a lesson
     */
    fun onSessionCompleted(gameType: String, practicedCount: Int, correctCount: Int, durationSeconds: Int) {
        val accuracy = if (practicedCount > 0) correctCount.toFloat() / practicedCount else 1.0f
        viewModelScope.launch {
            repository.logSession(gameType, practicedCount, accuracy, durationSeconds)
            _points.value += correctCount * 15
            val masteredCount = wordProgressList.value.count { it.isMastered }
            repository.evaluateAndUnlockAchievements(
                masteredCount = masteredCount,
                streakDays = streakDays.value,
                physicalBreaks = _physicalBreaksCompleted.value,
                totalSessions = recentSessions.value.size
            )
            SoundFxHelper.playLessonCompleteFanfare()
            setMascotMood(MascotMood.SUPERSTAR, 4000)
            speakLumi("Hooray! You completed the lesson! Look at all your stars!", MascotMood.SUPERSTAR)
        }
    }

    /**
     * Plays real-world sound effects fetched from Freesound API
     */
    fun playRealSoundEffect(wordId: String) {
        viewModelScope.launch {
            SoundFxHelper.playPop()
            val soundUrl = freesoundRepository.getSoundForWord(wordId)
            if (!soundUrl.isNullOrBlank()) {
                SoundFxHelper.playSoundUrl(soundUrl)
            } else {
                SoundFxHelper.playStarBurst()
            }
        }
    }

    /**
     * SRS Prioritized Word Retrieval:
     * Prioritizes missed / incorrect words first in upcoming learning sessions!
     */
    fun getPrioritizedWordsForSession(
        category: LearningCategory? = null,
        count: Int = 4
    ): List<VocabularyItem> {
        val pool = if (category != null) {
            repository.getWordsByCategory(category)
        } else {
            repository.allVocabulary
        }

        val progressMap = wordProgressList.value.associateBy { it.wordId }
        val now = System.currentTimeMillis()

        val missed = mutableListOf<VocabularyItem>()
        val due = mutableListOf<VocabularyItem>()
        val unpracticed = mutableListOf<VocabularyItem>()
        val mastered = mutableListOf<VocabularyItem>()

        for (item in pool) {
            val prog = progressMap[item.id]
            when {
                prog == null -> unpracticed.add(item)
                prog.isMastered -> mastered.add(item)
                prog.errorCount > 0 -> missed.add(item)
                prog.nextReviewAt <= now -> due.add(item)
                else -> unpracticed.add(item)
            }
        }

        // Sort missed words by error count descending (words child struggled with most come first)
        missed.sortByDescending { progressMap[it.id]?.errorCount ?: 0 }

        val result = mutableListOf<VocabularyItem>()
        result.addAll(missed)
        result.addAll(due.filter { it !in result })
        result.addAll(unpracticed.shuffled().filter { it !in result })
        result.addAll(mastered.shuffled().filter { it !in result })

        return result.take(count)
    }

    fun getWordsForCategory(category: LearningCategory): List<VocabularyItem> {
        return repository.getWordsByCategory(category)
    }

    fun getAllWords(): List<VocabularyItem> = repository.allVocabulary

    override fun onCleared() {
        super.onCleared()
        periodicBreakJob?.cancel()
    }

    class Factory(
        private val repository: VocabularyRepository,
        private val speechHelper: SpeechHelper,
        private val freesoundRepository: FreesoundRepository = FreesoundRepository(),
        private val giphyRepository: GiphyRepository = GiphyRepository(),
        private val pixabayRepository: PixabayRepository = PixabayRepository()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LumiViewModel(
                repository,
                speechHelper,
                freesoundRepository,
                giphyRepository,
                pixabayRepository
            ) as T
        }
    }
}
