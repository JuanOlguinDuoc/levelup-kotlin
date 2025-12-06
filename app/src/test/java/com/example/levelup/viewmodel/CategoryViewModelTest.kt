package com.example.levelup.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.CategoryModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var context: Context
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var mediaPlayer: MediaPlayer

    // Use a MutableStateFlow to easily control the categories data in tests
    private lateinit var categoriesFlow: MutableStateFlow<List<CategoryModel>>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create mock context that won't cause file path issues
        context = mockk(relaxed = true)
        every { context.filesDir } returns mockk(relaxed = true)
        every { context.filesDir.absolutePath } returns "/tmp/test"
        
        dataStoreManager = mockk(relaxed = true)
        mediaPlayer = mockk(relaxed = true)

        // Mock MediaPlayer static create method
        mockkStatic(MediaPlayer::class)
        every { MediaPlayer.create(any(), any<android.net.Uri>()) } returns mediaPlayer

        // Setup the flow that the ViewModel will collect
        categoriesFlow = MutableStateFlow(emptyList())
        every { dataStoreManager.categories } returns categoriesFlow
        coEvery { dataStoreManager.saveCategories(any()) } just Runs

        // Create the ViewModel instance for all tests
        categoryViewModel = CategoryViewModel(dataStoreManager, context)
        testDispatcher.scheduler.advanceUntilIdle() // Ensure init completes
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is an empty list`() = runTest {
        assertTrue(categoryViewModel.categories.value.isEmpty())
    }

    @Test
    fun `guardarCategoria adds a new category and plays sound`() = runTest {
        val newCategory = CategoryModel(id = 1, name = "New Category", description = "Desc", icon = "icon")

        // Action
        categoryViewModel.guardarCategoria(newCategory)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification
        coVerify { dataStoreManager.saveCategories(listOf(newCategory)) }
        verify { mediaPlayer.start() }
    }

    @Test
    fun `guardarCategoria does not add a duplicate category`() = runTest {
        val existingCategory = CategoryModel(id = 1, name = "Existing", description = "Desc", icon = "icon")
        categoriesFlow.value = listOf(existingCategory) // Pre-load state
        testDispatcher.scheduler.advanceUntilIdle()

        // Action: Try to add the same category again
        categoryViewModel.guardarCategoria(existingCategory)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification: saveCategories should not be called again after the initial load
        coVerify(exactly = 0) { dataStoreManager.saveCategories(any()) }
        verify(exactly = 0) { mediaPlayer.start() }
    }

    @Test
    fun `eliminarCategoria removes the correct category`() = runTest {
        val category1 = CategoryModel(id = 1, name = "Category 1", description = "Desc 1", icon = "icon1")
        val category2 = CategoryModel(id = 2, name = "Category 2", description = "Desc 2", icon = "icon2")
        categoriesFlow.value = listOf(category1, category2)
        testDispatcher.scheduler.advanceUntilIdle()

        // Action
        categoryViewModel.eliminarCategoria(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification
        coVerify { dataStoreManager.saveCategories(listOf(category2)) }
    }

    @Test
    fun `editarCategoria updates an existing category`() = runTest {
        val originalCategory = CategoryModel(id = 1, name = "Old Name", description = "Old Desc", icon = "oldIcon")
        categoriesFlow.value = listOf(originalCategory)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedCategory = originalCategory.copy(name = "New Name", description = "New Desc", icon = "newIcon")

        // Action
        categoryViewModel.editarCategoria(1, "New Name", "New Desc", "newIcon")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification
        coVerify { dataStoreManager.saveCategories(listOf(updatedCategory)) }
    }

    @Test
    fun `editarCategoria does nothing if category not found`() = runTest {
        val category = CategoryModel(id = 1, name = "Category", description = "Desc", icon = "icon")
        categoriesFlow.value = listOf(category)
        testDispatcher.scheduler.advanceUntilIdle()

        // Action: Try to edit a non-existent category
        categoryViewModel.editarCategoria(99, "New Name", "New Desc", "newIcon")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification: saveCategories should not be called
        coVerify(exactly = 0) { dataStoreManager.saveCategories(any()) }
    }

    @Test
    fun `playSuccessSound handles exceptions gracefully`() = runTest {
        // Arrange: Make MediaPlayer throw an exception
        every { MediaPlayer.create(any(), any<android.net.Uri>()) } throws RuntimeException("Test Exception")
        val newCategory = CategoryModel(id = 1, name = "Category", description = "Desc", icon = "icon")

        // Action & Assert: The ViewModel should catch the exception and not crash
        try {
            categoryViewModel.guardarCategoria(newCategory)
            testDispatcher.scheduler.advanceUntilIdle()
        } catch (e: Exception) {
            fail("ViewModel should have caught the exception, but it was thrown.")
        }

        // Verification: saveCategories should still be called
        coVerify { dataStoreManager.saveCategories(listOf(newCategory)) }
    }
}
