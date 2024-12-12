package com.example.autograde.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user_answers")
@Parcelize
data class UserAnswer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userTestId: String,
    val questionId: String,
    val answer: String? = null,
    val isBookmarked: Boolean = false,
    val remainingTime: Long = 0L,
    val sequence: Int,
) : Parcelable

