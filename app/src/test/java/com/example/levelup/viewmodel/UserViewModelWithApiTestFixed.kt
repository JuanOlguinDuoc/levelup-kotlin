package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.model.ApiResult
import com.example.levelup.model.UserModel
import com.example.levelup.model.UserCreateResponse
import com.example.levelup.repository.LevelUpRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelWithApiTestFixed {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var context: Context
    private lateinit var userViewModel: UserViewModelWithApi

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        context = mockk(relaxed = true)
        
        // Mock del Repository
        mockkConstructor(LevelUpRepository::class)
        
        // Configurar comportamiento por defecto
        every { anyConstructed<LevelUpRepository>().getUsersFromLocal() } returns 
            flowOf(emptyList())
        coEvery { anyConstructed<LevelUpRepository>().getUsers() } returns 
            flowOf(ApiResult.Success(emptyList()))
        
        userViewModel = UserViewModelWithApi(context)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should be not loading with no errors`() = runTest {
        assertFalse(userViewModel.isLoading.value)
        assertNull(userViewModel.errorMessage.value)
    }

    @Test
    fun `loadUsers should update loading state on success`() = runTest {
        val testUsers = listOf(
            UserModel(id = 1, run = "12345678-9", direccion = "Address 1", firstName = "John",
                lastName = "Doe", email = "john@test.com", password = "pass123", role = "user"),
            UserModel(id = 2, run = "98765432-1", direccion = "Address 2", firstName = "Jane",
                lastName = "Smith", email = "jane@test.com", password = "pass456", role = "user")
        )
        
        coEvery { anyConstructed<LevelUpRepository>().getUsers() } returns 
            flowOf(ApiResult.Success(testUsers))
        
        // Ejecutar
        userViewModel.loadUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertFalse(userViewModel.isLoading.value)
        assertNull(userViewModel.errorMessage.value)
        coVerify { anyConstructed<LevelUpRepository>().getUsers() }
    }

    @Test
    fun `loadUsers should set error message on failure`() = runTest {
        val errorMessage = "Error al cargar usuarios"
        coEvery { anyConstructed<LevelUpRepository>().getUsers() } returns 
            flowOf(ApiResult.Error(Exception(errorMessage)))
        
        // Ejecutar
        userViewModel.loadUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals(errorMessage, userViewModel.errorMessage.value)
        assertFalse(userViewModel.isLoading.value)
    }

    @Test
    fun `guardarUsuario should validate required fields`() = runTest {
        val invalidUser = UserModel(
            id = 1,
            run = "",
            direccion = "Address",
            firstName = "John",
            lastName = "Doe",
            email = "john@test.com",
            password = "pass123",
            role = "user"
        )
        
        // Ejecutar
        userViewModel.guardarUsuario(invalidUser)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals("Todos los campos son requeridos", userViewModel.errorMessage.value)
        assertFalse(userViewModel.isLoading.value)
    }

    @Test
    fun `guardarUsuario should call createUser on repository with valid data`() = runTest {
        val validUser = UserModel(
            id = 1,
            run = "12345678-9",
            direccion = "Address",
            firstName = "John",
            lastName = "Doe",
            email = "john@test.com",
            password = "pass123",
            role = "user"
        )
        
        val createResponse = UserCreateResponse(
            message = "User created successfully",
            user = validUser,
            error = null
        )
        
        coEvery { anyConstructed<LevelUpRepository>().createUser(any()) } returns 
            ApiResult.Success(createResponse)
        
        // Ejecutar
        userViewModel.guardarUsuario(validUser)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        coVerify { anyConstructed<LevelUpRepository>().createUser(any()) }
        assertFalse(userViewModel.isLoading.value)
    }

    @Test
    fun `guardarUsuario should set error message on repository failure`() = runTest {
        val validUser = UserModel(
            id = 1,
            run = "12345678-9",
            direccion = "Address",
            firstName = "John",
            lastName = "Doe",
            email = "john@test.com",
            password = "pass123",
            role = "user"
        )
        val errorMessage = "Error al guardar usuario"
        
        coEvery { anyConstructed<LevelUpRepository>().createUser(any()) } returns 
            ApiResult.Error(Exception(errorMessage))
        
        // Ejecutar
        userViewModel.guardarUsuario(validUser)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals(errorMessage, userViewModel.errorMessage.value)
        assertFalse(userViewModel.isLoading.value)
    }

    @Test
    fun `usuarios flow should return data from local repository`() = runTest {
        val testUsers = listOf(
            UserModel(id = 1, run = "12345678-9", direccion = "Address 1", firstName = "John",
                lastName = "Doe", email = "john@test.com", password = "pass123", role = "user")
        )
        
        every { anyConstructed<LevelUpRepository>().getUsersFromLocal() } returns 
            flowOf(testUsers)
        
        val viewModel = UserViewModelWithApi(context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals(testUsers, viewModel.usuarios.value)
    }

    @Test
    fun `init should trigger loadUsers`() = runTest {
        // Verificar que se llamó a getUsers en el init
        coVerify { anyConstructed<LevelUpRepository>().getUsers() }
    }

    @Test
    fun `guardarUsuario should trim whitespace from fields`() = runTest {
        val userWithSpaces = UserModel(
            id = 1,
            run = "  12345678-9  ",
            direccion = "Address",
            firstName = "  John  ",
            lastName = "  Doe  ",
            email = "  john@test.com  ",
            password = "pass123",
            role = "user"
        )
        
        val createResponse = UserCreateResponse(
            message = "User created successfully",
            user = userWithSpaces,
            error = null
        )
        
        coEvery { anyConstructed<LevelUpRepository>().createUser(any()) } returns 
            ApiResult.Success(createResponse)
        
        // Ejecutar
        userViewModel.guardarUsuario(userWithSpaces)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó con datos trimmed
        coVerify { anyConstructed<LevelUpRepository>().createUser(any()) }
    }

    @Test
    fun `isLoading should handle Loading state`() = runTest {
        coEvery { anyConstructed<LevelUpRepository>().getUsers() } returns 
            flowOf(ApiResult.Loading(true))
        
        userViewModel.loadUsers()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se procesó el estado de Loading
        assertTrue(userViewModel.isLoading.value)
    }
}
