package com.example.levelup.repository

import android.content.Context
import com.example.levelup.api.ApiClient
import com.example.levelup.auth.AuthManager
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class LevelUpRepository(context: Context) {
    
    private val apiService = ApiClient.apiService
    private val dataStoreManager = DataStoreManager(context)
    private val authManager = AuthManager(context)
    
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
        return authManager.getToken().first()
    }
    
    // Auth methods
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        val result = apiCall { 
            apiService.login(LoginRequest(email, password)) 
        }
        
        if (result is ApiResult.Success) {
            // Verificar que el token no sea nulo (credenciales válidas)
            if (result.data.token != null && result.data.email != null) {
                authManager.saveToken(result.data.token)
                authManager.saveUserEmail(result.data.email)
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
        authManager.clearAuth()
    }
    
    // Users methods
    suspend fun getUsers(): Flow<ApiResult<List<UserModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = getAuthToken()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val result = apiCall { 
            apiService.getUsers(authManager.getBearerToken(token)) 
        }
        
        if (result is ApiResult.Success) {
            // Guarda automáticamente en el almacenamiento local
            dataStoreManager.saveUsers(result.data)
        }
        
        emit(result)
    }
    
    // Para datos locales (offline)
    fun getUsersFromLocal(): Flow<List<UserModel>> {
        return dataStoreManager.users
    }
    
    suspend fun createUser(user: RegisterRequest): ApiResult<UserCreateResponse> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.createUser(authManager.getBearerToken(token), user) 
        }
        
        if (result is ApiResult.Success) {
            // Refresh local data
            getUsers().collect {} // This will update local storage
        }
        
        return result
    }
    
    suspend fun updateUser(id: Long, user: UserModel): ApiResult<UserUpdateResponse> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.updateUser(authManager.getBearerToken(token), id, user) 
        }
        
        if (result is ApiResult.Success) {
            // Refresh local data
            getUsers().collect {}
        }
        
        return result
    }
    
    suspend fun deleteUser(id: Long): ApiResult<UserDeleteResponse> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.deleteUser(authManager.getBearerToken(token), id) 
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
        
        val result = apiCall { 
            apiService.getCategories(authManager.getBearerToken(token)) 
        }
        
        if (result is ApiResult.Success) {
            dataStoreManager.saveCategories(result.data)
        }
        
        emit(result)
    }
    
    fun getCategoriesFromLocal(): Flow<List<CategoryModel>> {
        return dataStoreManager.categories
    }
    
    suspend fun createCategory(category: CategoryModel): ApiResult<CategoryModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.createCategory(authManager.getBearerToken(token), category) 
        }
        
        if (result is ApiResult.Success) {
            getCategories().collect {}
        }
        
        return result
    }
    
    suspend fun updateCategory(id: Long, category: CategoryModel): ApiResult<CategoryModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.updateCategory(authManager.getBearerToken(token), id, category) 
        }
        
        if (result is ApiResult.Success) {
            getCategories().collect {}
        }
        
        return result
    }
    
    suspend fun deleteCategory(id: Long): ApiResult<Unit> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.deleteCategory(authManager.getBearerToken(token), id) 
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
        
        val result = apiCall { 
            apiService.getPlatforms(authManager.getBearerToken(token)) 
        }
        
        if (result is ApiResult.Success) {
            dataStoreManager.savePlatform(result.data)
        }
        
        emit(result)
    }
    
    fun getPlatformsFromLocal(): Flow<List<PlatformModel>> {
        return dataStoreManager.platforms
    }
    
    suspend fun createPlatform(platform: PlatformModel): ApiResult<PlatformModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.createPlatform(authManager.getBearerToken(token), platform) 
        }
        
        if (result is ApiResult.Success) {
            getPlatforms().collect {}
        }
        
        return result
    }
    
    suspend fun updatePlatform(id: Long, platform: PlatformModel): ApiResult<PlatformModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.updatePlatform(authManager.getBearerToken(token), id, platform) 
        }
        
        if (result is ApiResult.Success) {
            getPlatforms().collect {}
        }
        
        return result
    }
    
    suspend fun deletePlatform(id: Long): ApiResult<Unit> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.deletePlatform(authManager.getBearerToken(token), id) 
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
        
        val result = apiCall { 
            apiService.getProducts(authManager.getBearerToken(token)) 
        }
        
        if (result is ApiResult.Success) {
            dataStoreManager.saveProducts(result.data)
        }
        
        emit(result)
    }
    
    fun getProductsFromLocal(): Flow<List<ProductModel>> {
        return dataStoreManager.products
    }
    
    suspend fun createProduct(product: ProductModel): ApiResult<ProductModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.createProduct(authManager.getBearerToken(token), product) 
        }
        
        if (result is ApiResult.Success) {
            getProducts().collect {}
        }
        
        return result
    }
    
    suspend fun updateProduct(id: Long, product: ProductModel): ApiResult<ProductModel> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.updateProduct(authManager.getBearerToken(token), id, product) 
        }
        
        if (result is ApiResult.Success) {
            getProducts().collect {}
        }
        
        return result
    }
    
    suspend fun deleteProduct(id: Long): ApiResult<Unit> {
        val token = getAuthToken() ?: return ApiResult.Error(Exception("No authentication token"))
        
        val result = apiCall { 
            apiService.deleteProduct(authManager.getBearerToken(token), id) 
        }
        
        if (result is ApiResult.Success) {
            getProducts().collect {}
        }
        
        return result
    }
}
