package com.eduhub.android.data.seed

import com.eduhub.android.data.local.EduHubDatabase
import com.eduhub.android.data.local.entities.*
import java.text.SimpleDateFormat
import java.util.*

object CurriculumSeeder {

    suspend fun populateDatabase(database: EduHubDatabase) {
        val courseDao = database.courseDao()
        val lessonDao = database.lessonDao()
        val quizDao = database.quizDao()
        val challengeDao = database.challengeDao()
        val userProgressDao = database.userProgressDao()
        val snippetDao = database.snippetDao()

        // 1. Check if already seeded
        if (courseDao.getCourseCount() > 0) return

        // 2. Initialize default User Progress
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        userProgressDao.insertOrUpdate(
            UserProgressEntity(
                id = 1,
                studentName = "Future Tech Leader",
                totalXp = 150,
                currentStreak = 1,
                lastActiveDate = todayStr,
                completedLessonsCount = 0,
                completedQuizzesCount = 0,
                completedChallengesCount = 0,
                isDarkMode = true,
                speechRate = 1.0f,
                preferredLanguageSlug = "python"
            )
        )

        // 3. Populate all languages from catalog
        for (spec in LanguageCatalogData.languages) {
            val courseEntity = CourseEntity(
                slug = spec.slug,
                title = spec.courseTitle,
                languageName = spec.langName,
                category = spec.category,
                difficulty = spec.difficulty,
                totalHours = spec.hours,
                description = spec.description,
                paradigm = spec.paradigm,
                iconEmoji = spec.iconEmoji,
                totalLessons = 8,
                completedLessons = 0,
                progressPercentage = 0,
                isCompleted = false,
                isCustom = false,
                isFavorite = spec.slug in listOf("python", "kotlin", "javascript")
            )
            val courseId = courseDao.insertCourse(courseEntity)

            // Seed 4-Tier 8-Lesson Progressive Curriculum (0 to Advanced)
            val lessons = listOf(
                // TIER 0: FUNDAMENTALS (0% -> 25%)
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 1,
                    moduleTitle = "Tier 0: Fundamentals & First Code (0% to 25%)",
                    lessonOrder = 1,
                    title = "Introduction, Architecture & First Program",
                    objectives = "Understand the language design philosophy, environment execution model, and execute your first program.",
                    contentMarkdown = "Welcome to **${spec.langName}**! In this lesson, you will learn the core design philosophy (${spec.paradigm}) and write your foundational first program. Every master programmer starts with the fundamental output pattern.",
                    codeExample = spec.helloCode,
                    expectedOutput = "Output stream verified successfully.",
                    isCompleted = false
                ),
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 1,
                    moduleTitle = "Tier 0: Fundamentals & First Code (0% to 25%)",
                    lessonOrder = 2,
                    title = "Variables, Types & Memory Semantics",
                    objectives = "Master primitive types, explicit and inferred declarations, immutability, and variable scoping.",
                    contentMarkdown = "Data representation is the bedrock of computing. Learn how ${spec.langName} allocates variables in memory, enforces type safety, and handles value mutation.",
                    codeExample = spec.varsCode,
                    expectedOutput = "Variables inspected and bound.",
                    isCompleted = false
                ),

                // TIER 1: CONTROL FLOW & LOGIC (25% -> 50%)
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 2,
                    moduleTitle = "Tier 1: Control Flow & Logic (25% to 50%)",
                    lessonOrder = 3,
                    title = "Arithmetic, Logical Operators & Expressions",
                    objectives = "Evaluate mathematical, relational, and boolean expressions with precision.",
                    contentMarkdown = "Computational logic relies on arithmetic operators, compound assignments, and logical chaining. Master how expressions evaluate in ${spec.langName}.",
                    codeExample = spec.opsCode,
                    expectedOutput = "Expression evaluation result verified.",
                    isCompleted = false
                ),
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 2,
                    moduleTitle = "Tier 1: Control Flow & Logic (25% to 50%)",
                    lessonOrder = 4,
                    title = "Conditional Branching & Pattern Matching",
                    objectives = "Direct program flow using if/else structures, switch/when constructs, and pattern matching.",
                    contentMarkdown = "Write clean decision branching. Handle multiple states cleanly without nested spaghetti code using pattern matching where supported.",
                    codeExample = spec.condCode,
                    expectedOutput = "Decision branches evaluated.",
                    isCompleted = false
                ),
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 2,
                    moduleTitle = "Tier 1: Control Flow & Logic (25% to 50%)",
                    lessonOrder = 5,
                    title = "Iteration, Loops & Sequence Processing",
                    objectives = "Process repeated operations, sequences, and ranges with structured loops and comprehensions.",
                    contentMarkdown = "Learn how ${spec.langName} loops over collections, manages loop indices, breaks and continues iterations efficiently.",
                    codeExample = spec.loopCode,
                    expectedOutput = "Iteration sequence complete.",
                    isCompleted = false
                ),

                // TIER 2: DATA STRUCTURES & OOP (50% -> 75%)
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 3,
                    moduleTitle = "Tier 2: Data Structures & Modularity (50% to 75%)",
                    lessonOrder = 6,
                    title = "Collections, Lists, Maps & Higher-Order Transforms",
                    objectives = "Structure and filter datasets with built-in collections and higher-order functional pipelines.",
                    contentMarkdown = "Manipulate arrays, lists, sets, and dictionaries/maps. Apply filtering, mapping, and reduction operations.",
                    codeExample = spec.collectionsCode,
                    expectedOutput = "Collections mapped and filtered.",
                    isCompleted = false
                ),
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 3,
                    moduleTitle = "Tier 2: Data Structures & Modularity (50% to 75%)",
                    lessonOrder = 7,
                    title = "Functions, Closures & Pure Logic",
                    objectives = "Write modular, reusable functions, default arguments, and lambda closures.",
                    contentMarkdown = "Functions allow code reuse and separation of concerns. Learn parameter passing semantics, return types, and first-class functions in ${spec.langName}.",
                    codeExample = spec.funcCode,
                    expectedOutput = "Function pipeline executed.",
                    isCompleted = false
                ),

                // TIER 3: ADVANCED & CAPSTONE (75% -> 100%)
                LessonEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    courseTitle = spec.courseTitle,
                    moduleIndex = 4,
                    moduleTitle = "Tier 3: Advanced Mastery & Capstone (75% to 100%)",
                    lessonOrder = 8,
                    title = "OOP, Error Handling & Capstone System Architecture",
                    objectives = "Construct robust domain objects, handle edge cases with exception safety, and build a capstone project.",
                    contentMarkdown = "Master object modeling, robust error containment, serialization, and real-world system design in ${spec.langName}.",
                    codeExample = spec.oopCode + "\n\n" + spec.errorHandlingCode + "\n\n" + spec.capstoneCode,
                    expectedOutput = "Capstone system architecture compiled.",
                    isCompleted = false
                )
            )
            lessonDao.insertAll(lessons)

            // Seed Quiz & 5 Mastery Questions
            val quizId = quizDao.insertQuiz(
                QuizEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    title = "${spec.langName} Comprehensive Mastery Quiz",
                    totalQuestions = 5,
                    bestScore = 0,
                    isPassed = false
                )
            )

            val questions = listOf(
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 1,
                    questionText = "Which paradigm best characterizes ${spec.langName}?",
                    optionA = spec.paradigm.split(",").firstOrNull()?.trim() ?: "Object-Oriented",
                    optionB = "Pure Machine Binary Only",
                    optionC = "Unstructured Goto-only language",
                    optionD = "Static Batch-card punch system",
                    correctOptionIndex = 0,
                    explanation = "${spec.langName} is officially classified as: ${spec.paradigm}."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 2,
                    questionText = "What is the recommended approach for handling unexpected errors in ${spec.langName}?",
                    optionA = "Ignore errors and let the runtime crash",
                    optionB = "Use structured try/catch or Result/Option error propagation types",
                    optionC = "Write print statements only",
                    optionD = "Reboot the operating system",
                    correctOptionIndex = 1,
                    explanation = "Robust software uses structured exception handling or monadic Result/Option types to prevent crashes."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 3,
                    questionText = "How do higher-order collection transforms (like filter/map) improve code quality?",
                    optionA = "They make code slower and less readable",
                    optionB = "They enable declarative, readable, and less error-prone data transformations",
                    optionC = "They are only available in assembly languages",
                    optionD = "They eliminate the need for variables completely",
                    correctOptionIndex = 1,
                    explanation = "Declarative higher-order functions reduce boilerplate loops and prevent off-by-one indexing errors."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 4,
                    questionText = "What is the key benefit of immutability and const/val bindings?",
                    optionA = "It prevents unintended state mutation and enhances thread safety",
                    optionB = "It doubles memory usage without benefits",
                    optionC = "It disables functions",
                    optionD = "It prevents compilation",
                    correctOptionIndex = 0,
                    explanation = "Immutable data structures prevent side-effects and simplify concurrent and asynchronous execution."
                ),
                QuizQuestionEntity(
                    quizId = quizId,
                    questionOrder = 5,
                    questionText = "When completing a capstone project in ${spec.langName}, what is the primary architecture goal?",
                    optionA = "Putting all code into a single 10,000 line file",
                    optionB = "Separation of concerns, clean modularity, and reproducible tests",
                    optionC = "Avoiding comments and type definitions",
                    optionD = "Relying purely on global variables",
                    correctOptionIndex = 1,
                    explanation = "Enterprise capstone systems require modular architectures, clean abstractions, and testability."
                )
            )
            quizDao.insertAllQuestions(questions)

            // Seed Hands-On Coding Challenge
            challengeDao.insertChallenge(
                ChallengeEntity(
                    courseId = courseId,
                    courseSlug = spec.slug,
                    languageName = spec.langName,
                    title = spec.challengeTitle,
                    description = spec.challengeDescription,
                    starterCode = spec.challengeStarter,
                    rubricPatterns = spec.challengeRubric,
                    hint = spec.challengeHint,
                    solutionSnippet = spec.funcCode,
                    isSolved = false,
                    earnedXp = 100
                )
            )

            // Seed Snippet Starter
            snippetDao.insertSnippet(
                SnippetEntity(
                    title = "${spec.langName} Quick Reference Starter",
                    languageSlug = spec.slug,
                    languageName = spec.langName,
                    code = spec.cheatSheetSummary,
                    tags = "syntax,cheatsheet,starter"
                )
            )
        }
    }
}
