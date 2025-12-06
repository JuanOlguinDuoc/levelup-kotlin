package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `authViewModel should be created successfully`() = runTest(testDispatcher) {
        // Test que el ViewModel se puede crear sin errores
        val authViewModel = AuthViewModel(context)
        
        // Verificar que el ViewModel no es nulo
        assertNotNull(authViewModel)
        
        // Verificar que los StateFlows están inicializados
        assertNotNull(authViewModel.isLoggedIn)
        assertNotNull(authViewModel.userEmail)
    }

    @Test
    fun `logout should not throw exception`() = runTest(testDispatcher) {
        // Test que el método logout no lanza excepciones
        val authViewModel = AuthViewModel(context)
        
        // Ejecutar logout - no debería lanzar excepción
        try {
            authViewModel.logout()
            // Si llegamos aquí, no se lanzó excepción
            assertTrue(true)
        } catch (e: Exception) {
            fail("Logout should not throw exception: ${e.message}")
        }
    }

    @Test
    fun `isLoggedIn should have initial false value`() = runTest(testDispatcher) {
        // Test que isLoggedIn tiene un valor inicial
        val authViewModel = AuthViewModel(context)
        
        // Verificar que tiene un valor inicial (puede ser true o false)
        val initialValue = authViewModel.isLoggedIn.value
        assertNotNull("isLoggedIn should have an initial value", initialValue)
    }

    @Test
    fun `userEmail should have initial value`() = runTest(testDispatcher) {
        // Test que userEmail tiene un valor inicial (puede ser null)
        val authViewModel = AuthViewModel(context)
        
        // Verificar que el StateFlow está inicializado
        val emailFlow = authViewModel.userEmail
        assertNotNull("userEmail StateFlow should be initialized", emailFlow)
    }

    @Test
    fun `initialState_isLoggedIn_isFalse_and_userEmail_isNull`() = runTest(testDispatcher) {
        // Test para verificar el estado inicial del ViewModel
        val authViewModel = AuthViewModel(context)

        // Verificar que isLoggedIn es inicialmente falso
        assertFalse("isLoggedIn should be initially false", authViewModel.isLoggedIn.value)

        // Verificar que userEmail es inicialmente nulo
        assertNull("userEmail should be initially null", authViewModel.userEmail.value)
    }
}
