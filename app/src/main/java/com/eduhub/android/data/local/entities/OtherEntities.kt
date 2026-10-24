package com.eduhub.android.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "challenges",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"]), Index(value = ["courseSlug"])]
)
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val courseSlug: String,
    val languageName: String,
    val title: String,
    val description: String,
    val starterCode: String,
    val rubricPatterns: String, // comma-separated required keywords or Regex
    val hint: String,
    val solutionSnippet: String = "",
    val isSolved: Boolean = false,
    val solvedTimestamp: Long = 0L,
    val earnedXp: Int = 100
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: Long,
    val courseSlug: String,
    val courseTitle: String,
    val lessonTitle: String,
    val content: String,
    val updatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val lessonId: Long,
    val courseSlug: String,
    val courseTitle: String,
    val lessonTitle: String,
    val moduleTitle: String,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val studentName: String = "Future Tech Leader",
    val totalXp: Int = 120,
    val currentStreak: Int = 1,
    val lastActiveDate: String = "",
    val completedLessonsCount: Int = 0,
    val completedQuizzesCount: Int = 0,
    val completedChallengesCount: Int = 0,
    val isDarkMode: Boolean = true,
    val speechRate: Float = 1.0f,
    val preferredLanguageSlug: String = "python",
    val serverUrl: String = "http://162.35.9.208:8080",
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val email: String = "",
    val role: String = "STUDENT",
    val serverUserId: Long = 0L,
    val adminToken: String = "",
    val lastSyncTimestamp: Long = 0L
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val verificationId: String,
    val courseSlug: String,
    val courseTitle: String,
    val studentName: String,
    val issueDate: String,
    val scorePercentage: Int = 100,
    val grade: String = "Mastery with Honors (100% Course Completion)",
    val sha256Signature: String = "",
    val aiCommendation: String = "",
    val issuerName: String = "EDUHUB Global Autonomous Academy",
    val isSyncedToServer: Boolean = false
)

@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val languageSlug: String,
    val languageName: String,
    val code: String,
    val tags: String = "starter,reference",
    val createdTimestamp: Long = System.currentTimeMillis()
)
