package com.eduhub.android.data.local.dao

import androidx.room.*
import com.eduhub.android.data.local.entities.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges WHERE courseSlug = :courseSlug LIMIT 1")
    suspend fun getChallengeForCourseSlug(courseSlug: String): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE courseSlug = :courseSlug LIMIT 1")
    fun getChallengeForCourseSlugFlow(courseSlug: String): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: Long): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE isSolved = 0 LIMIT 1")
    suspend fun getNextUnsolvedChallenge(): ChallengeEntity?

    @Query("SELECT * FROM challenges ORDER BY isSolved ASC, id ASC")
    fun getAllChallengesFlow(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(challenges: List<ChallengeEntity>)

    @Query("UPDATE challenges SET isSolved = 1, solvedTimestamp = :timestamp WHERE id = :challengeId")
    suspend fun markChallengeSolved(challengeId: Long, timestamp: Long)
}
