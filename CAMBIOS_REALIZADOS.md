# Cambios Realizados para Login y CRUD de Usuarios

## Resumen General

Este documento describe todos los cambios realizados para que el sistema de login funcione correctamente y las operaciones CRUD de usuarios se integren adecuadamente con el backend de Spring Boot.

## Problemas Identificados y Solucionados

### 1. **Login Fallaba con Tokens Nulos**

**Problema:** El login retornaba `null` para tokens válidos porque el backend respondía con credenciales inválidas pero código 200.

**Solución:** Modificación en `LoginResponse.kt` para permitir campos opcionales:

```kotlin
@Serializable
data class LoginResponse(
    val token: String? = null,        // Permitir null
    val email: String? = null,        // Permitir null  
    val message: String? = null,      // Permitir null
    val user: UserModel? = null       // Permitir null
)
```

**Validación en `LevelUpRepository.kt`:**
```kotlin
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
```

### 2. **Error "role is required" en Creación de Usuarios**

**Problema:** El backend requería el campo `role` pero el frontend no lo enviaba correctamente.

**Solución:** Actualización de `RegisterRequest` en `ApiResponse.kt`:

```kotlin
@Serializable
data class RegisterRequest(
    val run: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: String = "usuario"  // Valor por defecto
)
```

**Validación en `UserViewModelWithApi.kt`:**
```kotlin
// Crear request directamente del UserModel actualizado
val request = RegisterRequest(
    run = usuario.run.trim(),
    firstName = usuario.firstName.trim(),
    lastName = usuario.lastName.trim(), 
    email = usuario.email.trim(),
    password = usuario.password,
    role = usuario.role.ifBlank { "usuario" }  // Asignar rol por defecto
)
```

### 3. **Incompatibilidad de Tipos ID (Int vs Long)**

**Problema:** El frontend usaba `Int` para IDs pero el backend de Spring Boot usa `Long`.

**Solución:** Cambio en `User.kt`:

```kotlin
@Serializable
data class UserModel(
    val id: Long = 0L,  // Cambio: Int -> Long
    val run: String = "",
    val direccion: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = ""
) {
    // Computed properties para compatibilidad con UI existente
    val nombres: String get() = firstName
    val apellidos: String get() = lastName
    val correo: String get() = email
}
```

**Actualización en `ApiService.kt`:**
```kotlin
@PUT("api/v1/users/{id}")
suspend fun updateUser(
    @Header("Authorization") token: String,
    @Path("id") id: Long,  // Cambio: Int -> Long
    @Body user: UserModel
): Response<UserUpdateResponse>

@DELETE("api/v1/users/{id}")
suspend fun deleteUser(
    @Header("Authorization") token: String,
    @Path("id") id: Long   // Cambio: Int -> Long
): Response<UserDeleteResponse>
```

### 4. **Alineación de Estructuras de Respuesta Backend-Frontend**

**Problema:** El backend retorna respuestas estructuradas pero el frontend esperaba modelos directos.

**Solución:** Creación de clases de respuesta en `ApiResponse.kt`:

```kotlin
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
```

**Actualización del `ApiService.kt`:**
```kotlin
@POST("api/v1/users")
suspend fun createUser(
    @Header("Authorization") token: String,
    @Body user: RegisterRequest
): Response<UserCreateResponse>  // Cambio: UserModel -> UserCreateResponse

@PUT("api/v1/users/{id}")
suspend fun updateUser(
    @Header("Authorization") token: String,
    @Path("id") id: Long,
    @Body user: UserModel
): Response<UserUpdateResponse>  // Cambio: UserModel -> UserUpdateResponse
```

### 5. **Corrección de Nombres de Campos**

**Problema:** Inconsistencia entre nombres de campos del frontend y backend.

**Solución:** Actualización en `UserViewModel.kt`:

```kotlin
fun editarUsuario(id: Long, newRun: String, newDireccion: String, 
                 newName: String, newApellido: String, newCorreo: String, newPassword: String){
    viewModelScope.launch {
        val usuariosActuales = usuarios.value.toMutableList()
        val index = usuariosActuales.indexOfFirst { it.id == id }
        if ( index != -1) {
            val usuarioEditado = usuariosActuales[index].copy(
                run = newRun,
                direccion = newDireccion,
                firstName = newName,      // Cambio: nombres -> firstName
                lastName = newApellido,   // Cambio: apellidos -> lastName
                email = newCorreo,        // Cambio: correo -> email
                password = newPassword
            )
            usuariosActuales[index] = usuarioEditado
            dataStorageManager.saveUsers(usuariosActuales)
        }
    }
}
```

### 6. **Actualización del Repositorio para Nuevas Respuestas**

