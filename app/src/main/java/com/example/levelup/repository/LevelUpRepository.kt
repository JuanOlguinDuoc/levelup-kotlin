package com.example.levelup.repository

import android.content.Context
import com.example.levelup.api.ApiClient
import com.example.levelup.api.ApiService
import com.example.levelup.auth.AuthManager
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class LevelUpRepository(context: Context? = null, private val apiService: ApiService = ApiClient.apiService) {
    private val dataStoreManager: DataStoreManager? = context?.let { DataStoreManager(it) }
    private val authManager: AuthManager? = context?.let { AuthManager(it) }
    
    // helper utilities available through imports
    
    // Helper function to handle API calls
    private suspend fun <T> apiCall(call: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                // Obtener el cuerpo del error para más detalles
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                ApiResult.Error(Exception("API Error: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    private suspend fun getAuthToken(): String? {
        return authManager?.getToken()?.first()
    }
    
    // Auth methods
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        val result = apiCall { 
            apiService.login(LoginRequest(email, password)) 
        }
        
        if (result is ApiResult.Success) {
            // Verificar que el token no sea nulo (credenciales válidas)
            if (result.data.token != null && result.data.email != null) {
                authManager?.saveToken(result.data.token)
                authManager?.saveUserEmail(result.data.email)
                return result
            } else {
                // El backend respondió 200 pero con credenciales inválidas
                return ApiResult.Error(Exception(result.data.message))
            }
        }
        
        return result
    }
    
    suspend fun register(request: RegisterRequest): ApiResult<UserCreateResponse> {
        return apiCall { apiService.register(request) }
    }
    
    suspend fun logout() {
        authManager?.clearAuth()
    }
    
    // Users methods
    suspend fun getUsers(): Flow<ApiResult<List<UserModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = getAuthToken()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.getUsers(bearer)
        }
        
        if (result is ApiResult.Success) {
            // Guarda automáticamente en el almacenamiento local
            dataStoreManager?.saveUsers(result.data)
        }
        
        emit(result)
    }
    
    // Para datos locales (offline)
    fun getUsersFromLocal(): Flow<List<UserModel>> {
        return dataStoreManager?.users ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    suspend fun createUser(user: RegisterRequest): ApiResult<UserCreateResponse> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.createUser(bearer, user)
        }
        
        if (result is ApiResult.Success) {
            // Refresh local data
            getUsers().collect {} // This will update local storage
        }
        
        return result
    }
    
    suspend fun updateUser(id: Long, user: UserModel): ApiResult<UserUpdateResponse> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.updateUser(bearer, id, user)
        }
        
        if (result is ApiResult.Success) {
            // Refresh local data
            getUsers().collect {}
        }
        
        return result
    }
    
    suspend fun deleteUser(id: Long): ApiResult<UserDeleteResponse> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.deleteUser(bearer, id)
        }
        
        if (result is ApiResult.Success) {
            // Refresh local data
            getUsers().collect {}
        }
        
        return result
    }
    
    // Categories methods
    suspend fun getCategories(): Flow<ApiResult<List<CategoryModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = getAuthToken()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.getCategories(bearer)
        }
        
        if (result is ApiResult.Success) {
            dataStoreManager?.saveCategories(result.data)
        }
        
        emit(result)
    }
    
    fun getCategoriesFromLocal(): Flow<List<CategoryModel>> {
        return dataStoreManager?.categories ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    suspend fun createCategory(category: CategoryModel): ApiResult<CategoryModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.createCategory(bearer, category)
        }
        
        if (result is ApiResult.Success) {
            getCategories().collect {}
        }
        
        return result
    }
    
    suspend fun updateCategory(id: Long, category: CategoryModel): ApiResult<CategoryModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.updateCategory(bearer, id, category)
        }
        
        if (result is ApiResult.Success) {
            getCategories().collect {}
        }
        
        return result
    }
    
    suspend fun deleteCategory(id: Long): ApiResult<Unit> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.deleteCategory(bearer, id)
        }
        
        if (result is ApiResult.Success) {
            getCategories().collect {}
        }
        
        return result
    }
    
    // Platforms methods
    suspend fun getPlatforms(): Flow<ApiResult<List<PlatformModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = getAuthToken()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.getPlatforms(bearer)
        }
        
        if (result is ApiResult.Success) {
            dataStoreManager?.savePlatform(result.data)
        }
        
        emit(result)
    }
    
    fun getPlatformsFromLocal(): Flow<List<PlatformModel>> {
        return dataStoreManager?.platforms ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    suspend fun createPlatform(platform: PlatformModel): ApiResult<PlatformModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.createPlatform(bearer, platform)
        }
        
        if (result is ApiResult.Success) {
            getPlatforms().collect {}
        }
        
        return result
    }
    
    suspend fun updatePlatform(id: Long, platform: PlatformModel): ApiResult<PlatformModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.updatePlatform(bearer, id, platform)
        }
        
        if (result is ApiResult.Success) {
            getPlatforms().collect {}
        }
        
        return result
    }
    
    suspend fun deletePlatform(id: Long): ApiResult<Unit> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.deletePlatform(bearer, id)
        }
        
        if (result is ApiResult.Success) {
            getPlatforms().collect {}
        }
        
        return result
    }
    
    // Products methods
    suspend fun getProducts(): Flow<ApiResult<List<ProductModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = getAuthToken()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.getProducts(bearer)
        }
        
        if (result is ApiResult.Success) {
            dataStoreManager?.saveProducts(result.data)
        }
        
        emit(result)
    }
    
    fun getProductsFromLocal(): Flow<List<ProductModel>> {
        return dataStoreManager?.products ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    suspend fun createProduct(product: ProductModel): ApiResult<ProductModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.createProduct(bearer, product)
        }
        
        if (result is ApiResult.Success) {
            getProducts().collect {}
        }
        
        return result
    }
    
    suspend fun updateProduct(id: Long, product: ProductModel): ApiResult<ProductModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.updateProduct(bearer, id, product)
        }
        
        if (result is ApiResult.Success) {
            getProducts().collect {}
        }
        
        return result
    }
    
    suspend fun deleteProduct(id: Long): ApiResult<Unit> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall {
            apiService.deleteProduct(bearer, id)
        }
        
        if (result is ApiResult.Success) {
            getProducts().collect {}
        }
        
        return result
    }

    // --- Carts methods (fakestoreapi.com) ---
    suspend fun getAllCarts(): ApiResult<List<com.example.levelup.model.Carrito>> {
        val result = apiCall { apiService.getAllCarts() }
        return result
    }

    suspend fun getCartById(id: Long): ApiResult<com.example.levelup.model.Carrito> {
        val result = apiCall { apiService.getCartById(id) }
        return result
    }

    suspend fun createCart(cart: com.example.levelup.model.Carrito): ApiResult<com.example.levelup.model.Carrito> {
        val result = apiCall { apiService.createCart(cart) }
        return result
    }

    suspend fun updateCart(id: Long, cart: com.example.levelup.model.Carrito): ApiResult<com.example.levelup.model.Carrito> {
        val result = apiCall { apiService.updateCart(id, cart) }
        return result
    }

    suspend fun deleteCart(id: Long): ApiResult<Unit> {
        val result = apiCall { apiService.deleteCart(id) }
        return result
    }
}
