package com.example.levelup.data

import com.example.levelup.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests esenciales para los modelos de datos de LevelUp
 * Cobertura 100%: constructores, propiedades computadas, serialización JSON
 */
class TestModels {
    
    // ===== USERMODEL TESTS - 100% COBERTURA =====
    
    @Test
    fun `UserModel constructor completo y propiedades computadas`() {
        val user = UserModel(
            id = 1L,
            run = "12345678-9",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "pass123",
            role = "admin",
            direccion = "Av. Libertad 456"
        )
        
        // Propiedades directas
        assertEquals(1L, user.id)
        assertEquals("12345678-9", user.run)
        assertEquals("Juan", user.firstName)
        assertEquals("Pérez", user.lastName)
        assertEquals("juan@test.com", user.email)
        assertEquals("pass123", user.password)
        assertEquals("admin", user.role)
        assertEquals("Av. Libertad 456", user.direccion)
        
        // Propiedades computadas
        assertEquals("Juan", user.nombres)
        assertEquals("Pérez", user.apellidos)
        assertEquals("juan@test.com", user.correo)
    }
    
    @Test
    fun `UserModel valores por defecto`() {
        val user = UserModel()
        assertEquals(0L, user.id)
        assertEquals("", user.run)
        assertEquals("", user.firstName)
        assertEquals("", user.lastName)
        assertEquals("", user.email)
        assertEquals("", user.password)
        assertEquals("usuario", user.role)
        assertEquals("", user.direccion)
    }
    
    @Test
    fun `UserModel serialización JSON`() {
        val user = UserModel(id = 1L, firstName = "Test", lastName = "User", email = "test@test.com")
        val json = Json.encodeToString(user)
        val deserializedUser = Json.decodeFromString<UserModel>(json)
        
        assertEquals(user.id, deserializedUser.id)
        assertEquals(user.firstName, deserializedUser.firstName)
        assertEquals(user.lastName, deserializedUser.lastName)
        assertEquals(user.email, deserializedUser.email)
        assertTrue(json.contains("\"firstName\":\"Test\""))
    }
    
    // ===== PRODUCTMODEL TESTS - 100% COBERTURA =====
    
    @Test
    fun `ProductModel constructor completo`() {
        val product = ProductModel(
            id = 1L,
            name = "Mouse Gaming",
            description = "Mouse RGB 7 botones",
            price = 25000,
            stock = 15
        )
        
        assertEquals(1L, product.id)
        assertEquals("Mouse Gaming", product.name)
        assertEquals("Mouse RGB 7 botones", product.description)
        assertEquals(25000, product.price)
        assertEquals(15, product.stock)
    }
    
    @Test
    fun `ProductModel serialización JSON`() {
        val product = ProductModel(1L, "Test Product", "Description", 1000, 5)
        val json = Json.encodeToString(product)
        val deserializedProduct = Json.decodeFromString<ProductModel>(json)
        
        assertEquals(product.name, deserializedProduct.name)
        assertEquals(product.price, deserializedProduct.price)
        assertTrue(json.contains("\"name\":\"Test Product\""))
    }
    
    // ===== CATEGORYMODEL TESTS - 100% COBERTURA =====
    
    @Test
    fun `CategoryModel constructor completo`() {
        val category = CategoryModel(
            id = 1L,
            name = "Gaming",
            description = "Productos gaming",
            icon = "/gaming.png"
        )
        
        assertEquals(1L, category.id)
        assertEquals("Gaming", category.name)
        assertEquals("Productos gaming", category.description)
        assertEquals("/gaming.png", category.icon)
    }
    
    @Test
    fun `CategoryModel serialización JSON`() {
        val category = CategoryModel(1L, "Test Category", "Test Desc", "/test.png")
        val json = Json.encodeToString(category)
        val deserializedCategory = Json.decodeFromString<CategoryModel>(json)
        
        assertEquals(category.name, deserializedCategory.name)
        assertTrue(json.contains("\"name\":\"Test Category\""))
    }
    
    // ===== PLATFORMMODEL TESTS - 100% COBERTURA =====
    
