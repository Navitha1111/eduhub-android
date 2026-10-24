package com.eduhub.android.data.local.dao

import androidx.room.*
import com.eduhub.android.data.local.entities.QuizEntity
import com.eduhub.android.data.local.entities.QuizQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes WHERE courseSlug = :courseSlug LIMIT 1")
    suspend fun getQuizForCourseSlug(courseSlug: String): QuizEntity?

    @Query("SELECT * FROM quizzes WHERE courseSlug = :courseSlug LIMIT 1")
    fun getQuizForCourseSlugFlow(courseSlug: String): Flow<QuizEntity?>

    @Query("SELECT * FROM quizzes WHERE id = :id LIMIT 1")
    suspend fun getQuizById(id: Long): QuizEntity?

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY questionOrder ASC")
    suspend fun getQuestionsForQuiz(quizId: Long): List<QuizQuestionEntity>

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId ORDER BY questionOrder ASC")
    fun getQuestionsForQuizFlow(quizId: Long): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllQuizzes(quizzes: List<QuizEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllQuestions(questions: List<QuizQuestionEntity>)

    @Query("UPDATE quizzes SET bestScore = :bestScore, isPassed = :isPassed, completedTimestamp = :timestamp WHERE id = :quizId")
    suspend fun updateQuizResult(quizId: Long, bestScore: Int, isPassed: Boolean, timestamp: Long)

    @Query("SELECT * FROM quizzes WHERE isPassed = 0 LIMIT 1")
    suspend fun getNextUntakenQuiz(): QuizEntity?
}
