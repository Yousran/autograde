package com.example.autograde.data.api.response

import com.google.gson.annotations.SerializedName

data class UpdateUserGradeResponse(

	@field:SerializedName("userTest")
	val userTest: UserTest? = null,

	@field:SerializedName("answer")
	val answer: AnswersItem? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UpdateAnswer (
	val answerId : String,
	val grade : Int
)