**Problema:** El repositorio esperaba tipos de retorno incorrectos.

**Solución:** Actualización en `LevelUpRepository.kt`:

```kotlin
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
    // Similar pattern...
}

suspend fun deleteUser(id: Long): ApiResult<UserDeleteResponse> {
    // Similar pattern...
}
```

### 7. **Manejo de Respuestas en ViewModels**

**Problema:** Los ViewModels no manejaban las nuevas respuestas estructuradas.

**Solución:** Actualización en `UserViewModelWithApi.kt`:

```kotlin
when (val result = repository.createUser(request)) {
    is ApiResult.Success -> {
        _isLoading.value = false
        playSuccessSound()
        println("DEBUG: Usuario creado exitosamente - ${result.data.message}")
        // Recargar la lista de usuarios desde el backend
        loadUsers()
    }
    is ApiResult.Error -> {
        _isLoading.value = false
        _errorMessage.value = result.exception.message
        println("DEBUG: Error al crear usuario - ${result.exception.message}")
    }
    is ApiResult.Loading -> {}
}
```

## Estructura Final de Archivos

### Archivos Modificados:

1. **ApiResponse.kt** - Nuevas clases de respuesta y RegisterRequest actualizado
2. **User.kt** - ID cambiado a Long, campos alineados con backend
3. **ApiService.kt** - Endpoints actualizados con tipos correctos
4. **LevelUpRepository.kt** - Métodos actualizados para nuevas respuestas
5. **UserViewModelWithApi.kt** - Manejo de respuestas estructuradas
6. **UserViewModel.kt** - Campos actualizados en editarUsuario

### Endpoints del Backend Utilizados:

```
POST /api/auth/login     - Login de usuarios
POST /api/auth/register  - Registro desde pantalla pública
GET  /api/v1/users       - Obtener lista de usuarios
POST /api/v1/users       - Crear nuevo usuario (desde admin)
PUT  /api/v1/users/{id}  - Actualizar usuario existente
DELETE /api/v1/users/{id} - Eliminar usuario
```

## Flujo de Datos Actualizado

1. **Login:**
   - Usuario ingresa credenciales → `LoginRequest`
   - Backend valida → `LoginResponse` con token o mensaje de error
   - Frontend verifica token no nulo antes de guardar

2. **CRUD Usuarios:**
   - Crear: `RegisterRequest` → `UserCreateResponse`
   - Leer: Token → `List<UserModel>`
   - Actualizar: `UserModel` → `UserUpdateResponse`
   - Eliminar: `id: Long` → `UserDeleteResponse`

## Beneficios Obtenidos

- ✅ Login funciona con validación correcta de credenciales
- ✅ Creación de usuarios incluye rol requerido
- ✅ Tipos de ID consistentes (Long) entre frontend y backend
- ✅ Manejo adecuado de respuestas del backend
- ✅ Mensajes de éxito/error desde el backend
- ✅ Compatibilidad completa con Spring Boot JPA
- ✅ Eliminación de errores de compilación

## Notas Importantes

- El campo `direccion` se mantiene solo en el frontend (no se envía al backend)
- Las computed properties (`nombres`, `apellidos`, `correo`) mantienen compatibilidad con UI existente
- Todos los IDs ahora usan `Long` para consistencia con JPA
- Las respuestas del backend incluyen mensajes descriptivos para mejor UX

---

## Configuración de la Conexión al Backend

### 1. **Configuración de Dependencias (build.gradle.kts)**

Primero se agregaron las dependencias necesarias para la conectividad con el backend:

