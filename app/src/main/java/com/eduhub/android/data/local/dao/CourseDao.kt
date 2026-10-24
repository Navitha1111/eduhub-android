package com.eduhub.android.data.local.dao

import androidx.room.*
import com.eduhub.android.data.local.entities.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY isCustom DESC, title ASC")
    fun getAllCoursesFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE category = :category ORDER BY title ASC")
    fun getCoursesByCategoryFlow(category: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Long): CourseEntity?

    @Query("SELECT * FROM courses WHERE slug = :slug LIMIT 1")
    suspend fun getCourseBySlug(slug: String): CourseEntity?

    @Query("SELECT * FROM courses WHERE slug = :slug LIMIT 1")
    fun getCourseBySlugFlow(slug: String): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE isCompleted = 1")
    fun getCompletedCoursesFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE progressPercentage > 0 AND progressPercentage < 100 ORDER BY progressPercentage DESC LIMIT 5")
    fun getInProgressCoursesFlow(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("UPDATE courses SET completedLessons = :completed, progressPercentage = :progress, isCompleted = :isCompleted WHERE id = :courseId")
    suspend fun updateProgress(courseId: Long, completed: Int, progress: Int, isCompleted: Boolean)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getCourseCount(): Int
}
