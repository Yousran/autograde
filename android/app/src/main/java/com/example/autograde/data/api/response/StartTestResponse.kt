package com.example.autograde.data.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartTestResponse(

	@field:SerializedName("userTestId")
	val userTestId: String? = null,

	@field:SerializedName("test")
	val test: TestStart? = null,

	@field:SerializedName("message")
	val message: String? = null

) : Parcelable

@Parcelize
data class StartQuestionsItem(

	@field:SerializedName("testId")
	val testId: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("questionText")
	val questionText: String? = null
) : Parcelable

@Parcelize
data class TestStart(

	@field:SerializedName("acceptResponses")
	val acceptResponses: Boolean? = null,

	@field:SerializedName("questions")
	val questions: List<StartQuestionsItem?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("testTitle")
	val testTitle: String? = null,

	@field:SerializedName("testDuration")
	val testDuration: Int? = null

) : Parcelable

data class TestRequest (
	val testId: String
)

data class TestRequestForGuest (
	val testId: String,
	val username : String
)