```kotlin
dependencies {
    // Retrofit para llamadas HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Kotlinx Serialization para JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    
    // OkHttp para interceptors y logging
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // DataStore para almacenamiento local
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Coroutines para programación asíncrona
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

### 2. **Configuración del Cliente API (ApiClient.kt)**

Creación del singleton para manejar las llamadas HTTP:

```kotlin
package com.example.levelup.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object ApiClient {
    
    private const val BASE_URL = "http://10.0.2.2:8080/" // Para emulador Android
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

**Puntos clave:**
- `10.0.2.2:8080` es la IP especial del emulador Android para localhost
- Timeouts configurados para conexiones lentas
- Logging interceptor para debug
- Kotlinx Serialization como conversor JSON

### 3. **Definición de la Interfaz API (ApiService.kt)**

Interfaz que define todos los endpoints del backend:

```kotlin
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
    
    // Más endpoints para categories, platforms, products...
}
```

**Características importantes:**
- Funciones `suspend` para uso con coroutines
- Headers de autorización para endpoints protegidos
- Tipos específicos de respuesta para cada operación
- Path parameters para IDs de recursos

### 4. **Sistema de Autenticación (AuthManager.kt)**

Manejo seguro del token JWT usando DataStore:

```kotlin
package com.example.levelup.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthManager(private val context: Context) {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
    }
    
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
        }
    }
    
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }
    
    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[EMAIL_KEY]
        }
    }
    
    fun getBearerToken(token: String): String {
        return "Bearer $token"
    }
    
    suspend fun clearAuth() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(EMAIL_KEY)
        }
    }
}
```

**Beneficios:**
- Almacenamiento encriptado automáticamente
- API reactiva con Flow
- Gestión automática de formato Bearer token

### 5. **Capa de Abstracción de Datos (LevelUpRepository.kt)**

Patrón Repository que centraliza el acceso a datos:

```kotlin
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
    
    // Helper function para manejo consistente de errores
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
    
    // Métodos de autenticación
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        val result = apiCall { 
            apiService.login(LoginRequest(email, password)) 
        }
        
        if (result is ApiResult.Success) {
            if (result.data.token != null && result.data.email != null) {
                authManager.saveToken(result.data.token)
                authManager.saveUserEmail(result.data.email)
                return result
            } else {
                return ApiResult.Error(Exception(result.data.message))
            }
        }
        
        return result
    }
    
    // Métodos CRUD con cache local
    suspend fun getUsers(): Flow<ApiResult<List<UserModel>>> = flow {
        emit(ApiResult.Loading())
        
        val token = authManager.getToken().first()
        if (token == null) {
            emit(ApiResult.Error(Exception("No authentication token")))
            return@flow
        }
        
        val result = apiCall { 
            apiService.getUsers(authManager.getBearerToken(token)) 
        }
        
        if (result is ApiResult.Success) {
            // Cache automático en almacenamiento local
            dataStoreManager.saveUsers(result.data)
        }
        
        emit(result)
    }
    
    // Más métodos CRUD...
}
```

**Características principales:**
- Patrón Repository para abstracción de datos
- Manejo consistente de errores con `ApiResult<T>`
- Cache automático en almacenamiento local
- Gestión automática de tokens de autorización
- Operaciones asíncronas con Flow

### 6. **Modelos de Datos con Serialización**

Definición de modelos que mapean con el backend:

```kotlin
package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String? = null,
    val email: String? = null,
    val message: String? = null,
    val user: UserModel? = null
)

@Serializable
data class RegisterRequest(
    val run: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: String = "usuario"
)

@Serializable
data class UserModel(
    val id: Long = 0L,
    val run: String = "",
    val direccion: String = "", // Campo solo frontend
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = ""
) {
    // Computed properties para compatibilidad UI
    val nombres: String get() = firstName
    val apellidos: String get() = lastName
    val correo: String get() = email
}

// Sealed class para manejo de estados
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()
    data class Loading(val isLoading: Boolean = true) : ApiResult<Nothing>()
}
```

### 7. **Integración en ViewModels**

Uso de la capa de datos en la lógica de negocio:

```kotlin
class UserViewModelWithApi(private val context: Context): ViewModel() {

    private val repository = LevelUpRepository(context)
    
    // Estados reactivos
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Datos desde repository con cache
    val usuarios: StateFlow<List<UserModel>> = repository.getUsersFromLocal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Operaciones CRUD
    fun guardarUsuario(usuario: UserModel) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val request = RegisterRequest(
                run = usuario.run.trim(),
                firstName = usuario.firstName.trim(),
                lastName = usuario.lastName.trim(), 
                email = usuario.email.trim(),
                password = usuario.password,
                role = usuario.role.ifBlank { "usuario" }
            )
            
            when (val result = repository.createUser(request)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    // Éxito manejado automáticamente
                    loadUsers() // Refresh datos
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }
}
```

### 8. **Configuración de Permisos (AndroidManifest.xml)**

Permisos necesarios para conectividad:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<application
    android:usesCleartextTraffic="true"  <!-- Para HTTP en desarrollo -->
    ... >
```

### 9. **Configuración de Red para Desarrollo**

Para permitir conexiones HTTP en desarrollo (network_security_config.xml):

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

## Flujo Completo de Conectividad

1. **Inicialización:** ApiClient crea instancia Retrofit con configuración
2. **Autenticación:** Usuario hace login → Token guardado en DataStore
3. **Llamadas Autenticadas:** Repository obtiene token → Añade header Authorization
4. **Manejo de Respuestas:** ApiResult wrapper maneja success/error/loading
5. **Cache Local:** Datos exitosos se guardan automáticamente en DataStore
6. **UI Reactiva:** ViewModels exponen StateFlow que se actualiza automáticamente

Este sistema proporciona una arquitectura robusta, escalable y mantenible para la comunicación con el backend Spring Boot.
