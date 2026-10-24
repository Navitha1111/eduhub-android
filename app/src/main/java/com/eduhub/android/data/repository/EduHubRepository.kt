package com.eduhub.android.data.repository

import com.eduhub.android.data.local.EduHubDatabase
import com.eduhub.android.data.local.entities.*
import com.eduhub.android.domain.models.DailyStreak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class EduHubRepository(private val database: EduHubDatabase) {

    private val courseDao = database.courseDao()
    private val lessonDao = database.lessonDao()
    private val quizDao = database.quizDao()
    private val challengeDao = database.challengeDao()
    private val bookmarkDao = database.bookmarkDao()
    private val noteDao = database.noteDao()
    private val userProgressDao = database.userProgressDao()
    private val certificateDao = database.certificateDao()
    private val snippetDao = database.snippetDao()

    // --- Courses ---
    val allCourses: Flow<List<CourseEntity>> = courseDao.getAllCoursesFlow()
    val inProgressCourses: Flow<List<CourseEntity>> = courseDao.getInProgressCoursesFlow()
    val completedCourses: Flow<List<CourseEntity>> = courseDao.getCompletedCoursesFlow()

    fun getCoursesByCategory(category: String): Flow<List<CourseEntity>> {
        return if (category == "All Categories" || category == "ALL") {
            courseDao.getAllCoursesFlow()
        } else {
            courseDao.getCoursesByCategoryFlow(category)
        }
    }

    fun getCourseBySlug(slug: String): Flow<CourseEntity?> = courseDao.getCourseBySlugFlow(slug)
    suspend fun getCourseDirect(slug: String): CourseEntity? = courseDao.getCourseBySlug(slug)

    // --- Lessons ---
    fun getLessonsForCourse(courseSlug: String): Flow<List<LessonEntity>> =
        lessonDao.getLessonsForCourseSlugFlow(courseSlug)

    fun getLessonById(id: Long): Flow<LessonEntity?> = lessonDao.getLessonByIdFlow(id)

    suspend fun completeLesson(lessonId: Long, courseId: Long): Boolean {
        lessonDao.updateLessonCompletion(lessonId, true, System.currentTimeMillis())
        userProgressDao.addXp(50)
        userProgressDao.incrementCompletedLessons()

        // Recalculate course progress
        val total = lessonDao.getTotalLessonsCountForCourse(courseId)
        val done = lessonDao.getCompletedLessonsCountForCourse(courseId)
        val pct = if (total > 0) ((done.toDouble() / total) * 100).toInt() else 0
        val isCompleted = done >= total

        courseDao.updateProgress(courseId, done, pct, isCompleted)

        // If completed, automatically award certificate
        if (isCompleted) {
            val course = courseDao.getCourseById(courseId)
            val user = userProgressDao.getUserProgress()
            if (course != null && user != null) {
                issueCertificate(course.slug, course.title, user.studentName, 100)
            }
        }
        return isCompleted
    }

    // --- Quizzes ---
    suspend fun getQuizForCourse(courseSlug: String): QuizEntity? =
        quizDao.getQuizForCourseSlug(courseSlug)

    fun getQuizForCourseFlow(courseSlug: String): Flow<QuizEntity?> =
        quizDao.getQuizForCourseSlugFlow(courseSlug)

    suspend fun getQuizQuestions(quizId: Long): List<QuizQuestionEntity> =
        quizDao.getQuestionsForQuiz(quizId)

    fun getQuizQuestionsFlow(quizId: Long): Flow<List<QuizQuestionEntity>> =
        quizDao.getQuestionsForQuizFlow(quizId)

    suspend fun recordQuizScore(quizId: Long, courseSlug: String, scorePct: Int) {
        val isPassed = scorePct >= 60
        quizDao.updateQuizResult(quizId, scorePct, isPassed, System.currentTimeMillis())
        if (isPassed) {
            userProgressDao.addXp(scorePct * 2)
            userProgressDao.incrementCompletedQuizzes()
        }
    }

    // --- Challenges ---
    suspend fun getChallengeForCourse(courseSlug: String): ChallengeEntity? =
        challengeDao.getChallengeForCourseSlug(courseSlug)

    fun getChallengeForCourseFlow(courseSlug: String): Flow<ChallengeEntity?> =
        challengeDao.getChallengeForCourseSlugFlow(courseSlug)

    suspend fun solveChallenge(challengeId: Long, earnedXp: Int) {
        challengeDao.markChallengeSolved(challengeId, System.currentTimeMillis())
        userProgressDao.addXp(earnedXp)
        userProgressDao.incrementCompletedChallenges()
    }

    // --- Bookmarks & Notes ---
    val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarksFlow()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotesFlow()

    suspend fun toggleBookmark(
        lessonId: Long,
        courseSlug: String,
        courseTitle: String,
        lessonTitle: String,
        moduleTitle: String
    ): Boolean {
        val existing = bookmarkDao.getBookmarkByLessonId(lessonId)
        return if (existing != null) {
            bookmarkDao.deleteBookmarkByLessonId(lessonId)
            lessonDao.updateBookmarkStatus(lessonId, false)
            false
        } else {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    lessonId = lessonId,
                    courseSlug = courseSlug,
                    courseTitle = courseTitle,
                    lessonTitle = lessonTitle,
                    moduleTitle = moduleTitle
                )
            )
            lessonDao.updateBookmarkStatus(lessonId, true)
            true
        }
    }

    fun getNoteForLesson(lessonId: Long): Flow<NoteEntity?> = noteDao.getNoteForLessonFlow(lessonId)

    suspend fun saveNote(
        lessonId: Long,
        courseSlug: String,
        courseTitle: String,
        lessonTitle: String,
        content: String
    ) {
        val existing = noteDao.getNoteForLesson(lessonId)
        val note = existing?.copy(content = content, updatedTimestamp = System.currentTimeMillis())
            ?: NoteEntity(
                lessonId = lessonId,
                courseSlug = courseSlug,
                courseTitle = courseTitle,
                lessonTitle = lessonTitle,
                content = content
            )
        noteDao.insertOrUpdateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    // --- Certificates ---
    val allCertificates: Flow<List<CertificateEntity>> = certificateDao.getAllCertificatesFlow()

    suspend fun issueCertificate(
        courseSlug: String,
        courseTitle: String,
        studentName: String,
        scorePct: Int = 100
    ): CertificateEntity {
        val existing = certificateDao.getCertificateByCourseSlug(courseSlug)
        if (existing != null) return existing

        val dateStr = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
        val hash = generateVerificationHash(studentName, courseSlug)
        val grade = when {
            scorePct >= 90 -> "Distinction (Honors)"
            scorePct >= 75 -> "Merit (Proficient)"
            else -> "Pass (Competent)"
        }
        val cert = CertificateEntity(
            verificationId = "EDUHUB-$hash",
            courseSlug = courseSlug,
            courseTitle = courseTitle,
            studentName = studentName,
            issueDate = dateStr,
            scorePercentage = scorePct,
            grade = grade
        )
        certificateDao.insertCertificate(cert)
        userProgressDao.addXp(250) // Certificate bonus XP
        return cert
    }

    private fun generateVerificationHash(name: String, slug: String): String {
        val raw = "$name:$slug:${System.currentTimeMillis()}"
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return digest.take(4).joinToString("") { "%02X".format(it) }
    }

    // --- User Progress & Gamification ---
    val userProgress: Flow<UserProgressEntity?> = userProgressDao.getUserProgressFlow()

    suspend fun updateStudentName(name: String) = userProgressDao.updateStudentName(name)
    suspend fun toggleTheme(isDark: Boolean) = userProgressDao.updateThemeMode(isDark)
    suspend fun updateSpeechRate(rate: Float) = userProgressDao.updateSpeechRate(rate)

    suspend fun checkDailyStreak() {
        val user = userProgressDao.getUserProgress() ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (user.lastActiveDate != today) {
            val newStreak = user.currentStreak + 1
            userProgressDao.updateStreak(newStreak, today)
            userProgressDao.addXp(30) // Daily streak bonus
        }
    }

    // --- Snippets & Playground ---
    val allSnippets: Flow<List<SnippetEntity>> = snippetDao.getAllSnippetsFlow()

    suspend fun saveSnippet(
        title: String,
        languageSlug: String,
        languageName: String,
        code: String,
        tags: String = "playground,custom"
    ) {
        snippetDao.insertSnippet(
            SnippetEntity(
                title = title,
                languageSlug = languageSlug,
                languageName = languageName,
                code = code,
                tags = tags
            )
        )
    }

    suspend fun deleteSnippet(snippet: SnippetEntity) = snippetDao.deleteSnippet(snippet)

    // --- Course Creator & Auto Generator ---
    suspend fun createCustomCourse(
        title: String,
        languageName: String,
        category: String,
        difficulty: String,
        hours: Int,
        description: String,
        starterCode: String
    ): Long {
        val slug = languageName.lowercase().replace(" ", "-") + "-" + System.currentTimeMillis() % 1000
        val course = CourseEntity(
            slug = slug,
            title = title,
            languageName = languageName,
            category = category,
            difficulty = difficulty,
            totalHours = hours,
            description = description,
            paradigm = "Custom user curriculum",
            iconEmoji = "✨",
            totalLessons = 4,
            completedLessons = 0,
            progressPercentage = 0,
            isCompleted = false,
            isCustom = true
        )
        val courseId = courseDao.insertCourse(course)

        // Seed 4 custom lessons
        val customLessons = (1..4).map { i ->
            LessonEntity(
                courseId = courseId,
                courseSlug = slug,
                courseTitle = title,
                moduleIndex = i,
                moduleTitle = "Custom Module $i",
                lessonOrder = i,
                title = "Lesson $i: Core $languageName Foundations",
                objectives = "Master the fundamentals of $languageName.",
                contentMarkdown = "# $languageName Lesson $i\n\nStudy this custom curriculum lesson and practice with the code editor.",
                codeExample = starterCode,
                expectedOutput = "Custom program executed.",
                isCompleted = false
            )
        }
        lessonDao.insertAll(customLessons)

        // Seed Custom Quiz
        val quizId = quizDao.insertQuiz(
            QuizEntity(
                courseId = courseId,
                courseSlug = slug,
                title = "$languageName Custom Quiz",
                totalQuestions = 2,
                bestScore = 0,
                isPassed = false
            )
        )
        val q1 = QuizQuestionEntity(
            quizId = quizId,
            questionOrder = 1,
            questionText = "What is the primary focus of $languageName?",
            optionA = "Modern Software Development",
            optionB = "Obsolete Punch Cards",
            optionC = "Random Numbers",
            optionD = "None of the above",
            correctOptionIndex = 0,
            explanation = "$languageName is designed for modern software development."
        )
        quizDao.insertQuestion(q1)

        // Seed Custom Challenge
        challengeDao.insertChallenge(
            ChallengeEntity(
                courseId = courseId,
                courseSlug = slug,
                languageName = languageName,
                title = "$languageName Custom Challenge",
                description = "Run your customized code and verify output.",
                starterCode = starterCode,
                rubricPatterns = languageName,
                hint = "Use the starter template provided.",
                solutionSnippet = starterCode,
                isSolved = false,
                earnedXp = 100
            )
        )

        return courseId
    }
}
