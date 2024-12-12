package com.example.autograde.data.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class CreateTestResponse(

	@field:SerializedName("test")
	val test: Test? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class CreatedQuestionsItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("answerText")
	val answerText: String? = null,

	@field:SerializedName("testId")
	val testId: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("questionText")
	val questionText: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable

@Parcelize
data class CreatedTest(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("acceptResponses")
	val acceptResponses: Boolean? = null,

	@field:SerializedName("creatorId")
	val creatorId: String? = null,

	@field:SerializedName("questions")
	val questions: List<CreatedQuestionsItem?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("testTitle")
	val testTitle: String? = null,

	@field:SerializedName("testDuration")
	val testDuration: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable


data class AcceptResponse(
	@field:SerializedName("message")
	val message: String? = null,
)

data class AcceptResponseRequest(
	val id : String,
	val acceptResponses: Boolean?
)

data class CreateTestRequest (
	val testTitle : String,
	val testDuration : Int ,
	val questions : List<CreateQuestion>?
)

data class CreateQuestion(
	var questionText: String? = null,
	var answerText: String? = null
)
