package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.model.LearningCategory
import com.example.ui.components.AchievementUnlockedDialog
import com.example.ui.components.LumiQuizFeedbackOverlay
import com.example.ui.components.LumiQuestModal
import com.example.ui.screens.ColorMixerScreen
import com.example.ui.screens.ExploreCategoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguagePickerDialog
import com.example.ui.screens.MovementQuestScreen
import com.example.ui.screens.MysterySpotlightScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.ParentHubScreen
import com.example.ui.screens.ParentQrSyncScreen
import com.example.ui.screens.ShadowGuessScreen
import com.example.ui.screens.SoundMatchScreen
import com.example.ui.screens.StickerBookScreen
import com.example.ui.screens.WorldMapScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LumiViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.util.SmartEngagementManager
import com.example.util.SmartSuggestionType
import com.example.ui.components.SmartEngagementDialog
import com.example.ui.screens.OnboardingScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: LumiViewModel by viewModels {
        val app = application as LumiApplication
        LumiViewModel.Factory(
            repository = app.repository,
            speechHelper = app.speechHelper,
            freesoundRepository = app.freesoundRepository,
            giphyRepository = app.giphyRepository,
            pixabayRepository = app.pixabayRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LumiApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LumiApp(viewModel: LumiViewModel) {
    val context = LocalContext.current
    val engagementManager = remember { SmartEngagementManager(context) }
    val networkObserver = remember { com.example.util.NetworkObserver.getInstance(context) }
    val isOnline by networkObserver.isOnline.collectAsState(initial = networkObserver.checkCurrentConnectivity())

    val navController = rememberNavController()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    var showLanguagePicker by remember { mutableStateOf(false) }

    val activeBreakQuest by viewModel.activePhysicalBreak.collectAsState()
    val isBreakVisible by viewModel.isPhysicalBreakVisible.collectAsState()
    val unlockedBadge by viewModel.unlockedBadgeEvent.collectAsState()
    val quizFeedback by viewModel.quizFeedbackEvent.collectAsState()

    var activeEngagementSuggestion by remember { mutableStateOf<SmartSuggestionType?>(null) }

    LaunchedEffect(Unit) {
        engagementManager.recordAppLaunch()
        com.example.service.DailyReminderWorker.scheduleDailyReminder(context)
    }

    LaunchedEffect(navController.currentBackStackEntryFlow) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == "home" && engagementManager.isOnboardingCompleted) {
                delay(2000)
                activeEngagementSuggestion = engagementManager.calculateBestTimeSuggestion()
            }
        }
    }

    val recentSessions by viewModel.recentSessions.collectAsState()
    var lastSessionsCount by remember { mutableStateOf(-1) }

    LaunchedEffect(recentSessions) {
        if (lastSessionsCount == -1) {
            lastSessionsCount = recentSessions.size
        } else if (recentSessions.size > lastSessionsCount) {
            engagementManager.recordGameCompleted()
            lastSessionsCount = recentSessions.size
        }
    }

    SharedTransitionLayout {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "splash",
                enterTransition = { fadeIn(tween(260)) + scaleIn(initialScale = 0.96f, animationSpec = tween(260)) },
                exitTransition = { fadeOut(tween(200)) },
                popEnterTransition = { fadeIn(tween(260)) + scaleIn(initialScale = 0.96f, animationSpec = tween(260)) },
                popExitTransition = { fadeOut(tween(200)) }
            ) {
                composable("splash") {
                    com.example.ui.screens.SplashScreen(
                        onSplashFinished = {
                            if (engagementManager.isOnboardingCompleted) {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else {
                                navController.navigate("onboarding") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("onboarding") {
                    OnboardingScreen(
                        onOnboardingFinished = {
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }

                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateCategory = { category ->
                            navController.navigate("category/${category.id}")
                        },
                        onNavigateGame = { gameId ->
                            navController.navigate("game/$gameId")
                        },
                        onNavigateParentHub = {
                            navController.navigate("parent_hub")
                        },
                        onOpenLanguagePicker = {
                            showLanguagePicker = true
                        },
                        onNavigateWorldMap = {
                            navController.navigate("world_map")
                        },
                        onNavigateParentQrSync = {
                            navController.navigate("parent_qr_sync")
                        }
                    )
                }

                composable("world_map") {
                    WorldMapScreen(
                        viewModel = viewModel,
                        onSelectCategory = { category ->
                            navController.navigate("category/${category.id}")
                        },
                        onBack = { navController.popBackStack() },
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }

                composable(
                    route = "category/{categoryId}",
                    arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val catId = backStackEntry.arguments?.getString("categoryId")
                    val category = LearningCategory.entries.find { it.id == catId } ?: LearningCategory.ANIMALS
                    ExploreCategoryScreen(
                        category = category,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        animatedVisibilityScope = this,
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }

                composable("game/mystery_spotlight") {
                    MysterySpotlightScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("game/sound_match") {
                    SoundMatchScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("game/shadow_guess") {
                    ShadowGuessScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("game/color_mixer") {
                    ColorMixerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("game/review_mistakes") {
                    com.example.ui.screens.ReviewMistakesScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

            composable("game/movement_quest") {
                MovementQuestScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("game/find_it") {
                com.example.ui.screens.FindItGameScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("find_it") {
                com.example.ui.screens.FindItGameScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("game/achievement_gallery") {
                com.example.ui.screens.AchievementGalleryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("achievement_gallery") {
                com.example.ui.screens.AchievementGalleryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("game/quiz") {
                com.example.ui.screens.QuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onFinishQuiz = { score, total ->
                        navController.navigate("lesson_celebration/$score/$total")
                    }
                )
            }

            composable("quiz") {
                com.example.ui.screens.QuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onFinishQuiz = { score, total ->
                        navController.navigate("lesson_celebration/$score/$total")
                    }
                )
            }

            composable("game/pronunciation") {
                com.example.ui.screens.PronunciationPracticeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onFinishLesson = { score, total ->
                        navController.navigate("lesson_celebration/$score/$total")
                    }
                )
            }

            composable("pronunciation") {
                com.example.ui.screens.PronunciationPracticeScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onFinishLesson = { score, total ->
                        navController.navigate("lesson_celebration/$score/$total")
                    }
                )
            }

            composable(
                route = "lesson_celebration/{score}/{total}",
                arguments = listOf(
                    navArgument("score") { type = NavType.IntType },
                    navArgument("total") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val score = backStackEntry.arguments?.getInt("score") ?: 100
                val total = backStackEntry.arguments?.getInt("total") ?: 5
                com.example.ui.screens.LessonCelebrationScreen(
                    viewModel = viewModel,
                    score = score,
                    wordsMasteredCount = total,
                    accuracyPercent = 100,
                    onContinueNext = {
                        navController.navigate("game/quiz") {
                            popUpTo("home")
                        }
                    },
                    onBackHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("lesson_celebration") {
                com.example.ui.screens.LessonCelebrationScreen(
                    viewModel = viewModel,
                    score = 100,
                    wordsMasteredCount = 5,
                    accuracyPercent = 100,
                    onContinueNext = {
                        navController.navigate("game/quiz") {
                            popUpTo("home")
                        }
                    },
                    onBackHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            composable("game/dashboard") {
                com.example.ui.screens.ProgressDashboardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("dashboard") {
                com.example.ui.screens.ProgressDashboardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("game/sticker_book") {
                StickerBookScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("game/world_map") {
                WorldMapScreen(
                    viewModel = viewModel,
                    onSelectCategory = { category ->
                        navController.navigate("category/${category.id}")
                    },
                    onBack = { navController.popBackStack() },
                    animatedVisibilityScope = this,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }

            composable("game/parent_qr_sync") {
                ParentQrSyncScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("parent_hub") {
                ParentHubScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("parent_dashboard") {
                ParentDashboardScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("parent_qr_sync") {
                ParentQrSyncScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                com.example.ui.screens.SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("game/settings") {
                com.example.ui.screens.SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        com.example.ui.components.OfflineModeBanner(
            isOffline = !isOnline,
            modifier = Modifier.align(androidx.compose.ui.Alignment.TopCenter)
        )
    }
}

    // Global Language Picker Dialog
    if (showLanguagePicker) {
        LanguagePickerDialog(
            currentLanguage = targetLanguage,
            onLanguageSelected = { lang ->
                viewModel.setLanguage(lang)
            },
            onDismiss = { showLanguagePicker = false }
        )
    }

    // Global Movement / Physical Break Quest Notification Popup
    if (isBreakVisible && activeBreakQuest != null) {
        LumiQuestModal(
            quest = activeBreakQuest!!,
            onCompleted = { viewModel.completePhysicalActivity(activeBreakQuest!!) },
            onDismiss = { viewModel.dismissPhysicalActivity() },
            onReplayAudio = { viewModel.speakLumi(activeBreakQuest!!.spokenPrompt) }
        )
    }

    // Global Achievement Milestone Unlocked Popup Dialog
    if (unlockedBadge != null) {
        AchievementUnlockedDialog(
            badge = unlockedBadge!!,
            onDismiss = { viewModel.dismissUnlockedBadgeEvent() },
            onViewGallery = {
                navController.navigate("achievement_gallery")
            }
        )
    }

    // Global Quiz Feedback Lottie Mascot Overlay
    if (quizFeedback != null) {
        LumiQuizFeedbackOverlay(
            feedbackType = quizFeedback!!,
            visible = true,
            onFinished = { viewModel.dismissQuizFeedback() }
        )
    }

    // Smart Engagement suggestion dialog
    if (activeEngagementSuggestion != null) {
        SmartEngagementDialog(
            suggestionType = activeEngagementSuggestion!!,
            onConfirm = {
                when (activeEngagementSuggestion!!) {
                    com.example.util.SmartSuggestionType.SHARE_APP -> engagementManager.executeShareApp()
                    com.example.util.SmartSuggestionType.RATE_APP -> engagementManager.executeRateApp()
                    com.example.util.SmartSuggestionType.UPDATE_APP -> engagementManager.executeUpdateApp()
                }
                activeEngagementSuggestion = null
            },
            onDismiss = {
                engagementManager.recordPromptDismissed()
                activeEngagementSuggestion = null
            }
        )
    }
}
