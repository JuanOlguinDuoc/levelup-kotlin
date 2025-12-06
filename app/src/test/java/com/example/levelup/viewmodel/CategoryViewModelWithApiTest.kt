package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.model.ApiResult
import com.example.levelup.model.CategoryModel
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
class CategoryViewModelWithApiTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var context: Context
    private lateinit var categoryViewModel: CategoryViewModelWithApi

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        context = mockk(relaxed = true)
        
        // Mock del Repository
        mockkConstructor(LevelUpRepository::class)
        
        // Configurar comportamiento por defecto
        every { anyConstructed<LevelUpRepository>().getCategoriesFromLocal() } returns 
            flowOf(emptyList())
        coEvery { anyConstructed<LevelUpRepository>().getCategories() } returns 
            flowOf(ApiResult.Success(emptyList()))
        
        categoryViewModel = CategoryViewModelWithApi(context)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should be not loading with no errors`() = runTest {
        assertFalse(categoryViewModel.isLoading.value)
        assertNull(categoryViewModel.errorMessage.value)
    }

    @Test
    fun `loadCategories should update loading state on success`() = runTest {
        val testCategories = listOf(
            CategoryModel(id = 1, name = "Cat 1", description = "Desc 1", icon = "icon1"),
            CategoryModel(id = 2, name = "Cat 2", description = "Desc 2", icon = "icon2")
        )
        
        coEvery { anyConstructed<LevelUpRepository>().getCategories() } returns 
            flowOf(ApiResult.Success(testCategories))
        
        // Ejecutar
        categoryViewModel.loadCategories()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertFalse(categoryViewModel.isLoading.value)
        assertNull(categoryViewModel.errorMessage.value)
        coVerify { anyConstructed<LevelUpRepository>().getCategories() }
    }

    @Test
    fun `loadCategories should set error message on failure`() = runTest {
        val errorMessage = "Error al cargar categorías"
        coEvery { anyConstructed<LevelUpRepository>().getCategories() } returns 
            flowOf(ApiResult.Error(Exception(errorMessage)))
        
        // Ejecutar
        categoryViewModel.loadCategories()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals(errorMessage, categoryViewModel.errorMessage.value)
        assertFalse(categoryViewModel.isLoading.value)
    }

    @Test
    fun `guardarCategoria should call createCategory on repository`() = runTest {
        val newCategory = CategoryModel(id = 1, name = "New Cat", description = "New Desc", icon = "newIcon")
        
        coEvery { anyConstructed<LevelUpRepository>().createCategory(any()) } returns 
            ApiResult.Success(newCategory)
        
        // Ejecutar
        categoryViewModel.guardarCategoria(newCategory)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        coVerify { anyConstructed<LevelUpRepository>().createCategory(newCategory) }
        assertFalse(categoryViewModel.isLoading.value)
        assertNull(categoryViewModel.errorMessage.value)
    }

    @Test
    fun `guardarCategoria should set error message on failure`() = runTest {
        val errorMessage = "Error al guardar categoría"
        val newCategory = CategoryModel(id = 1, name = "New Cat", description = "New Desc", icon = "newIcon")
        
        coEvery { anyConstructed<LevelUpRepository>().createCategory(any()) } returns 
            ApiResult.Error(Exception(errorMessage))
        
        // Ejecutar
        categoryViewModel.guardarCategoria(newCategory)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals(errorMessage, categoryViewModel.errorMessage.value)
        assertFalse(categoryViewModel.isLoading.value)
    }

    @Test
    fun `categorias flow should return data from local repository`() = runTest {
        val testCategories = listOf(
            CategoryModel(id = 1, name = "Cat 1", description = "Desc 1", icon = "icon1")
        )
        
        every { anyConstructed<LevelUpRepository>().getCategoriesFromLocal() } returns 
            flowOf(testCategories)
        
        val viewModel = CategoryViewModelWithApi(context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar
        assertEquals(testCategories, viewModel.categorias.value)
    }

    @Test
    fun `init should trigger loadCategories`() = runTest {
        // Verificar que se llamó a getCategories en el init
        coVerify { anyConstructed<LevelUpRepository>().getCategories() }
    }

    @Test
    fun `isLoading should handle Loading state`() = runTest {
        coEvery { anyConstructed<LevelUpRepository>().getCategories() } returns 
            flowOf(ApiResult.Loading(true))
        
        categoryViewModel.loadCategories()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se procesó el estado de Loading
        coVerify { anyConstructed<LevelUpRepository>().getCategories() }
    }
}
