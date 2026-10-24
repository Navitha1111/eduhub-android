package com.eduhub.android.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
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
data class LessonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val courseSlug: String,
    val courseTitle: String,
    val moduleIndex: Int, // 1: Tier 0, 2: Tier 1, 3: Tier 2, 4: Tier 3
    val moduleTitle: String,
    val lessonOrder: Int,
    val title: String,
    val objectives: String,
    val contentMarkdown: String,
    val codeExample: String,
    val expectedOutput: String,
    val isCompleted: Boolean = false,
    val completionTimestamp: Long = 0L,
    val isBookmarked: Boolean = false
)
