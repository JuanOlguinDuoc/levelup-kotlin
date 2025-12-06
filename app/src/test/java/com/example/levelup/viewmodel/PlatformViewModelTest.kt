package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.PlatformModel
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
class PlatformViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var context: Context
    private lateinit var platformViewModel: PlatformViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        context = mockk(relaxed = true)
        dataStoreManager = mockk(relaxed = true)
        
        // Configurar comportamiento por defecto
        every { dataStoreManager.platforms } returns flowOf(emptyList())
        coEvery { dataStoreManager.savePlatform(any()) } just Runs
        
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have empty platforms list`() = runTest {
        assertEquals(0, platformViewModel.plataformas.value.size)
    }

    @Test
    fun `guardarPlataforma should add new platform`() = runTest {
        val existingPlatforms = listOf(
            PlatformModel(id = 1, name = "PlayStation")
        )
        val newPlatform = PlatformModel(id = 2, name = "Xbox")
        
        every { dataStoreManager.platforms } returns flowOf(existingPlatforms)
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        platformViewModel.guardarPlataforma(newPlatform)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a savePlatform con la lista actualizada
        coVerify { 
            dataStoreManager.savePlatform(
                match { it.size == 2 && it.contains(newPlatform) }
            )
        }
    }

    @Test
    fun `guardarPlataforma should not add duplicate platform`() = runTest {
        val existingPlatform = PlatformModel(id = 1, name = "PlayStation")
        val existingPlatforms = listOf(existingPlatform)
        
        every { dataStoreManager.platforms } returns flowOf(existingPlatforms)
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Intentar agregar plataforma duplicada
        platformViewModel.guardarPlataforma(existingPlatform)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que NO se llamó a savePlatform
        coVerify(exactly = 0) { dataStoreManager.savePlatform(any()) }
    }

    @Test
    fun `eliminarPlataforma should remove platform by id`() = runTest {
        val platforms = listOf(
            PlatformModel(id = 1, name = "PlayStation"),
            PlatformModel(id = 2, name = "Xbox")
        )
        
        every { dataStoreManager.platforms } returns flowOf(platforms)
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        platformViewModel.eliminarPlataforma(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a savePlatform sin la plataforma eliminada
        coVerify { 
            dataStoreManager.savePlatform(
                match { it.size == 1 && it.none { platform -> platform.id == 1L } }
            )
        }
    }

    @Test
    fun `editarPlataforma should update existing platform`() = runTest {
        val originalPlatform = PlatformModel(id = 1, name = "Old Name")
        val platforms = listOf(originalPlatform)
        
        every { dataStoreManager.platforms } returns flowOf(platforms)
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        platformViewModel.editarPlataforma(1, "New Name")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a savePlatform con la plataforma actualizada
        coVerify { 
            dataStoreManager.savePlatform(
                match { 
                    it.size == 1 && 
                    it[0].name == "New Name"
                }
            )
        }
    }

    @Test
    fun `editarPlataforma should not update if platform not found`() = runTest {
        val platforms = listOf(
            PlatformModel(id = 1, name = "PlayStation")
        )
        
        every { dataStoreManager.platforms } returns flowOf(platforms)
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Intentar editar plataforma inexistente
        platformViewModel.editarPlataforma(999, "New Name")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que NO se llamó a savePlatform con el nuevo nombre
        coVerify(exactly = 0) { 
            dataStoreManager.savePlatform(
                match { it.any { platform -> platform.name == "New Name" } }
            )
        }
    }

    @Test
    fun `multiple operations should work correctly`() = runTest {
        val platforms = mutableListOf(
            PlatformModel(id = 1, name = "PlayStation"),
            PlatformModel(id = 2, name = "Xbox")
        )
        
        every { dataStoreManager.platforms } returns flowOf(platforms)
        platformViewModel = PlatformViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Agregar
        val newPlatform = PlatformModel(id = 3, name = "Nintendo")
        platformViewModel.guardarPlataforma(newPlatform)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a savePlatform al menos una vez
        coVerify(atLeast = 1) { dataStoreManager.savePlatform(any()) }
    }
}
