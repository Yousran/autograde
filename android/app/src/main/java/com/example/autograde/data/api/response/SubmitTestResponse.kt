package com.example.autograde.data.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubmitTestResponse(

	@field:SerializedName("totalGrade")
	val totalGrade: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("results")
	val results: List<ResultsItem?>? = null,

	@field:SerializedName("testTitle")
	val testTitle: String? = null
) : Parcelable

@Parcelize
data class ResultsItem(

	@field:SerializedName("question")
	val question: Question? = null,

	@field:SerializedName("answer")
	val answer: Answer? = null
) : Parcelable


@Parcelize
data class Answer(

	@field:SerializedName("userTestId")
	val userTestId: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("questionId")
	val questionId: String? = null,

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("grade")
	val grade: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable


@Parcelize
data class Question(

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

data class SubmitTestRequest(
	val userTestId: String ,
	val questions: List<Answers>,
	val timeLeft : Int
)

data class Answers(
	val questionId: String,
	val answer: String
)

