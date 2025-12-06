package com.example.levelup.repository

import com.example.levelup.api.ApiService
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Response

/**
 * Simplified tests for LevelUpRepository.
 * Covers one happy path and one error path per feature.
 */
class TestLevelUpRepository {

    private fun apiMock(): ApiService = mock()

    // --- Carts: happy + error ---
    @Test
    fun carts_happy_and_error() = runBlocking {
        val api = apiMock()
        val carts = listOf(Carrito(id = 1, userId = 1, date = "d", products = emptyList()))
        whenever(api.getAllCarts()).thenReturn(Response.success(carts))
        val repo = LevelUpRepository(null, api)

        val ok = repo.getAllCarts()
        assertTrue(ok is ApiResult.Success)

        whenever(api.getAllCarts()).thenAnswer { throw RuntimeException("net") }
        val err = repo.getAllCarts()
        assertTrue(err is ApiResult.Error)
    }

    // --- Auth: login happy + error ---
    @Test
    fun login_happy_and_error() = runBlocking {
        val api = apiMock()
        whenever(api.login(LoginRequest("a@b.com", "p"))).thenReturn(Response.success(LoginResponse("t","a@b.com","ok")))
        val repo = LevelUpRepository(null, api)
        val ok = repo.login("a@b.com","p")
        assertTrue(ok is ApiResult.Success)

        whenever(api.login(any())).thenAnswer { throw RuntimeException("net") }
        val err = repo.login("x","y")
        assertTrue(err is ApiResult.Error)
    }

    // --- Users: getUsers no-token and createUser no-token ---
    @Test
    fun users_no_token_behaviour() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        // getUsers should emit an Error because no token
        val emitted = mutableListOf<ApiResult<List<UserModel>>>()
        repo.getUsers().collect { emitted.add(it) }
        assertTrue(emitted.any { it is ApiResult.Error })

        // createUser should return Error when no token
        val req = RegisterRequest(run = "1", firstName = "A", lastName = "B", email = "a@b.com", password = "p")
        val createRes = repo.createUser(req)
        assertTrue(createRes is ApiResult.Error)
    }

    // --- getUsersFromLocal returns empty if no datastore ---
    @Test
    fun getUsersFromLocal_empty_when_no_datastore() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)
        var value: List<UserModel>? = null
        repo.getUsersFromLocal().collect { value = it }
        assertNotNull(value)
        assertTrue(value!!.isEmpty())
    }

    // --- Products: no-token behaviour (simple) ---
    @Test
    fun products_no_token_behaviour() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val emitted = mutableListOf<ApiResult<List<ProductModel>>>()
        repo.getProducts().collect { emitted.add(it) }
        assertTrue(emitted.any { it is ApiResult.Error })

        var local: List<ProductModel>? = null
        repo.getProductsFromLocal().collect { local = it }
        assertNotNull(local)
        assertTrue(local!!.isEmpty())
    }

    // --- Categories: no-token behaviour (simple) ---
    @Test
    fun categories_no_token_behaviour() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val emitted = mutableListOf<ApiResult<List<CategoryModel>>>()
        repo.getCategories().collect { emitted.add(it) }
        assertTrue(emitted.any { it is ApiResult.Error })

        var local: List<CategoryModel>? = null
        repo.getCategoriesFromLocal().collect { local = it }
        assertNotNull(local)
        assertTrue(local!!.isEmpty())
    }

    // --- Platforms: no-token behaviour (simple) ---
    @Test
    fun platforms_no_token_behaviour() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val emitted = mutableListOf<ApiResult<List<PlatformModel>>>()
        repo.getPlatforms().collect { emitted.add(it) }
        assertTrue(emitted.any { it is ApiResult.Error })

        var local: List<PlatformModel>? = null
        repo.getPlatformsFromLocal().collect { local = it }
        assertNotNull(local)
        assertTrue(local!!.isEmpty())
    }

    // --- Users CRUD: no-token returns Error ---
    @Test
    fun users_crud_no_token() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val user = UserModel(id = 1, email = "test@test.com")
        assertTrue(repo.updateUser(1, user) is ApiResult.Error)
        assertTrue(repo.deleteUser(1) is ApiResult.Error)
    }

    // --- Products CRUD: no-token returns Error ---
    @Test
    fun products_crud_no_token() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val prod = ProductModel(id = 1, name = "p", description = "d", price = 100, stock = 10)
        assertTrue(repo.createProduct(prod) is ApiResult.Error)
        assertTrue(repo.updateProduct(1, prod) is ApiResult.Error)
        assertTrue(repo.deleteProduct(1) is ApiResult.Error)
    }

    // --- Categories CRUD: no-token returns Error ---
    @Test
    fun categories_crud_no_token() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val cat = CategoryModel(id = 1, name = "c", description = "d", icon = "i")
        assertTrue(repo.createCategory(cat) is ApiResult.Error)
        assertTrue(repo.updateCategory(1, cat) is ApiResult.Error)
        assertTrue(repo.deleteCategory(1) is ApiResult.Error)
    }

    // --- Platforms CRUD: no-token returns Error ---
    @Test
    fun platforms_crud_no_token() = runBlocking {
        val api = apiMock()
        val repo = LevelUpRepository(null, api)

        val plat = PlatformModel(id = 1, name = "p")
        assertTrue(repo.createPlatform(plat) is ApiResult.Error)
        assertTrue(repo.updatePlatform(1, plat) is ApiResult.Error)
        assertTrue(repo.deletePlatform(1) is ApiResult.Error)
    }

    // --- Carts: more operations (getById, create, update, delete) ---
    @Test
    fun carts_operations() = runBlocking {
        val api = apiMock()
        val cart = Carrito(id = 2, userId = 2, date = "d", products = emptyList())
        whenever(api.getCartById(2)).thenReturn(Response.success(cart))
        whenever(api.createCart(cart)).thenReturn(Response.success(cart))
        whenever(api.updateCart(2, cart)).thenReturn(Response.success(cart))
        whenever(api.deleteCart(2)).thenReturn(Response.success(Unit))

        val repo = LevelUpRepository(null, api)

        assertTrue(repo.getCartById(2) is ApiResult.Success)
        assertTrue(repo.createCart(cart) is ApiResult.Success)
        assertTrue(repo.updateCart(2, cart) is ApiResult.Success)
        assertTrue(repo.deleteCart(2) is ApiResult.Success)
    }

    // --- Register: simple happy path ---
    @Test
    fun register_happy() = runBlocking {
        val api = apiMock()
        val req = RegisterRequest(run = "1", firstName = "A", lastName = "B", email = "a@b.com", password = "p")
        val resp = UserCreateResponse(message = "ok", user = UserModel(id = 5, email = "a@b.com"))
        whenever(api.register(req)).thenReturn(Response.success(resp))

        val repo = LevelUpRepository(null, api)
        val result = repo.register(req)
        assertTrue(result is ApiResult.Success)
    }
}
