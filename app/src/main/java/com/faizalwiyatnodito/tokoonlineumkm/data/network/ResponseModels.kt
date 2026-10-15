package com.faizalwiyatnodito.tokoonlineumkm.data.network

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("username")
    val username: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("no_tlp")
    val noTlp: String = ""
)

data class LoginResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: User? = null
)

data class RegisterResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = ""
)
