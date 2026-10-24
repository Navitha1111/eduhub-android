package com.eduhub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eduhub.android.ui.components.CommonBottomNavBar
import com.eduhub.android.ui.navigation.Screen
import com.eduhub.android.ui.screens.*
import com.eduhub.android.ui.theme.EduHubTheme
import com.eduhub.android.ui.viewmodels.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as EduHubApp
        val repository = app.repository
        val ttsManager = app.ttsManager

        setContent {
            val userProgress by repository.userProgress.collectAsState(initial = null)
            val isDark = userProgress?.isDarkMode ?: true

            EduHubTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                MainAppScaffold(
                    navController = navController,
                    repository = repository,
                    ttsManager = ttsManager
                )
            }
        }
    }
}

@Composable
fun MainAppScaffold(
    navController: NavHostController,
    repository: com.eduhub.android.data.repository.EduHubRepository,
    ttsManager: com.eduhub.android.utils.TextToSpeechManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Courses.route,
        Screen.Playground.route,
        Screen.CheatSheet.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                CommonBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home Dashboard
            composable(Screen.Home.route) {
                val homeVm = rememberViewModel { HomeViewModel(repository) }
                HomeScreen(
                    viewModel = homeVm,
                    onNavigateToCourse = { slug -> navController.navigate(Screen.CourseDetail.createRoute(slug)) },
                    onNavigateToPlayground = { navController.navigate(Screen.Playground.createRoute()) },
                    onNavigateToFlashcards = { navController.navigate(Screen.Flashcard.route) },
                    onNavigateToBookmarks = { navController.navigate(Screen.BookmarksNotes.route) },
                    onNavigateToCertificates = { navController.navigate(Screen.Certificates.route) },
                    onNavigateToCreator = { navController.navigate(Screen.AdminCourseCreator.route) },
                    onNavigateToCourses = { navController.navigate(Screen.Courses.route) }
                )
            }

            // 2. Courses Catalog
            composable(Screen.Courses.route) {
                val courseVm = rememberViewModel { CourseViewModel(repository) }
                CoursesScreen(
                    viewModel = courseVm,
                    onNavigateToCourse = { slug -> navController.navigate(Screen.CourseDetail.createRoute(slug)) }
                )
            }

            // 3. Course Details Roadmap (4-tier)
            composable(
                route = Screen.CourseDetail.route,
                arguments = listOf(navArgument("slug") { type = NavType.StringType })
            ) { backStackEntry ->
                val slug = backStackEntry.arguments?.getString("slug") ?: "python"
                val courseVm = rememberViewModel { CourseViewModel(repository) }
                CourseDetailScreen(
                    courseSlug = slug,
                    viewModel = courseVm,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLesson = { id -> navController.navigate(Screen.LessonDetail.createRoute(id)) },
                    onNavigateToQuiz = { s -> navController.navigate(Screen.Quiz.createRoute(s)) },
                    onNavigateToChallenge = { s -> navController.navigate(Screen.Challenge.createRoute(s)) },
                    onNavigateToPlayground = { s -> navController.navigate(Screen.Playground.createRoute(s)) }
                )
            }

            // 4. Lesson Detail & Audio Narrator Reader
            composable(
                route = Screen.LessonDetail.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.LongType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
                val lessonVm = rememberViewModel { LessonViewModel(repository, ttsManager) }
                LessonDetailScreen(
                    lessonId = lessonId,
                    viewModel = lessonVm,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlayground = { lang -> navController.navigate(Screen.Playground.createRoute(lang)) }
                )
            }

            // 5. Interactive Quiz
            composable(
                route = Screen.Quiz.route,
                arguments = listOf(navArgument("courseSlug") { type = NavType.StringType })
            ) { backStackEntry ->
                val slug = backStackEntry.arguments?.getString("courseSlug") ?: "python"
                val quizVm = rememberViewModel { QuizViewModel(repository) }
                QuizScreen(
                    courseSlug = slug,
                    viewModel = quizVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 6. Coding Challenge
            composable(
                route = Screen.Challenge.route,
                arguments = listOf(navArgument("courseSlug") { type = NavType.StringType })
            ) { backStackEntry ->
                val slug = backStackEntry.arguments?.getString("courseSlug") ?: "python"
                val challengeVm = rememberViewModel { ChallengeViewModel(repository) }
                ChallengeScreen(
                    courseSlug = slug,
                    viewModel = challengeVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 7. Code Playground
            composable(
                route = Screen.Playground.route,
                arguments = listOf(navArgument("lang") {
                    type = NavType.StringType
                    defaultValue = "python"
                })
            ) { backStackEntry ->
                val lang = backStackEntry.arguments?.getString("lang") ?: "python"
                val playgroundVm = rememberViewModel { PlaygroundViewModel(repository) }
                CodePlaygroundScreen(
                    initialLanguage = lang,
                    viewModel = playgroundVm
                )
            }

            // 8. Cheat Sheet
            composable(Screen.CheatSheet.route) {
                val cheatVm = rememberViewModel { CheatSheetViewModel() }
                CheatSheetScreen(
                    viewModel = cheatVm,
                    onNavigateToPlayground = { lang -> navController.navigate(Screen.Playground.createRoute(lang)) }
                )
            }

            // 9. Flashcards
            composable(Screen.Flashcard.route) {
                val flashVm = rememberViewModel { FlashcardViewModel() }
                FlashcardScreen(
                    viewModel = flashVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 10. Bookmarks & Notes
            composable(Screen.BookmarksNotes.route) {
                val bnVm = rememberViewModel { BookmarksNotesViewModel(repository) }
                BookmarksNotesScreen(
                    viewModel = bnVm,
                    onNavigateToLesson = { id -> navController.navigate(Screen.LessonDetail.createRoute(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 11. Certificates
            composable(Screen.Certificates.route) {
                val certVm = rememberViewModel { CertificateViewModel(repository) }
                CertificateScreen(
                    viewModel = certVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 12. 1-Click Admin Course Creator
            composable(Screen.AdminCourseCreator.route) {
                val creatorVm = rememberViewModel { AdminCourseCreatorViewModel(repository) }
                AdminCourseCreatorScreen(
                    viewModel = creatorVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 13. Profile & Badges
            composable(Screen.Profile.route) {
                val profileVm = rememberViewModel { ProfileViewModel(repository) }
                ProfileScreen(
                    viewModel = profileVm,
                    onNavigateToCertificates = { navController.navigate(Screen.Certificates.route) },
                    onNavigateToBookmarks = { navController.navigate(Screen.BookmarksNotes.route) },
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) }
                )
            }

            // 14. VPS Cloud Authentication & Student Registration
            composable(Screen.Auth.route) {
                AuthScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() },
                    onAuthSuccess = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
inline fun <reified T : androidx.lifecycle.ViewModel> rememberViewModel(
    crossinline creator: () -> T
): T {
    return androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : androidx.lifecycle.ViewModel> create(modelClass: Class<VM>): VM {
                return creator() as VM
            }
        }
    )
}
