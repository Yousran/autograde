package com.example.autograde.data.api.response

import com.google.gson.annotations.SerializedName

data class PastTestDetailResponse(

	@field:SerializedName("totalGrade")
	val totalGrade: Int? = null,

	@field:SerializedName("results")
	val results: List<ResultsItemPast?>? = null,

	@field:SerializedName("testTitle")
	val testTitle: String? = null
)

data class ResultsItemPast(

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("grade")
	val grade: Int? = null
)