    @Test
    fun `PlatformModel constructor completo`() {
        val platform = PlatformModel(id = 1L, name = "PlayStation 5")
        assertEquals(1L, platform.id)
        assertEquals("PlayStation 5", platform.name)
    }
    
    @Test
    fun `PlatformModel serialización JSON`() {
        val platform = PlatformModel(1L, "PC")
        val json = Json.encodeToString(platform)
        val deserializedPlatform = Json.decodeFromString<PlatformModel>(json)
        
        assertEquals(platform.name, deserializedPlatform.name)
        assertTrue(json.contains("\"name\":\"PC\""))
    }
    
    // ===== ROLEMODEL TESTS - 100% COBERTURA =====
    
    @Test
    fun `RoleModel constructor completo`() {
        val role = RoleModel(id = 1L, name = "administrador")
        assertEquals(1L, role.id)
        assertEquals("administrador", role.name)
    }
    
    @Test
    fun `RoleModel valores por defecto`() {
        val role = RoleModel()
        assertEquals(null, role.id)
        assertEquals(null, role.name)
    }
    
    @Test
    fun `RoleModel serialización JSON`() {
        val role = RoleModel(1L, "admin")
        val json = Json.encodeToString(role)
        val deserializedRole = Json.decodeFromString<RoleModel>(json)
        
        assertEquals(role.name, deserializedRole.name)
        assertTrue(json.contains("\"name\":\"admin\""))
    }
    
    // ===== LOGINREQUEST TESTS - 100% COBERTURA =====
    
    @Test
    fun `LoginRequest constructor completo`() {
        val request = LoginRequest(email = "user@test.com", password = "password123")
        assertEquals("user@test.com", request.email)
        assertEquals("password123", request.password)
    }
    
    @Test
    fun `LoginRequest serialización JSON`() {
        val request = LoginRequest("test@test.com", "pass123")
        val json = Json.encodeToString(request)
        val deserializedRequest = Json.decodeFromString<LoginRequest>(json)
        
        assertEquals(request.email, deserializedRequest.email)
        assertEquals(request.password, deserializedRequest.password)
        assertTrue(json.contains("\"email\":\"test@test.com\""))
    }
    
    // ===== LOGINRESPONSE TESTS - 100% COBERTURA =====
    
    @Test
    fun `LoginResponse constructor completo`() {
        val response = LoginResponse(
            token = "jwt.token.here",
            email = "user@test.com",
            message = "Login exitoso"
        )
        
        assertEquals("jwt.token.here", response.token)
        assertEquals("user@test.com", response.email)
        assertEquals("Login exitoso", response.message)
    }
    
    @Test
    fun `LoginResponse con valores null`() {
        val response = LoginResponse(token = null, email = null, message = "Error")
        assertEquals(null, response.token)
        assertEquals(null, response.email)
        assertEquals("Error", response.message)
    }
    
    @Test
    fun `LoginResponse serialización JSON`() {
        val response = LoginResponse("token123", "test@test.com", "OK")
        val json = Json.encodeToString(response)
        val deserializedResponse = Json.decodeFromString<LoginResponse>(json)
        
        assertEquals(response.token, deserializedResponse.token)
        assertTrue(json.contains("\"token\":\"token123\""))
    }
    
    // ===== REGISTERREQUEST TESTS - 100% COBERTURA =====
    
    @Test
    fun `RegisterRequest constructor completo`() {
        val request = RegisterRequest(
            run = "12345678-9",
            firstName = "Carlos",
            lastName = "Mendoza",
            email = "carlos@test.com",
            password = "password123",
            role = "vendedor"
        )
        
        assertEquals("12345678-9", request.run)
        assertEquals("Carlos", request.firstName)
        assertEquals("Mendoza", request.lastName)
        assertEquals("carlos@test.com", request.email)
        assertEquals("password123", request.password)
        assertEquals("vendedor", request.role)
    }
    
    @Test
    fun `RegisterRequest role por defecto`() {
        val request = RegisterRequest(
            run = "11111111-1",
            firstName = "Ana",
            lastName = "Silva",
            email = "ana@test.com",
            password = "pass123"
        )
        assertEquals("usuario", request.role)
    }
    
