package com.example.autograde.data.api.response

import com.google.gson.annotations.SerializedName

data class PastTestResponse(

	@field:SerializedName("tests")
	val tests: List<TestsItem?>? = null
)


