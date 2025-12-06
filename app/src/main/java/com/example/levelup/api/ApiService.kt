package com.example.levelup.api

import com.example.levelup.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth endpoints
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserCreateResponse>
    
    // Users endpoints
    @GET("api/v1/users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<UserModel>>
    
    @POST("api/v1/users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body user: RegisterRequest
    ): Response<UserCreateResponse>
    
    @PUT("api/v1/users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body user: UserModel
    ): Response<UserUpdateResponse>
    
    @DELETE("api/v1/users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<UserDeleteResponse>
    
    // Categories endpoints
    @GET("api/v1/categories")
    suspend fun getCategories(@Header("Authorization") token: String): Response<List<CategoryModel>>
    
    @POST("api/v1/categories")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Body category: CategoryModel
    ): Response<CategoryModel>
    
    @PUT("api/v1/categories/{id}")
    suspend fun updateCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body category: CategoryModel
    ): Response<CategoryModel>
    
    @DELETE("api/v1/categories/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>
    
    // Platforms endpoints
    @GET("api/v1/platforms")
    suspend fun getPlatforms(@Header("Authorization") token: String): Response<List<PlatformModel>>
    
    @POST("api/v1/platforms")
    suspend fun createPlatform(
        @Header("Authorization") token: String,
        @Body platform: PlatformModel
    ): Response<PlatformModel>
    
    @PUT("api/v1/platforms/{id}")
    suspend fun updatePlatform(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body platform: PlatformModel
    ): Response<PlatformModel>
    
    @DELETE("api/v1/platforms/{id}")
    suspend fun deletePlatform(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>
    
    // Products endpoints
    @GET("api/v1/products")
    suspend fun getProducts(@Header("Authorization") token: String): Response<List<ProductModel>>
    
    @POST("api/v1/products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body product: ProductModel
    ): Response<ProductModel>
    
    @PUT("api/v1/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body product: ProductModel
    ): Response<ProductModel>
    
    @DELETE("api/v1/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    // --- Carts (fakestoreapi.com) ---
    @GET("https://fakestoreapi.com/carts")
    suspend fun getAllCarts(): Response<List<com.example.levelup.model.Carrito>>

    @GET("https://fakestoreapi.com/carts/{id}")
    suspend fun getCartById(@Path("id") id: Long): Response<com.example.levelup.model.Carrito>

    @POST("https://fakestoreapi.com/carts")
    suspend fun createCart(@Body cart: com.example.levelup.model.Carrito): Response<com.example.levelup.model.Carrito>

    @PUT("https://fakestoreapi.com/carts/{id}")
    suspend fun updateCart(@Path("id") id: Long, @Body cart: com.example.levelup.model.Carrito): Response<com.example.levelup.model.Carrito>

    @DELETE("https://fakestoreapi.com/carts/{id}")
    suspend fun deleteCart(@Path("id") id: Long): Response<Unit>
}