    @Test
    fun `RegisterRequest serialización JSON`() {
        val request = RegisterRequest("12345678-9", "Test", "User", "test@test.com", "pass123")
        val json = Json.encodeToString(request)
        val deserializedRequest = Json.decodeFromString<RegisterRequest>(json)
        
        assertEquals(request.firstName, deserializedRequest.firstName)
        assertEquals("usuario", deserializedRequest.role) // valor por defecto
        assertTrue(json.contains("\"firstName\":\"Test\""))
    }
    
    // ===== USERCREATERESPONSE TESTS - 100% COBERTURA =====
    
    @Test
    fun `UserCreateResponse constructor completo`() {
        val user = UserModel(id = 1L, firstName = "Test", lastName = "User")
        val response = UserCreateResponse(
            message = "Usuario creado",
            user = user,
            error = null
        )
        
        assertEquals("Usuario creado", response.message)
        assertNotNull(response.user)
        assertEquals(null, response.error)
    }
    
    @Test
    fun `UserCreateResponse con error`() {
        val response = UserCreateResponse(
            message = "Error",
            user = null,
            error = "Email existe"
        )
        
        assertEquals("Error", response.message)
        assertEquals(null, response.user)
        assertEquals("Email existe", response.error)
    }
    
    @Test
    fun `UserCreateResponse serialización JSON`() {
        val response = UserCreateResponse("Test message", null, null)
        val json = Json.encodeToString(response)
        val deserializedResponse = Json.decodeFromString<UserCreateResponse>(json)
        
        assertEquals(response.message, deserializedResponse.message)
        assertTrue(json.contains("\"message\":\"Test message\""))
    }
    
    // ===== USERUPDATERESPONSE TESTS - 100% COBERTURA =====
    
    @Test
    fun `UserUpdateResponse constructor completo`() {
        val user = UserModel(id = 1L, firstName = "Updated")
        val response = UserUpdateResponse(
            message = "Actualizado",
            user = user,
            error = null
        )
        
        assertEquals("Actualizado", response.message)
        assertEquals("Updated", response.user?.firstName)
        assertEquals(null, response.error)
    }
    
    @Test
    fun `UserUpdateResponse serialización JSON`() {
        val response = UserUpdateResponse("Test message", null, null)
        val json = Json.encodeToString(response)
        val deserializedResponse = Json.decodeFromString<UserUpdateResponse>(json)
        
        assertEquals(response.message, deserializedResponse.message)
        assertTrue(json.contains("\"message\":\"Test message\""))
    }
    
    // ===== USERDELETERESPONSE TESTS - 100% COBERTURA =====
    
    @Test
    fun `UserDeleteResponse constructor completo`() {
        val response = UserDeleteResponse(
            message = "Usuario eliminado",
            error = null
        )
        
        assertEquals("Usuario eliminado", response.message)
        assertEquals(null, response.error)
    }
    
    @Test
    fun `UserDeleteResponse con error`() {
        val response = UserDeleteResponse(
            message = "Error al eliminar",
            error = "Usuario no encontrado"
        )
        
        assertEquals("Error al eliminar", response.message)
        assertEquals("Usuario no encontrado", response.error)
    }
    
    @Test
    fun `UserDeleteResponse serialización JSON`() {
        val response = UserDeleteResponse("Test message", null)
        val json = Json.encodeToString(response)
        val deserializedResponse = Json.decodeFromString<UserDeleteResponse>(json)
        
        assertEquals(response.message, deserializedResponse.message)
        assertTrue(json.contains("\"message\":\"Test message\""))
    }
    
    // ===== APIERROR TESTS - 100% COBERTURA =====
    
    @Test
    fun `ApiError constructor completo`() {
        val error = ApiError(
            message = "Error interno",
            error = "Server error 500"
        )
        
        assertEquals("Error interno", error.message)
        assertEquals("Server error 500", error.error)
    }
    
    @Test
    fun `ApiError solo con mensaje`() {
        val error = ApiError(message = "Error simple")
        assertEquals("Error simple", error.message)
        assertEquals(null, error.error)
    }
    
