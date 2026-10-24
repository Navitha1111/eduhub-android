package com.eduhub.android.data.local.dao

import androidx.room.*
import com.eduhub.android.data.local.entities.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY moduleIndex ASC, lessonOrder ASC")
    fun getLessonsForCourseFlow(courseId: Long): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE courseSlug = :courseSlug ORDER BY moduleIndex ASC, lessonOrder ASC")
    fun getLessonsForCourseSlugFlow(courseSlug: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE courseSlug = :courseSlug ORDER BY moduleIndex ASC, lessonOrder ASC")
    suspend fun getLessonsForCourseSlug(courseSlug: String): List<LessonEntity>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: Long): LessonEntity?

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    fun getLessonByIdFlow(id: Long): Flow<LessonEntity?>

    @Query("SELECT * FROM lessons WHERE isBookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedLessonsFlow(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE isCompleted = 0 ORDER BY id ASC LIMIT 1")
    suspend fun getNextUnfinishedLesson(): LessonEntity?

    @Query("SELECT * FROM lessons WHERE isCompleted = 1")
    fun getAllCompletedLessonsFlow(): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :isCompleted, completionTimestamp = :timestamp WHERE id = :lessonId")
    suspend fun updateLessonCompletion(lessonId: Long, isCompleted: Boolean, timestamp: Long)

    @Query("UPDATE lessons SET isBookmarked = :isBookmarked WHERE id = :lessonId")
    suspend fun updateBookmarkStatus(lessonId: Long, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM lessons WHERE courseId = :courseId")
    suspend fun getTotalLessonsCountForCourse(courseId: Long): Int

    @Query("SELECT COUNT(*) FROM lessons WHERE courseId = :courseId AND isCompleted = 1")
    suspend fun getCompletedLessonsCountForCourse(courseId: Long): Int
}
