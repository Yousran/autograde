package com.example.autograde.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.autograde.data.local.entity.UserAnswer

@Dao
interface UserAnswerDao {
    @Query("SELECT * FROM user_answers WHERE questionId = :questionId LIMIT 1")
    suspend fun getAnswerByQuestionId(questionId: String): UserAnswer?

    @Query("SELECT * FROM user_answers WHERE userTestId = :userTestId AND sequence > 0 ORDER BY sequence")
    suspend fun getAllAnswersByUserTestId(userTestId: String): List<UserAnswer>

    @Query("SELECT MAX(sequence) FROM user_answers WHERE userTestId = :userTestId")
    suspend fun getMaxSequenceForUserTest(userTestId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: UserAnswer)

    @Update
    suspend fun updateAnswer(userAnswer: UserAnswer)

    // Fungsi untuk menghapus jawaban berdasarkan questionId
    @Query("DELETE FROM user_answers WHERE questionId = :questionId")
    suspend fun deleteAnswerByQuestionId(questionId: String)


    @Query("DELETE FROM user_answers WHERE userTestId = :userTestId")
    suspend fun deleteAllAnswersByUserTestId(userTestId: String)

    @Query("DELETE FROM user_answers")
    suspend fun deleteAllAnswers()

    // Query untuk mendapatkan semua soal yang di-bookmark
    @Query("SELECT * FROM user_answers WHERE isBookmarked = 1")
    suspend fun getBookmarkedQuestions(): List<UserAnswer>

    // Update status bookmark
    @Query("UPDATE user_answers SET isBookmarked = :isBookmarked WHERE questionId = :questionId")
    suspend fun updateBookmarkStatus(questionId: String, isBookmarked: Boolean)

    @Query("SELECT isBookmarked FROM user_answers WHERE questionId = :questionId")
    suspend fun isQuestionBookmarked(questionId: String): Boolean

    @Query("UPDATE user_answers SET answer = :answer WHERE questionId = :questionId")
    suspend fun updateAnswerByQuestionId(questionId: String, answer: String)

    @Query("UPDATE user_answers SET remainingTime = :remainingTime WHERE userTestId = :userTestId")
    suspend fun updateRemainingTime(userTestId: String, remainingTime: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialTimer(userAnswer: UserAnswer)

    @Query("SELECT remainingTime FROM user_answers WHERE userTestId = :userTestId LIMIT 1")
    fun getRemainingTime(userTestId: String): LiveData<Long>


    @Query("SELECT COUNT(*) FROM user_answers WHERE userTestId = :userTestId")
    suspend fun isTimerRunning(userTestId: String): Int



}
