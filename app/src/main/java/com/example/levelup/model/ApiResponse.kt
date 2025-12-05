package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String?,
    val email: String?,
    val message: String
)

@Serializable
data class RegisterRequest(
    val run: String,
    val firstName: String,  
    val lastName: String,   
    val email: String,      
    val password: String,
    val role: String = "usuario"  // Rol por defecto
)

@Serializable
data class UserCreateResponse(
    val message: String,
    val user: UserModel? = null,
    val error: String? = null
)

@Serializable
data class UserUpdateResponse(
    val message: String,
    val user: UserModel? = null,
    val error: String? = null
)

@Serializable
data class UserDeleteResponse(
    val message: String,
    val error: String? = null
)

@Serializable
data class ApiError(
    val message: String,
    val error: String? = null
)

// Wrapper genérico para respuestas de la API
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Throwable) : ApiResult<Nothing>()
    data class Loading(val isLoading: Boolean = true) : ApiResult<Nothing>()
}
