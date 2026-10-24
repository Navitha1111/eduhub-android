package com.eduhub.android.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quizzes",
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
data class QuizEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val courseSlug: String,
    val title: String,
    val totalQuestions: Int = 5,
    val bestScore: Int = 0,
    val isPassed: Boolean = false,
    val completedTimestamp: Long = 0L
)

@Entity(
    tableName = "quiz_questions",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["quizId"])]
)
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizId: Long,
    val questionOrder: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0..3
    val explanation: String
)
