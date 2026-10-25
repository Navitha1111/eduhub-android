package com.eduhub.android.data.local.dao

import androidx.room.*
import com.eduhub.android.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdTimestamp DESC")
    fun getAllBookmarksFlow(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getBookmarkByLessonId(lessonId: Long): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE lessonId = :lessonId")
    suspend fun deleteBookmarkByLessonId(lessonId: Long)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getNoteForLesson(lessonId: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE lessonId = :lessonId LIMIT 1")
    fun getNoteForLessonFlow(lessonId: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes ORDER BY updatedTimestamp DESC")
    fun getAllNotesFlow(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: NoteEntity): Long

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    fun getUserProgressFlow(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
    suspend fun getUserProgress(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET totalXp = totalXp + :xpDelta WHERE id = 1")
    suspend fun addXp(xpDelta: Int)

    @Query("UPDATE user_progress SET completedLessonsCount = completedLessonsCount + 1 WHERE id = 1")
    suspend fun incrementCompletedLessons()

    @Query("UPDATE user_progress SET completedQuizzesCount = completedQuizzesCount + 1 WHERE id = 1")
    suspend fun incrementCompletedQuizzes()

    @Query("UPDATE user_progress SET completedChallengesCount = completedChallengesCount + 1 WHERE id = 1")
    suspend fun incrementCompletedChallenges()

    @Query("UPDATE user_progress SET studentName = :name WHERE id = 1")
    suspend fun updateStudentName(name: String)

    @Query("UPDATE user_progress SET isDarkMode = :isDark WHERE id = 1")
    suspend fun updateThemeMode(isDark: Boolean)

    @Query("UPDATE user_progress SET speechRate = :rate WHERE id = 1")
    suspend fun updateSpeechRate(rate: Float)

    @Query("UPDATE user_progress SET currentStreak = :streak, lastActiveDate = :dateStr WHERE id = 1")
    suspend fun updateStreak(streak: Int, dateStr: String)

    @Query("UPDATE user_progress SET isLoggedIn = :isLoggedIn, username = :username, email = :email, studentName = :fullName, role = :role, serverUserId = :userId, adminToken = :adminToken, lastSyncTimestamp = :syncTime WHERE id = 1")
    suspend fun updateAuthSession(isLoggedIn: Boolean, username: String, email: String, fullName: String, role: String, userId: Long, adminToken: String, syncTime: Long)

    @Query("UPDATE user_progress SET serverUrl = :serverUrl WHERE id = 1")
    suspend fun updateServerUrl(serverUrl: String)

    @Query("UPDATE user_progress SET totalXp = :xp, completedLessonsCount = :completedLessons, currentStreak = :streak WHERE id = 1")
    suspend fun updateStats(xp: Int, completedLessons: Int, streak: Int)

    @Query("UPDATE user_progress SET lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastSync(timestamp: Long)

    @Query("UPDATE user_progress SET isLoggedIn = 0, username = '', email = '', role = 'STUDENT', serverUserId = 0, adminToken = '' WHERE id = 1")
    suspend fun clearAuthSession()
}

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificates ORDER BY id DESC")
    fun getAllCertificatesFlow(): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates WHERE courseSlug = :slug LIMIT 1")
    suspend fun getCertificateByCourseSlug(slug: String): CertificateEntity?

    @Query("SELECT * FROM certificates WHERE courseSlug = :slug LIMIT 1")
    fun getCertificateByCourseSlugFlow(slug: String): Flow<CertificateEntity?>

    @Query("SELECT * FROM certificates WHERE verificationId = :id OR sha256Signature = :id LIMIT 1")
    suspend fun getCertificateByVerificationId(id: String): CertificateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(cert: CertificateEntity): Long
}

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets ORDER BY createdTimestamp DESC")
    fun getAllSnippetsFlow(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE languageSlug = :languageSlug ORDER BY createdTimestamp DESC")
    fun getSnippetsForLanguageFlow(languageSlug: String): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SnippetEntity): Long

    @Delete
    suspend fun deleteSnippet(snippet: SnippetEntity)
}