    @Test
    fun `ApiError serialización JSON`() {
        val error = ApiError("Test error", "Details")
        val json = Json.encodeToString(error)
        val deserializedError = Json.decodeFromString<ApiError>(json)
        
        assertEquals(error.message, deserializedError.message)
        assertEquals(error.error, deserializedError.error)
        assertTrue(json.contains("\"message\":\"Test error\""))
    }
    
    // ===== APIRESULT TESTS - 100% COBERTURA =====
    
    @Test
    fun `ApiResult Success constructor`() {
        val result = ApiResult.Success("test data")
        assertTrue(result is ApiResult.Success)
        assertEquals("test data", result.data)
    }
    
    @Test
    fun `ApiResult Error constructor`() {
        val exception = Exception("test error")
        val result = ApiResult.Error(exception)
        assertTrue(result is ApiResult.Error)
        assertEquals("test error", result.exception.message)
    }
    
    @Test
    fun `ApiResult Loading constructores`() {
        val loadingTrue = ApiResult.Loading()
        val loadingFalse = ApiResult.Loading(false)
        
        assertTrue(loadingTrue.isLoading)
        assertTrue(!loadingFalse.isLoading)
    }
    
    // ===== CARTPRODUCT TESTS - 100% COBERTURA =====
    
    @Test
    fun `CartProduct constructor completo`() {
        val cartProduct = CartProduct(productId = 5L, quantity = 3)
        assertEquals(5L, cartProduct.productId)
        assertEquals(3, cartProduct.quantity)
    }
    
    @Test
    fun `CartProduct serialización JSON`() {
        val cartProduct = CartProduct(10L, 2)
        val json = Json.encodeToString(cartProduct)
        val deserialized = Json.decodeFromString<CartProduct>(json)
        
        assertEquals(cartProduct.productId, deserialized.productId)
        assertEquals(cartProduct.quantity, deserialized.quantity)
        assertTrue(json.contains("\"productId\":10"))
        assertTrue(json.contains("\"quantity\":2"))
    }
    
    // ===== CARRITO TESTS - 100% COBERTURA =====
    
    @Test
    fun `Carrito constructor completo`() {
        val products = listOf(
            CartProduct(1L, 2),
            CartProduct(2L, 5)
        )
        val carrito = Carrito(
            id = 10L,
            userId = 100L,
            date = "2024-12-06",
            products = products
        )
        
        assertEquals(10L, carrito.id)
        assertEquals(100L, carrito.userId)
        assertEquals("2024-12-06", carrito.date)
        assertEquals(2, carrito.products.size)
        assertEquals(1L, carrito.products[0].productId)
        assertEquals(2, carrito.products[0].quantity)
    }
    
    @Test
    fun `Carrito con valores por defecto`() {
        val carrito = Carrito(userId = 50L, date = "2024-01-01")
        
        assertEquals(null, carrito.id)
        assertEquals(50L, carrito.userId)
        assertEquals("2024-01-01", carrito.date)
        assertTrue(carrito.products.isEmpty())
    }
    
    @Test
    fun `Carrito con id null y products vacío`() {
        val carrito = Carrito(userId = 1L, date = "2024-12-01", products = emptyList())
        assertEquals(null, carrito.id)
        assertEquals(0, carrito.products.size)
    }
    
    @Test
    fun `Carrito serialización JSON`() {
        val carrito = Carrito(
            id = 5L,
            userId = 20L,
            date = "2024-12-06",
            products = listOf(CartProduct(3L, 1))
        )
        val json = Json.encodeToString(carrito)
        val deserialized = Json.decodeFromString<Carrito>(json)
        
        assertEquals(carrito.id, deserialized.id)
        assertEquals(carrito.userId, deserialized.userId)
        assertEquals(carrito.date, deserialized.date)
        assertEquals(1, deserialized.products.size)
        assertTrue(json.contains("\"userId\":20"))
        assertTrue(json.contains("\"date\":\"2024-12-06\""))
    }
    
    @Test
    fun `Carrito serialización con id null`() {
        val carrito = Carrito(userId = 99L, date = "2025-01-01")
        val json = Json.encodeToString(carrito)
        val deserialized = Json.decodeFromString<Carrito>(json)
        
        assertEquals(null, deserialized.id)
        assertEquals(99L, deserialized.userId)
    }
}