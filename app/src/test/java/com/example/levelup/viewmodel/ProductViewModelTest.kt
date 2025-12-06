package com.example.levelup.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.ProductModel
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
class ProductViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var context: Context
    private lateinit var productViewModel: ProductViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        context = mockk(relaxed = true)
        dataStoreManager = mockk(relaxed = true)
        
        // Configurar comportamiento por defecto
        every { dataStoreManager.products } returns flowOf(emptyList())
        coEvery { dataStoreManager.saveProducts(any()) } just Runs
        
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should have empty products list`() = runTest {
        assertEquals(0, productViewModel.productos.value.size)
    }

    @Test
    fun `guardarProducto should add new product`() = runTest {
        val existingProducts = listOf(
            ProductModel(id = 1, name = "Product 1", description = "Desc 1", price = 100, stock = 10)
        )
        val newProduct = ProductModel(id = 2, name = "Product 2", description = "Desc 2", price = 200, stock = 20)
        
        every { dataStoreManager.products } returns flowOf(existingProducts)
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        productViewModel.guardarProducto(newProduct)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a saveProducts con la lista actualizada
        coVerify { 
            dataStoreManager.saveProducts(
                match { it.size == 2 && it.contains(newProduct) }
            )
        }
    }

    @Test
    fun `guardarProducto should not add duplicate product`() = runTest {
        val existingProduct = ProductModel(id = 1, name = "Product 1", description = "Desc 1", price = 100, stock = 10)
        val existingProducts = listOf(existingProduct)
        
        every { dataStoreManager.products } returns flowOf(existingProducts)
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Intentar agregar producto duplicado
        productViewModel.guardarProducto(existingProduct)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que NO se llamó a saveProducts
        coVerify(exactly = 0) { dataStoreManager.saveProducts(any()) }
    }

    @Test
    fun `eliminarProducto should remove product by id`() = runTest {
        val products = listOf(
            ProductModel(id = 1, name = "Product 1", description = "Desc 1", price = 100, stock = 10),
            ProductModel(id = 2, name = "Product 2", description = "Desc 2", price = 200, stock = 20)
        )
        
        every { dataStoreManager.products } returns flowOf(products)
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        productViewModel.eliminarProducto(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a saveProducts sin el producto eliminado
        coVerify { 
            dataStoreManager.saveProducts(
                match { it.size == 1 && it.none { product -> product.id == 1L } }
            )
        }
    }

    @Test
    fun `editarProducto should update existing product`() = runTest {
        val originalProduct = ProductModel(
            id = 1, 
            name = "Old Name", 
            description = "Old Desc", 
            price = 100, 
            stock = 10
        )
        val products = listOf(originalProduct)
        
        every { dataStoreManager.products } returns flowOf(products)
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        productViewModel.editarProducto(1, "New Name", "New Desc", 200, 20)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se llamó a saveProducts con el producto actualizado
        coVerify { 
            dataStoreManager.saveProducts(
                match { 
                    it.size == 1 && 
                    it[0].name == "New Name" && 
                    it[0].description == "New Desc" &&
                    it[0].price == 200 &&
                    it[0].stock == 20
                }
            )
        }
    }

    @Test
    fun `editarProducto should not update if product not found`() = runTest {
        val products = listOf(
            ProductModel(id = 1, name = "Product 1", description = "Desc 1", price = 100, stock = 10)
        )
        
        every { dataStoreManager.products } returns flowOf(products)
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Intentar editar producto inexistente
        productViewModel.editarProducto(999, "New Name", "New Desc", 200, 20)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que NO se llamó a saveProducts con el nuevo nombre
        coVerify(exactly = 0) { 
            dataStoreManager.saveProducts(
                match { it.any { product -> product.name == "New Name" } }
            )
        }
    }

    @Test
    fun `editarProducto should preserve product id`() = runTest {
        val originalProduct = ProductModel(
            id = 1, 
            name = "Old Name", 
            description = "Old Desc", 
            price = 100, 
            stock = 10
        )
        val products = listOf(originalProduct)
        
        every { dataStoreManager.products } returns flowOf(products)
        productViewModel = ProductViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Ejecutar
        productViewModel.editarProducto(1, "New Name", "New Desc", 200, 20)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Verificar que se preservó el id
        coVerify { 
            dataStoreManager.saveProducts(
                match { 
                    it.size == 1 && 
                    it[0].id == 1L
                }
            )
        }
    }
}
