package com.faizalwiyatnodito.tokoonlineumkm.data.network

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("products")
    suspend fun getProducts(
        @Query("category_id") categoryId: Int? = null,
        @Query("search") search: String? = null
    ): Response<ProductListResponse>

    @GET("categories")
    suspend fun getCategories(): Response<CategoryListResponse>

    @GET("carts")
    suspend fun getCarts(@Query("user_id") userId: Int): Response<CartResponse>

    @FormUrlEncoded
    @POST("carts")
    suspend fun addToCart(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int
    ): Response<SimpleResponse>

    @FormUrlEncoded
    @PUT("carts/{id}")
    suspend fun updateCart(
        @Path("id") id: Int,
        @Field("quantity") quantity: Int
    ): Response<SimpleResponse>

    @DELETE("carts/{id}")
    suspend fun deleteCart(@Path("id") id: Int): Response<SimpleResponse>

    @GET("orders")
    suspend fun getOrders(@Query("user_id") userId: Int): Response<OrderListResponse>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<UserResponse>
}
