package com.example.autograde.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.autograde.data.local.entity.UserAnswer

@Database(entities = [UserAnswer::class], version = 1)
abstract class UserAnswerDatabase : RoomDatabase() {
    abstract fun userAnswerDao(): UserAnswerDao

    companion object {
        @Volatile
        private var INSTANCE: UserAnswerDatabase? = null

        // Fungsi singleton untuk mendapatkan instance database
        fun getInstance(context: Context): UserAnswerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserAnswerDatabase::class.java,
                    "user_answers_db"
                )
                    .fallbackToDestructiveMigration() // Menghapus data lama dan membuat ulang database
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
