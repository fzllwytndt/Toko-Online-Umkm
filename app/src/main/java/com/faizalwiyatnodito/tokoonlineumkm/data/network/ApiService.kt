package com.faizalwiyatnodito.tokoonlineumkm.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

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

    // Memperbarui profil (nickname + foto). Dikirim sebagai multipart/form-data.
    // Bagian avatar boleh null kalau pengguna tidak mengganti fotonya.
    @Multipart
    @POST("users/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Part("username") username: RequestBody,
        @Part avatar: MultipartBody.Part?
    ): Response<UserResponse>

    // Mengunduh file gambar (foto profil) dari URL penuh, dipakai untuk menampilkan avatar.
    @Streaming
    @GET
    suspend fun unduhGambar(@Url url: String): Response<ResponseBody>
}
