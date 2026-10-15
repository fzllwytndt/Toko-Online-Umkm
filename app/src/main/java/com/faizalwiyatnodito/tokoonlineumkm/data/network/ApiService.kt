package com.faizalwiyatnodito.tokoonlineumkm.data.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("no_tlp") noTlp: String
    ): Response<RegisterResponse>
}
