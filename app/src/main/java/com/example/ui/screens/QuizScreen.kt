package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MascotMood
import com.example.model.VocabularyItem
import com.example.ui.components.FocusableCard
import com.example.ui.components.LumiLottieReaction
import com.example.ui.theme.SleekBackground
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.SleekSurfaceBorder
import com.example.ui.theme.SleekTextDark
import com.example.ui.theme.SleekTextMuted
import com.example.ui.viewmodel.LumiViewModel

enum class QuizDifficulty(val title: String, val timeLimit: Int, val distractorCount: Int) {
    EASY("Easy", 30, 2),
    MEDIUM("Medium", 15, 3),
    HARD("Hard", 10, 4)
}

/**
 * QuizQuestion Model
 */
data class QuizQuestion(
    val targetItem: VocabularyItem,
    val targetTranslation: String,
    val options: List<String>,
    val correctAnswer: String
)

/**
 * QuizScreen
 *
 * Multiple-choice vocabulary game fetching terms from the Room database / repository.
 * Tracks user score, streak combos, and records answer metrics in Room DB.
 */
@Composable
fun QuizScreen(
    viewModel: LumiViewModel,
    onBack: () -> Unit,
    onFinishQuiz: (score: Int, total: Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val soundManager = remember { com.example.audio.SoundManager.getInstance(context) }
    
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val wordProgressList by viewModel.wordProgressList.collectAsState()

    var selectedDifficulty by remember { mutableStateOf<QuizDifficulty>(QuizDifficulty.EASY) }

    // Generate 10 multiple-choice questions for longer, deeper lesson sessions
    val questions: List<QuizQuestion> = remember(wordProgressList, targetLanguage, selectedDifficulty) {
        val allItems: List<VocabularyItem> = viewModel.currentCategoryWords
        val pool = allItems.shuffled().take(10)

        pool.map { targetItem ->
            val targetTrans = targetItem.translations[targetLanguage.code] ?: targetItem.englishWord
            val distractorCount = selectedDifficulty?.distractorCount ?: 3
            val wrongDistractors = (allItems - targetItem).shuffled().take(distractorCount).map {
                it.translations[targetLanguage.code] ?: it.englishWord
            }
            val optionsList = (wrongDistractors + targetTrans).shuffled()

            QuizQuestion(
                targetItem = targetItem,
                targetTranslation = targetTrans,
                options = optionsList,
                correctAnswer = targetTrans
            )
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questions.getOrNull(currentIndex) ?: questions.firstOrNull()

    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(selectedDifficulty?.timeLimit ?: 15) }

    // Timer logic
    LaunchedEffect(currentIndex, isAnswered, selectedDifficulty) {
        timeLeft = selectedDifficulty?.timeLimit ?: 15
        while (timeLeft > 0 && !isAnswered) {
            kotlinx.coroutines.delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && !isAnswered) {
            isAnswered = true
            soundManager.playEncouragingOops()
            viewModel.speakLumi("Time's up! The correct answer was ${currentQuestion?.correctAnswer}", MascotMood.ENCOURAGING)
            currentQuestion?.targetItem?.let { viewModel.onAnswerGiven(it.id, false) }
        }
    }

    // TTS voice prompt when question changes
    LaunchedEffect(currentIndex, currentQuestion) {
        currentQuestion?.let { q ->
            viewModel.speakLumi(q.targetTranslation, MascotMood.HAPPY)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBackground)
            .testTag("quiz_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FocusableCard(
                        onClick = onBack,
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = SleekSurface,
                        unfocusedBorderColor = SleekSurfaceBorder,
                        focusedBorderColor = SleekEmerald,
                        testTag = "quiz_back_button"
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SleekTextDark)
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.quiz_vocabulary_quiz),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Text(
                            text = stringResource(R.string.quiz_question_counter, currentIndex + 1, questions.size),
                            fontSize = 12.sp,
                            color = SleekTextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SleekGold.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "★ $score XP",
                            fontWeight = FontWeight.Black,
                            color = SleekGoldDark,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { ((currentIndex + 1).toFloat() / questions.size.coerceAtLeast(1)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = SleekEmerald,
                    trackColor = SleekSurfaceBorder
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⏱️ $timeLeft s",
                        fontWeight = FontWeight.Bold,
                        color = if (timeLeft <= 5) SleekCoral else SleekTextMuted,
                        fontSize = 16.sp
                    )
                }
            }

            // Question Display Card
            currentQuestion?.let { q ->
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.5.dp, SleekSurfaceBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = q.targetItem.emoji, fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = q.targetItem.englishWord,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = SleekTextDark
                        )
                        Text(
                            text = stringResource(R.string.quiz_choose_translation, targetLanguage.displayName),
                            fontSize = 12.sp,
                            color = SleekTextMuted,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FocusableCard(
                            onClick = { viewModel.speakLumi(q.targetTranslation, MascotMood.HAPPY) },
                            shape = RoundedCornerShape(14.dp),
                            backgroundColor = SleekOcean.copy(alpha = 0.12f),
                            unfocusedBorderColor = SleekOcean.copy(alpha = 0.3f),
                            focusedBorderColor = SleekOcean,
                            testTag = "quiz_audio_prompt_button"
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = SleekOceanDark, modifier = Modifier.size(18.dp))
                                Text(stringResource(R.string.quiz_listen_audio), fontWeight = FontWeight.Bold, color = SleekOceanDark, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Multiple Choice Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    q.options.forEachIndexed { optIndex, optionText ->
                        val isCorrectOption = optionText == q.correctAnswer
                        val isSelected = selectedOption == optionText

                        val cardBg = when {
                            !isAnswered -> SleekSurface
                            isCorrectOption -> SleekEmerald.copy(alpha = 0.18f)
                            isSelected -> SleekCoral.copy(alpha = 0.18f)
                            else -> SleekSurface
                        }

                        val borderColor = when {
                            !isAnswered -> SleekSurfaceBorder
                            isCorrectOption -> SleekEmerald
                            isSelected -> SleekCoral
                            else -> SleekSurfaceBorder
                        }

                        FocusableCard(
                            onClick = {
                                if (!isAnswered) {
                                    selectedOption = optionText
                                    isAnswered = true
                                    val correct = optionText == q.correctAnswer
                                    if (correct) {
                                        score += 20
                                        soundManager.playPositiveAnswerChime()
                                        viewModel.speakLumi("Correct! High five!", MascotMood.HAPPY)
                                    } else {
                                        soundManager.playEncouragingOops()
                                        viewModel.speakLumi("Almost! The right word is ${q.correctAnswer}", MascotMood.ENCOURAGING)
                                    }
                                    viewModel.onAnswerGiven(q.targetItem.id, correct)
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            backgroundColor = cardBg,
                            unfocusedBorderColor = borderColor,
                            focusedBorderColor = SleekGold,
                            testTag = "quiz_option_$optIndex"
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = optionText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextDark
                                )

                                if (isAnswered) {
                                    if (isCorrectOption) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SleekEmerald)
                                    } else if (isSelected) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = SleekCoral)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Mascot Companion & Next Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val isAnswerCorrect = if (isAnswered) (selectedOption == currentQuestion?.correctAnswer) else null
                LumiLottieReaction(
                    isCorrect = isAnswerCorrect,
                    streakCount = if (isAnswerCorrect == true) (score / 20) else 0,
                    size = 96.dp,
                    customSpeech = when {
                        !isAnswered -> stringResource(R.string.lumi_speech_tap_answer)
                        isAnswerCorrect == true -> stringResource(R.string.lumi_speech_great_job)
                        else -> stringResource(R.string.lumi_speech_keep_practicing)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (currentIndex < questions.lastIndex) {
                            currentIndex++
                            selectedOption = null
                            isAnswered = false
                        } else {
                            onFinishQuiz(score, questions.size)
                        }
                    },
                    enabled = isAnswered,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("quiz_next_button")
                ) {
                    Text(
                        text = if (currentIndex < questions.lastIndex) stringResource(R.string.quiz_next_question) else stringResource(R.string.quiz_see_results),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
