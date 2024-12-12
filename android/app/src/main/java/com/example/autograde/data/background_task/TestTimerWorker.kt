package com.example.autograde.data.background_task

import android.content.Context
import android.content.Intent
import androidx.work.WorkerParameters
import com.example.autograde.data.local.room.UserAnswerDao
import com.example.autograde.data.local.room.UserAnswerDatabase
import kotlinx.coroutines.delay
import androidx.work.CoroutineWorker
import com.example.autograde.data.local.entity.UserAnswer


class TestTimerWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val testId = inputData.getString("testId") ?: return Result.failure()
        val testDuration = inputData.getInt("testDuration", 0) // dalam menit

        val db = UserAnswerDatabase.getInstance(applicationContext)
        val userAnswerDao = db.userAnswerDao()

        val startTime = System.currentTimeMillis()
        val endTime = startTime + (testDuration * 60 * 1000L)

        val initialTimer = UserAnswer(
            userTestId = testId,
            questionId = "",
            remainingTime = endTime - System.currentTimeMillis(),
            sequence = 0
        )
        userAnswerDao.insertInitialTimer(initialTimer)

        while (System.currentTimeMillis() < endTime) {
            val remainingTime = endTime - System.currentTimeMillis()

            // Update waktu tersisa ke database
            userAnswerDao.updateRemainingTime(testId, remainingTime)

            // Tunggu 1 detik
            delay(1000)
        }

        // Timer selesai, kirim broadcast
        val intent = Intent("com.example.autograde.TIMER_FINISHED")
        applicationContext.sendBroadcast(intent)

        return Result.success()
    }
}

