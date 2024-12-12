package com.example.autograde.data.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class JoinTestResponse(

	@field:SerializedName("test")
	val test: Test? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
data class QuestionsItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("questionText")
	val questionText: String? = null
) : Parcelable


@Parcelize
data class Test(

	@field:SerializedName("questionCount")
	val questionCount: Int? = null,

	@field:SerializedName("acceptResponses")
	val acceptResponses: Boolean? = null,

	@field:SerializedName("questions")
	val questions: List<QuestionsItem?>? = null,

	@field:SerializedName("UserTestCount")
	val userTestCount: Int? = null,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("testTitle")
	val testTitle: String? = null,

	@field:SerializedName("testDuration")
	val testDuration: Int? = null
) : Parcelable


