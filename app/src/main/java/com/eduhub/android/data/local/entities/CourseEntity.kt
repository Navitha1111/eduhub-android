package com.eduhub.android.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val slug: String,
    val title: String,
    val languageName: String,
    val category: String,
    val difficulty: String,
    val totalHours: Int,
    val description: String,
    val paradigm: String,
    val iconEmoji: String = "⚡",
    val totalLessons: Int = 8,
    val completedLessons: Int = 0,
    val progressPercentage: Int = 0,
    val isCompleted: Boolean = false,
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false
)
