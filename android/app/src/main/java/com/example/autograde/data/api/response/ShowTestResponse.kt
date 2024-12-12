package com.example.autograde.data.api.response

import com.google.gson.annotations.SerializedName

data class ShowTestResponse(

	@field:SerializedName("test")
	val test: TestShow? = null
)

data class QuestionsShow(

	@field:SerializedName("answerText")
	val answerText: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("questionText")
	val questionText: String? = null
)

data class TestShow(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("acceptResponses")
	val acceptResponses: Boolean? = null,

	@field:SerializedName("creatorId")
	val creatorId: String? = null,

	@field:SerializedName("questions")
	val questions: List<QuestionsShow>,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userTests")
	val userTests: List<UserTestsItem?>? = null,

	@field:SerializedName("testTitle")
	val testTitle: String? = null,

	@field:SerializedName("testDuration")
	val testDuration: Int? = null,

	@field:SerializedName("userTestCount")
	val userTestCount: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class UserTestsItem(

	@field:SerializedName("totalGrade")
	val totalGrade: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("timeLeft")
	val timeLeft: Int? = null,

	@field:SerializedName("testDate")
	val testDate: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
