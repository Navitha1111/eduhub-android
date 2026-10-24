package com.eduhub.android.domain.models

/**
 * Mastery level definitions representing the 0-to-Advanced journey.
 */
enum class LanguageLevel(val title: String, val tier: Int, val description: String, val colorHex: Long) {
    TIER_0_BEGINNER("Level 0: Fundamentals", 0, "Absolute basics: syntax, variables, operators, I/O", 0xFF06B6D4),
    TIER_1_INTERMEDIATE("Level 1: Control Flow", 1, "Logic branching, loops, functions, collections", 0xFF3B82F6),
    TIER_2_ADVANCED("Level 2: OOP & Modularity", 2, "Classes, data structures, exception handling, generics", 0xFF8B5CF6),
    TIER_3_MASTER("Level 3: Expert & Capstone", 3, "Async I/O, concurrency, memory, real-world projects", 0xFF10B981)
}

enum class CourseCategory(val displayName: String) {
    ALL("All Categories"),
    GENERAL("General Programming"),
    WEB("Web & Full-Stack"),
    MOBILE("Mobile & Cross-Platform"),
    DATA_AI("Data Science & AI"),
    SYSTEMS("Systems & Low-Level"),
    FUNCTIONAL("Functional & Modern"),
    SCRIPTING("Scripting & DevOps"),
    EMERGING("Emerging & Specialized")
}

data class DailyStreak(
    val currentStreakDays: Int = 1,
    val lastActiveDate: String = "",
    val totalXp: Int = 150,
    val levelNumber: Int = 1,
    val levelTitle: String = "🌱 Beginner Explorer"
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val unlockedDate: String = ""
)

data class CodeExecutionResult(
    val isSuccess: Boolean,
    val output: String,
    val passedChecks: List<String> = emptyList(),
    val missingChecks: List<String> = emptyList()
)

data class Flashcard(
    val id: Long,
    val languageSlug: String,
    val languageName: String,
    val question: String,
    val answer: String,
    val category: String
)
