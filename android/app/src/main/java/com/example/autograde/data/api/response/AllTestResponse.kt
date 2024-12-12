package com.example.autograde.data.api.response

import com.google.gson.annotations.SerializedName

data class AllTestResponse(

	@field:SerializedName("tests")
	val tests: List<TestsItem?>? = null
)

data class TestsItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("acceptResponses")
	val acceptResponses: Boolean? = null,

	@field:SerializedName("creatorId")
	val creatorId: String? = null,

	@field:SerializedName("questions")
	val questions: List<QuestionsItemTest?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("testTitle")
	val testTitle: String? = null,

	@field:SerializedName("testDuration")
	val testDuration: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class QuestionsItemTest(

	@field:SerializedName("answerText")
	val answerText: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("questionText")
	val questionText: String? = null
)
