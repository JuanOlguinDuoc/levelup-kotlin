package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.model.ApiResult
import com.example.levelup.model.Carrito
import com.example.levelup.model.CartProduct
import com.example.levelup.repository.LevelUpRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var context: Context
    private lateinit var repository: LevelUpRepository
    private lateinit var carritoViewModel: CarritoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        repository = mockk()
        carritoViewModel = CarritoViewModel(context)
        
        // Use reflection to set the private repository property
        val repositoryField = carritoViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(carritoViewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `carritoViewModel should be created successfully`() = runTest(testDispatcher) {
        // Test que el ViewModel se puede crear sin errores
        val carritoViewModel = CarritoViewModel(context)
        
        // Verificar que el ViewModel no es nulo
        assertNotNull(carritoViewModel)
        
        // Verificar que los StateFlows están inicializados
        assertNotNull(carritoViewModel.carts)
        assertNotNull(carritoViewModel.isLoading)
        assertNotNull(carritoViewModel.errorMessage)
    }

    @Test
    fun `loadAllCarts should not throw exception`() = runTest(testDispatcher) {
        // Test que el método loadAllCarts no lanza excepciones
        val carritoViewModel = CarritoViewModel(context)
        
        // Ejecutar loadAllCarts - no debería lanzar excepción
        try {
            carritoViewModel.loadAllCarts()
            // Si llegamos aquí, no se lanzó excepción
            assertTrue(true)
        } catch (e: Exception) {
            fail("loadAllCarts should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `getCartById should not throw exception`() = runTest(testDispatcher) {
        // Test que el método getCartById no lanza excepciones
        val carritoViewModel = CarritoViewModel(context)
        
        // Ejecutar getCartById - no debería lanzar excepción
        try {
            carritoViewModel.getCartById(1)
            // Si llegamos aquí, no se lanzó excepción
            assertTrue(true)
        } catch (e: Exception) {
            fail("getCartById should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `carts should have initial value`() = runTest(testDispatcher) {
        // Test que carts tiene un valor inicial
        val carritoViewModel = CarritoViewModel(context)
        
        // Verificar que tiene un valor inicial
        val initialValue = carritoViewModel.carts.value
        assertNotNull("carts should have an initial value", initialValue)
    }

    @Test
    fun `loadAllCarts should update carts on success`() = runTest(testDispatcher) {
        // Given
        val mockCarts = listOf(
            Carrito(id = 1, userId = 1, date = "2024-01-01", products = emptyList()),
            Carrito(id = 2, userId = 2, date = "2024-01-02", products = emptyList())
        )
        coEvery { repository.getAllCarts() } returns ApiResult.Success(mockCarts)

        // When
        carritoViewModel.loadAllCarts()

        // Then
        assertEquals(mockCarts, carritoViewModel.carts.value)
        assertFalse(carritoViewModel.isLoading.value)
        assertNull(carritoViewModel.errorMessage.value)
    }

    @Test
    fun `loadAllCarts should set error message on failure`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getAllCarts() } returns ApiResult.Error(Exception(errorMessage))

        // When
        carritoViewModel.loadAllCarts()

        // Then
        assertEquals(errorMessage, carritoViewModel.errorMessage.value)
        assertTrue(carritoViewModel.carts.value.isEmpty())
        assertFalse(carritoViewModel.isLoading.value)
    }

    @Test
    fun `getCartById should update carts on success`() = runTest(testDispatcher) {
        // Given
        val cartId = 1L
        val mockCart = Carrito(id = cartId, userId = 1, date = "2024-01-01", products = emptyList())
        coEvery { repository.getCartById(cartId) } returns ApiResult.Success(mockCart)

        // When
        carritoViewModel.getCartById(cartId)

        // Then
        assertEquals(listOf(mockCart), carritoViewModel.carts.value)
        assertFalse(carritoViewModel.isLoading.value)
        assertNull(carritoViewModel.errorMessage.value)
    }

    @Test
    fun `getCartById should set error message on failure`() = runTest(testDispatcher) {
        // Given
        val cartId = 1L
        val errorMessage = "Cart not found"
        coEvery { repository.getCartById(cartId) } returns ApiResult.Error(Exception(errorMessage))

        // When
        carritoViewModel.getCartById(cartId)

        // Then
        assertEquals(errorMessage, carritoViewModel.errorMessage.value)
        assertTrue(carritoViewModel.carts.value.isEmpty())
        assertFalse(carritoViewModel.isLoading.value)
    }

    @Test
    fun `createCart should refresh carts on success`() = runTest(testDispatcher) {
        // Given
        val newCart = Carrito(id = 3, userId = 1, date = "2024-01-03", products = emptyList())
        coEvery { repository.createCart(any()) } returns ApiResult.Success(newCart)
        coEvery { repository.getAllCarts() } returns ApiResult.Success(listOf(newCart)) // For the refresh

        // When
        carritoViewModel.createCart(1, emptyList())

        // Then
        coVerify { repository.createCart(any()) }
        coVerify { repository.getAllCarts() } // Verify that the list is refreshed
        assertFalse(carritoViewModel.isLoading.value)
        assertNull(carritoViewModel.errorMessage.value)
        assertEquals(listOf(newCart), carritoViewModel.carts.value)
    }

    @Test
    fun `createCart should set error message on failure`() = runTest(testDispatcher) {
        // Given
        val errorMessage = "Failed to create cart"
        coEvery { repository.createCart(any()) } returns ApiResult.Error(Exception(errorMessage))

        // When
        carritoViewModel.createCart(1, emptyList())

        // Then
        assertEquals(errorMessage, carritoViewModel.errorMessage.value)
        assertFalse(carritoViewModel.isLoading.value)
    }

    @Test
    fun `deleteCart should refresh carts on success`() = runTest(testDispatcher) {
        // Given
        val cartId = 1L
        coEvery { repository.deleteCart(cartId) } returns ApiResult.Success(Unit)
        coEvery { repository.getAllCarts() } returns ApiResult.Success(emptyList()) // For the refresh

        // When
        carritoViewModel.deleteCart(cartId)

        // Then
        coVerify { repository.deleteCart(cartId) }
        coVerify { repository.getAllCarts() } // Verify that the list is refreshed
        assertFalse(carritoViewModel.isLoading.value)
        assertNull(carritoViewModel.errorMessage.value)
        assertTrue(carritoViewModel.carts.value.isEmpty())
    }

    @Test
    fun `deleteCart should set error message on failure`() = runTest(testDispatcher) {
        // Given
        val cartId = 1L
        val errorMessage = "Failed to delete cart"
        coEvery { repository.deleteCart(cartId) } returns ApiResult.Error(Exception(errorMessage))

        // When
        carritoViewModel.deleteCart(cartId)

        // Then
        assertEquals(errorMessage, carritoViewModel.errorMessage.value)
        assertFalse(carritoViewModel.isLoading.value)
    }

    @Test
    fun `updateCart should refresh carts on success`() = runTest(testDispatcher) {
        // Given
        val cartId = 1L
        val updatedCart = Carrito(id = cartId, userId = 1, date = "2024-01-01", products = emptyList())
        coEvery { repository.updateCart(cartId, updatedCart) } returns ApiResult.Success(updatedCart)
        coEvery { repository.getAllCarts() } returns ApiResult.Success(listOf(updatedCart)) // For the refresh

        // When
        carritoViewModel.updateCart(cartId, updatedCart)

        // Then
        coVerify { repository.updateCart(cartId, updatedCart) }
        coVerify { repository.getAllCarts() } // Verify that the list is refreshed
        assertFalse(carritoViewModel.isLoading.value)
        assertNull(carritoViewModel.errorMessage.value)
        assertEquals(listOf(updatedCart), carritoViewModel.carts.value)
    }

    @Test
    fun `updateCart should set error message on failure`() = runTest(testDispatcher) {
        // Given
        val cartId = 1L
        val updatedCart = Carrito(id = cartId, userId = 1, date = "2024-01-01", products = emptyList())
        val errorMessage = "Failed to update cart"
        coEvery { repository.updateCart(cartId, updatedCart) } returns ApiResult.Error(Exception(errorMessage))

        // When
        carritoViewModel.updateCart(cartId, updatedCart)

        // Then
        assertEquals(errorMessage, carritoViewModel.errorMessage.value)
        assertFalse(carritoViewModel.isLoading.value)
    }

    @Test
    fun `clearError should set errorMessage to null`() {
        // Given
        val errorMessage = "An error"
        val errorField = carritoViewModel::class.java.getDeclaredField("_errorMessage")
        errorField.isAccessible = true
        (errorField.get(carritoViewModel) as MutableStateFlow<String?>).value = errorMessage
        assertEquals(errorMessage, carritoViewModel.errorMessage.value)

        // When
        carritoViewModel.clearError()

        // Then
        assertNull(carritoViewModel.errorMessage.value)
    }
}
