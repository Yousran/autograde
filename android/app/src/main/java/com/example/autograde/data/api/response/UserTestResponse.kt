package com.example.autograde.data.api.response

import com.google.gson.annotations.SerializedName

data class UserTestResponse(

	@field:SerializedName("userTest")
	val userTest: UserTest? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class AnswersItem(

	@field:SerializedName("userTestId")
	val userTestId: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("questionId")
	val questionId: String? = null,

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("question")
	val question: QuestionUser? = null,

	@field:SerializedName("grade")
	val grade: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class UserTest(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("totalGrade")
	val totalGrade: Int? = null,

	@field:SerializedName("answers")
	val answers: List<AnswersItem?>? = null,

	@field:SerializedName("testId")
	val testId: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("timeLeft")
	val timeLeft: Int? = null,

	@field:SerializedName("testDate")
	val testDate: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class QuestionUser(

	@field:SerializedName("questionText")
	val questionText: String? = null
)

data class Request (
	val userTestId : String
)
