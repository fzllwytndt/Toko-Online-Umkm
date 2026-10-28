package com.faizalwiyatnodito.tokoonlineumkm.data.network

import com.google.gson.annotations.SerializedName

// Seluruh respons backend memakai bentuk yang sama:
// { "status": true/false, "message": "...", "data": ... }
// Bagian "data" isinya berbeda-beda, jadi dibuatkan model masing-masing di bawah ini.

data class User(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("username")
    val username: String = "",

    @SerializedName("email")
    val email: String = "",

    @SerializedName("no_tlp")
    val noTlp: String = "",

    // Path relatif foto profil dari server, misalnya "uploads/avatars/avatar_1_123.png".
    // Bernilai null kalau pengguna belum punya foto profil.
    @SerializedName("avatar")
    val avatar: String? = null
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

data class UserResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: User? = null
)

// Dipakai untuk endpoint yang hanya perlu tahu berhasil atau gagal,
// misalnya menambah, mengubah, dan menghapus item keranjang.
data class SimpleResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = ""
)

data class Product(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("category_id")
    val categoryId: Int? = null,

    @SerializedName("category_name")
    val categoryName: String? = null,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("price")
    val price: Double = 0.0,

    @SerializedName("is_best_seller")
    val isBestSeller: Boolean = false,

    @SerializedName("is_available")
    val isAvailable: Boolean = true,

    @SerializedName("description")
    val description: String? = null
)

data class ProductListResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<Product> = emptyList()
)

data class Category(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = ""
)

data class CategoryListResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<Category> = emptyList()
)

data class CartItem(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("product_id")
    val productId: Int = 0,

    @SerializedName("product_name")
    val productName: String = "",

    @SerializedName("price")
    val price: Double = 0.0,

    @SerializedName("quantity")
    val quantity: Int = 0,

    @SerializedName("subtotal")
    val subtotal: Double = 0.0,

    @SerializedName("notes")
    val notes: String? = null,

    @SerializedName("is_available")
    val isAvailable: Boolean = true
)

data class CartData(
    @SerializedName("items")
    val items: List<CartItem> = emptyList(),

    @SerializedName("total_item")
    val totalItem: Int = 0,

    @SerializedName("total_harga")
    val totalHarga: Double = 0.0
)

data class CartResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: CartData? = null
)

data class OrderItem(
    @SerializedName("product_name")
    val productName: String = "",

    @SerializedName("quantity")
    val quantity: Int = 0,

    @SerializedName("subtotal")
    val subtotal: Double = 0.0
)

data class Order(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("order_type")
    val orderType: String = "",

    @SerializedName("payment_method")
    val paymentMethod: String = "",

    @SerializedName("total_amount")
    val totalAmount: Double = 0.0,

    @SerializedName("status")
    val status: String = "",

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("items")
    val items: List<OrderItem> = emptyList()
)

data class OrderListResponse(
    @SerializedName("status")
    val status: Boolean = false,

    @SerializedName("message")
    val message: String = "",

    @SerializedName("data")
    val data: List<Order> = emptyList()
)
