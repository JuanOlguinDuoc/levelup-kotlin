package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.UserModel
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
class UserViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var context: Context
    private lateinit var userViewModel: UserViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        context = mockk(relaxed = true)
        dataStoreManager = mockk(relaxed = true)
        
        // Configurar comportamiento por defecto
        every { dataStoreManager.users } returns flowOf(emptyList())
        coEvery { dataStoreManager.saveUsers(any()) } just Runs
        
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have empty users list`() = runTest {
        assertEquals(0, userViewModel.usuarios.value.size)
    }

    @Test
    fun `guardarUsuario should add new user`() = runTest {
        val existingUsers = listOf(
            UserModel(id = 1, run = "12345678-9", direccion = "Address 1", firstName = "John", 
                lastName = "Doe", email = "john@test.com", password = "pass123", role = "user")
        )
        val newUser = UserModel(id = 2, run = "98765432-1", direccion = "Address 2", firstName = "Jane",
            lastName = "Smith", email = "jane@test.com", password = "pass456", role = "user")
        
        every { dataStoreManager.users } returns flowOf(existingUsers)
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        userViewModel.guardarUsuario(newUser)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a saveUsers con la lista actualizada
        coVerify { 
            dataStoreManager.saveUsers(
                match { it.size == 2 && it.contains(newUser) }
            )
        }
    }

    @Test
    fun `guardarUsuario should not add duplicate user`() = runTest {
        val existingUser = UserModel(id = 1, run = "12345678-9", direccion = "Address 1", firstName = "John",
            lastName = "Doe", email = "john@test.com", password = "pass123", role = "user")
        val existingUsers = listOf(existingUser)
        
        every { dataStoreManager.users } returns flowOf(existingUsers)
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Intentar agregar usuario duplicado
        userViewModel.guardarUsuario(existingUser)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que NO se llamó a saveUsers
        coVerify(exactly = 0) { dataStoreManager.saveUsers(any()) }
    }

    @Test
    fun `eliminarUsuario should remove user by id`() = runTest {
        val users = listOf(
            UserModel(id = 1, run = "12345678-9", direccion = "Address 1", firstName = "John",
                lastName = "Doe", email = "john@test.com", password = "pass123", role = "user"),
            UserModel(id = 2, run = "98765432-1", direccion = "Address 2", firstName = "Jane",
                lastName = "Smith", email = "jane@test.com", password = "pass456", role = "user")
        )
        
        every { dataStoreManager.users } returns flowOf(users)
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        userViewModel.eliminarUsuario(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a saveUsers sin el usuario eliminado
        coVerify { 
            dataStoreManager.saveUsers(
                match { it.size == 1 && it.none { user -> user.id == 1L } }
            )
        }
    }

    @Test
    fun `editarUsuario should update existing user`() = runTest {
        val originalUser = UserModel(
            id = 1,
            run = "12345678-9",
            direccion = "Old Address",
            firstName = "Old Name",
            lastName = "Old LastName",
            email = "old@test.com",
            password = "oldpass",
            role = "user"
        )
        val users = listOf(originalUser)
        
        every { dataStoreManager.users } returns flowOf(users)
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        userViewModel.editarUsuario(1, "98765432-1", "New Address", "New Name", "New LastName", "new@test.com", "newpass")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a saveUsers con el usuario actualizado
        coVerify { 
            dataStoreManager.saveUsers(
                match { 
                    it.size == 1 && 
                    it[0].run == "98765432-1" &&
                    it[0].direccion == "New Address" &&
                    it[0].firstName == "New Name" &&
                    it[0].lastName == "New LastName" &&
                    it[0].email == "new@test.com" &&
                    it[0].password == "newpass"
                }
            )
        }
    }

    @Test
    fun `editarUsuario should not update if user not found`() = runTest {
        val users = listOf(
            UserModel(id = 1, run = "12345678-9", direccion = "Address 1", firstName = "John",
                lastName = "Doe", email = "john@test.com", password = "pass123", role = "user")
        )
        
        every { dataStoreManager.users } returns flowOf(users)
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Intentar editar usuario inexistente
        userViewModel.editarUsuario(999, "98765432-1", "New Address", "New Name", "New LastName", "new@test.com", "newpass")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que NO se llamó a saveUsers con el nuevo nombre
        coVerify(exactly = 0) { 
            dataStoreManager.saveUsers(
                match { it.any { user -> user.firstName == "New Name" } }
            )
        }
    }

    @Test
    fun `editarUsuario should preserve user id and role`() = runTest {
        val originalUser = UserModel(
            id = 1,
            run = "12345678-9",
            direccion = "Old Address",
            firstName = "Old Name",
            lastName = "Old LastName",
            email = "old@test.com",
            password = "oldpass",
            role = "admin"
        )
        val users = listOf(originalUser)
        
        every { dataStoreManager.users } returns flowOf(users)
        userViewModel = UserViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        userViewModel.editarUsuario(1, "98765432-1", "New Address", "New Name", "New LastName", "new@test.com", "newpass")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se preservó el id y role
        coVerify { 
            dataStoreManager.saveUsers(
                match { 
                    it.size == 1 && 
                    it[0].id == 1L &&
                    it[0].role == "admin"
                }
            )
        }
    }
}
