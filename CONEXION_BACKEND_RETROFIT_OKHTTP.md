# Conexión Backend con Retrofit y OkHttp en LevelUp

Este documento detalla la implementación de la conexión con el backend utilizando Retrofit y OkHttp en la aplicación LevelUp Android.

## Tabla de Contenidos

1. [Arquitectura de Red](#arquitectura-de-red)
2. [Configuración HTTP Client (OkHttp)](#configuración-http-client-okhttp)
3. [Configuración Retrofit](#configuración-retrofit)
4. [Definición de Endpoints (ApiService)](#definición-de-endpoints-apiservice)
5. [Gestión de Autenticación (AuthManager)](#gestión-de-autenticación-authmanager)
6. [Repository Pattern](#repository-pattern)
7. [Manejo de Errores](#manejo-de-errores)
8. [Serialización JSON](#serialización-json)
9. [Casos de Uso Prácticos](#casos-de-uso-prácticos)

## Arquitectura de Red

### Stack Tecnológico

- **OkHttp**: Cliente HTTP para comunicación de red
- **Retrofit**: Cliente REST tipado para Android/Java
- **kotlinx.serialization**: Serialización/deserialización JSON
- **DataStore**: Persistencia local de tokens de autenticación
- **Coroutines**: Programación asíncrona con manejo de flujos

### Diagrama de Capas

```
┌─────────────────────────────────┐
│       UI Layer (Compose)        │
│     ViewModel + State           │
└─────────────────────────────────┘
                 │
┌─────────────────────────────────┐
│      Repository Pattern         │
│   LevelUpRepository.kt          │
└─────────────────────────────────┘
                 │
┌─────────────────────────────────┐
│     Network Layer (Retrofit)    │
│      ApiService.kt              │
└─────────────────────────────────┘
                 │
┌─────────────────────────────────┐
│     HTTP Client (OkHttp)        │
│      ApiClient.kt               │
└─────────────────────────────────┘
                 │
┌─────────────────────────────────┐
│     Backend API / External APIs │
│   AWS Backend + Fake Store API  │
└─────────────────────────────────┘
```

## Configuración HTTP Client (OkHttp)

### ApiClient.kt - Configuración Base

```kotlin
object ApiClient {
    private const val BASE_URL = "https://api-gateway-levelup-410014354071.us-east-1.elb.amazonaws.com/"
    
    // Cliente OkHttp con configuración personalizada
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY  // Logs completos en debug
            } else {
                HttpLoggingInterceptor.Level.NONE  // Sin logs en producción
            }
        })
        .connectTimeout(30, TimeUnit.SECONDS)    // Timeout de conexión
        .readTimeout(30, TimeUnit.SECONDS)       // Timeout de lectura
        .writeTimeout(30, TimeUnit.SECONDS)      // Timeout de escritura
        .build()
    
    // Instancia Retrofit configurada
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true      // Ignora campos desconocidos
                isLenient = true             // JSON más permisivo
                encodeDefaults = false       // No serializa valores por defecto
            }.asConverterFactory("application/json".toMediaType())
        )
        .build()
    
    // Servicio API singleton
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
```

### Características Clave del Cliente HTTP

1. **Logging Interceptor**:
   - Debug: Logs completos de request/response
   - Producción: Sin logs por seguridad

2. **Timeouts Configurados**:
   - Connect: 30 segundos
   - Read/Write: 30 segundos cada uno

3. **Gestión de JSON**:
   - Ignora campos desconocidos
   - Modo lenient para flexibilidad
   - No serializa valores por defecto

## Configuración Retrofit

### Convertidor de JSON

```kotlin
// kotlinx.serialization como convertidor principal
.addConverterFactory(
    Json {
        ignoreUnknownKeys = true      // Tolerancia a cambios en API
        isLenient = true             // Acepta JSON malformado
        encodeDefaults = false       // Optimización de payload
    }.asConverterFactory("application/json".toMediaType())
)
```

### Beneficios de kotlinx.serialization

- **Performance**: Más rápido que Gson/Moshi
- **Type Safety**: Verificación en tiempo de compilación
- **Multiplatform**: Compatible con Kotlin Multiplatform
- **Annotation Based**: Configuración declarativa

## Definición de Endpoints (ApiService)

### ApiService.kt - Interface REST

```kotlin
interface ApiService {
    
    // =============================================================================
    // AUTENTICACIÓN
    // =============================================================================
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("auth/register") 
    suspend fun register(@Body request: RegisterRequest): Response<UserCreateResponse>
    
    // =============================================================================
    // USUARIOS (CRUD Completo)
    // =============================================================================
    
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): Response<List<UserModel>>
    
    @POST("users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body user: RegisterRequest
    ): Response<UserCreateResponse>
    
    @PUT("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body user: UserUpdateRequest
    ): Response<UserUpdateResponse>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<UserDeleteResponse>
    
    // =============================================================================
    // VIDEOJUEGOS (CRUD Completo)
    // =============================================================================
    
    @GET("videogames")
    suspend fun getVideoGames(@Header("Authorization") token: String): Response<List<VideoGameModel>>
    
    @POST("videogames")
    suspend fun createVideoGame(
        @Header("Authorization") token: String,
        @Body videoGame: VideoGameCreateRequest
    ): Response<VideoGameCreateResponse>
    
    @PUT("videogames/{id}")
    suspend fun updateVideoGame(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body videoGame: VideoGameUpdateRequest
    ): Response<VideoGameUpdateResponse>
    
    @DELETE("videogames/{id}")
    suspend fun deleteVideoGame(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<VideoGameDeleteResponse>
    
    // =============================================================================
    // API EXTERNA - FAKE STORE API
    // =============================================================================
    
    @GET("https://fakestoreapi.com/products")
    suspend fun getProducts(): Response<List<ProductModel>>
    
    @GET("https://fakestoreapi.com/products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductModel>
}
```

### Patrones de Implementación

1. **Headers de Autenticación**: Cada endpoint protegido recibe `@Header("Authorization")`
2. **Response Wrapper**: Uso de `Response<T>` para manejo completo de HTTP
3. **Path Parameters**: `@Path` para IDs dinámicos en URLs
4. **Body Serialization**: `@Body` para payloads JSON automáticos
5. **External APIs**: URLs completas para APIs externas

## Gestión de Autenticación (AuthManager)

### AuthManager.kt - Token Management

```kotlin
class AuthManager(context: Context) {
    private val dataStore = context.dataStore
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }
    
    // Guardar token de autenticación
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    // Obtener token como Flow reactivo
    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }
    
    // Formatear token para headers HTTP
    fun getBearerToken(token: String): String {
        return "Bearer $token"
    }
    
    // Limpiar autenticación completa
    suspend fun clearAuth() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
    }
    
    // Gestión de email del usuario
    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }
    
    fun getUserEmail(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }
}
```

### Características del AuthManager

1. **DataStore Integration**: Persistencia reactiva y type-safe
2. **Flow-based**: Observación reactiva de cambios de autenticación
3. **Bearer Token Format**: Formateo automático para headers HTTP
4. **Complete Auth Management**: Token + información de usuario

## Repository Pattern

### LevelUpRepository.kt - Capa de Abstracción

```kotlin
class LevelUpRepository(
    context: Context? = null, 
    private val apiService: ApiService = ApiClient.apiService
) {
    private val dataStoreManager: DataStoreManager? = context?.let { DataStoreManager(it) }
    private val authManager: AuthManager? = context?.let { AuthManager(it) }
    
    // =============================================================================
    // HELPER PARA LLAMADAS API
    // =============================================================================
    
    private suspend fun <T> apiCall(call: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                ApiResult.Error(Exception("API Error: ${response.code()} ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    
    // =============================================================================
    // AUTENTICACIÓN
    // =============================================================================
    
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        val result = apiCall { 
            apiService.login(LoginRequest(email, password)) 
        }
        
        if (result is ApiResult.Success) {
            // Verificar credenciales válidas
            if (result.data.token != null && result.data.email != null) {
                authManager?.saveToken(result.data.token)
                authManager?.saveUserEmail(result.data.email)
                return result
            } else {
                return ApiResult.Error(Exception(result.data.message))
            }
        }
        return result
    }
    
    // =============================================================================
    // USUARIOS CON CACHE LOCAL
    // =============================================================================
    
    suspend fun getUsers(): Flow<ApiResult<List<UserModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = getAuthToken()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
        val result = apiCall { apiService.getUsers(bearer) }
        
        if (result is ApiResult.Success) {
            // Cache automático en DataStore
            dataStoreManager?.saveUsers(result.data)
        }
        
        emit(result)
    }
    
    // Acceso a cache local (offline)
    fun getUsersFromLocal(): Flow<List<UserModel>> {
        return dataStoreManager?.users ?: flow { emit(emptyList()) }
    }
}
```

### Patrones del Repository

1. **Error Handling Centralizado**: `apiCall()` wrapper para manejo uniforme
2. **Token Management**: Inyección automática de headers de autenticación
3. **Cache Strategy**: Datos remotos + cache local automático
4. **Flow-based**: APIs reactivas con estados Loading/Success/Error
5. **Offline Support**: Fallback a datos locales cuando no hay red

## Manejo de Errores

### ApiResult Sealed Class

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()
    data class Loading(val isLoading: Boolean = true) : ApiResult<Nothing>()
}
```

### Estrategia de Manejo de Errores

1. **HTTP Status Codes**: Diferenciación entre éxito (2xx) y error (4xx/5xx)
2. **Error Body Parsing**: Extracción de mensajes detallados del backend
3. **Network Exceptions**: Manejo de timeouts y errores de conectividad
4. **Authentication Errors**: Detección automática de tokens expirados
5. **User-Friendly Messages**: Conversión de errores técnicos a mensajes comprensibles

### Ejemplo de Manejo de Error Completo

```kotlin
// En ViewModel
viewModelScope.launch {
    repository.getUsers().collect { result ->
        when (result) {
            is ApiResult.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
            is ApiResult.Success -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        users = result.data,
                        error = null
                    )
                }
            }
            is ApiResult.Error -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = when {
                            result.exception.message?.contains("401") == true -> 
                                "Sesión expirada. Por favor, inicia sesión nuevamente."
                            result.exception.message?.contains("NetworkException") == true -> 
                                "Sin conexión a internet. Verifica tu red."
                            else -> 
                                "Error inesperado: ${result.exception.message}"
                        }
                    )
                }
            }
        }
    }
}
```

## Serialización JSON

### Configuración kotlinx.serialization

```kotlin
// En build.gradle.kts (Module: app)
plugins {
    kotlin("plugin.serialization") version "1.8.10"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
}
```

### Modelos de Datos Serializables

```kotlin
@Serializable
data class UserModel(
    @SerialName("id") val id: Int,
    @SerialName("nombre") val nombre: String,
    @SerialName("email") val email: String,
    @SerialName("edad") val edad: Int,
    @SerialName("fechaCreacion") val fechaCreacion: String? = null,
    @SerialName("fechaActualizacion") val fechaActualizacion: String? = null
)

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("message") val message: String,
    @SerialName("token") val token: String? = null,
    @SerialName("email") val email: String? = null
)
```

### Ventajas de la Serialización

1. **Type Safety**: Verificación en tiempo de compilación
2. **Performance**: Generación de código optimizada
3. **Null Safety**: Manejo explícito de campos opcionales
4. **Custom Names**: `@SerialName` para mapeo flexible
5. **Default Values**: Valores por defecto para campos opcionales

## Casos de Uso Prácticos

### 1. Autenticación y Gestión de Sesión

```kotlin
// En LoginViewModel
suspend fun login(email: String, password: String) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        when (val result = repository.login(email, password)) {
            is ApiResult.Success -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = result.data
                    )
                }
                // Navegación automática tras login exitoso
                navigationCallback?.invoke()
            }
            is ApiResult.Error -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }
}
```

### 2. Operaciones CRUD con Cache

```kotlin
// En UsersViewModel
private fun loadUsers() {
    viewModelScope.launch {
        // Cargar datos del cache local primero
        repository.getUsersFromLocal().collect { localUsers ->
            _uiState.update { it.copy(users = localUsers) }
        }
        
        // Luego actualizar desde red
        repository.getUsers().collect { result ->
            when (result) {
                is ApiResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            users = result.data,
                            isLoading = false,
                            lastSyncTime = System.currentTimeMillis()
                        )
                    }
                }
                is ApiResult.Error -> {
                    // Mantener datos locales en caso de error
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            syncError = result.exception.message
                        )
                    }
                }
            }
        }
    }
}
```

### 3. Integración de APIs Externas

```kotlin
// En ProductsViewModel - Fake Store API
suspend fun loadProducts() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        when (val result = repository.getProducts()) {
            is ApiResult.Success -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        products = result.data
                    )
                }
            }
            is ApiResult.Error -> {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error cargando productos: ${result.exception.message}"
                    )
                }
            }
        }
    }
}
```

### 4. Manejo de Tokens Expirados

```kotlin
// En Repository - Auto-refresh de tokens
private suspend fun <T> authenticatedApiCall(call: suspend (String) -> Response<T>): ApiResult<T> {
    val token = getAuthToken()
    if (token == null) {
        return ApiResult.Error(Exception("No authentication token"))
    }
    
    val bearer = authManager?.getBearerToken(token) ?: "Bearer $token"
    val result = apiCall { call(bearer) }
    
    // Si el token expiró, limpiar autenticación
    if (result is ApiResult.Error && 
        result.exception.message?.contains("401") == true) {
        authManager?.clearAuth()
    }
    
    return result
}
```

## Best Practices y Recomendaciones

### 1. Configuración de Timeouts

- **Connect Timeout**: 15-30 segundos para conexión inicial
- **Read Timeout**: 30-60 segundos para operaciones lentas
- **Write Timeout**: 30 segundos para uploads

### 2. Logging en Producción

```kotlin
// Conditional logging based on build type
.addInterceptor(HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE  // IMPORTANTE: Sin logs en producción
    }
})
```

### 3. Error Handling Strategy

- **Network Errors**: Retry automático con exponential backoff
- **HTTP Errors**: Parsing específico del error body
- **Timeout Errors**: Fallback a cache local cuando sea posible
- **Authentication Errors**: Logout automático y redirección a login

### 4. Performance Optimization

```kotlin
// Connection pooling y keep-alive
private val okHttpClient = OkHttpClient.Builder()
    .connectionPool(ConnectionPool(10, 5, TimeUnit.MINUTES))
    .retryOnConnectionFailure(true)
    .build()
```

### 5. Security Considerations

- **Token Storage**: Usar DataStore encriptado o Android Keystore
- **Certificate Pinning**: Para producción con APIs críticas
- **Obfuscation**: Ofuscar URLs y endpoints en builds de release
- **Request Validation**: Validar inputs antes de enviar requests

---

## Conclusión

La implementación de Retrofit + OkHttp en LevelUp proporciona una arquitectura robusta y escalable para la comunicación con el backend. Los patrones implementados incluyen:

- **Configuración centralizada** del cliente HTTP
- **Manejo de errores** unificado y user-friendly
- **Cache strategy** híbrida (remote + local)
- **Authentication management** automático con DataStore
- **Type-safe serialization** con kotlinx.serialization
- **Reactive data flow** con Coroutines y Flow

Esta arquitectura permite mantener la aplicación responsive, manejar estados offline, y proporcionar una experiencia de usuario fluida incluso en condiciones de red adversas.

**Fecha de documentación**: 2024
**Versión de la aplicación**: 1.0.0
**Dependencias principales**: Retrofit 2.9.0, OkHttp 4.11.0, kotlinx.serialization 1.5.1
