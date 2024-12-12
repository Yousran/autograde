package com.example.autograde.data.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ProfileResponse(
	@field:SerializedName("googleId")
	val googleId: @RawValue Any? = null,

	@field:SerializedName("profilePictureUrl")
	val profilePictureUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("isVerified")
	val isVerified: Boolean? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("profilePictureCloudId")
	val profilePictureCloudId: @RawValue Any? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("googleToken")
	val googleToken: @RawValue Any? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: @RawValue Any? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable
