package com.example.autograde.data.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val username: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val profilePictureUrl : String
) : Parcelable

