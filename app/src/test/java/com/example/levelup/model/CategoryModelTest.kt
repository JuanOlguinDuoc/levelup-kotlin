package com.example.levelup.model

import org.junit.Test
import org.junit.Assert.*

class CategoryModelTest {

    @Test
    fun `CategoryModel should create with all fields`() {
        // Arrange
        val id = 1L
        val name = "Consolas"
        val description = "Consolas de videojuegos"
        val icon = "console_icon"
        
        // Act
        val category = CategoryModel(
            id = id,
            name = name,
            description = description,
            icon = icon
        )
        
        // Assert
        assertEquals(id, category.id)
        assertEquals(name, category.name)
        assertEquals(description, category.description)
        assertEquals(icon, category.icon)
    }

    @Test
    fun `CategoryModel should handle empty fields`() {
        // Arrange & Act
        val category = CategoryModel(
            id = 0,
            name = "",
            description = "",
            icon = ""
        )
        
        // Assert
        assertNotNull(category)
        assertTrue(category.name.isEmpty())
        assertTrue(category.description.isEmpty())
        assertTrue(category.icon.isEmpty())
    }

    @Test
    fun `CategoryModel should support copy with modifications`() {
        // Arrange
        val originalCategory = CategoryModel(
            id = 1L,
            name = "Consolas",
            description = "Consolas de videojuegos",
            icon = "console_icon"
        )
        
        // Act
        val modifiedCategory = originalCategory.copy(
            name = "Videojuegos",
            description = "Videojuegos para todas las plataformas"
        )
        
        // Assert
        assertEquals("Videojuegos", modifiedCategory.name)
        assertEquals("Videojuegos para todas las plataformas", modifiedCategory.description)
        assertEquals(originalCategory.id, modifiedCategory.id)
        assertEquals(originalCategory.icon, modifiedCategory.icon)
    }

    @Test
    fun `CategoryModel equals should work correctly`() {
        // Arrange
        val category1 = CategoryModel(
            id = 1L,
            name = "Consolas",
            description = "Consolas de videojuegos",
            icon = "console_icon"
        )
        
        val category2 = CategoryModel(
            id = 1L,
            name = "Consolas",
            description = "Consolas de videojuegos",
            icon = "console_icon"
        )
        
        val category3 = category1.copy(id = 2L)
        
        // Act & Assert
        assertEquals(category1, category2)
        assertNotEquals(category1, category3)
    }

    @Test
    fun `CategoryModel hashCode should work correctly`() {
        // Arrange
        val category1 = CategoryModel(
            id = 1L,
            name = "Consolas",
            description = "Consolas de videojuegos",
            icon = "console_icon"
        )
        
        val category2 = CategoryModel(
            id = 1L,
            name = "Consolas",
            description = "Consolas de videojuegos",
            icon = "console_icon"
        )
        
        // Act & Assert
        assertEquals(category1.hashCode(), category2.hashCode())
    }

    @Test
    fun `CategoryModel toString should contain relevant information`() {
        // Arrange
        val category = CategoryModel(
            id = 1L,
            name = "Consolas",
            description = "Consolas de videojuegos",
            icon = "console_icon"
        )
        
        // Act
        val categoryString = category.toString()
        
        // Assert
        assertTrue("toString should contain name", categoryString.contains("Consolas"))
        assertTrue("toString should contain description", categoryString.contains("Consolas de videojuegos"))
        assertTrue("toString should contain icon", categoryString.contains("console_icon"))
        assertTrue("toString should contain id", categoryString.contains("1"))
    }

    @Test
    fun `CategoryModel should handle special characters`() {
        // Arrange
        val category = CategoryModel(
            id = 1L,
            name = "Categoría con ácentos",
            description = "Descripción con çaracteres especiales: @#$%",
            icon = "icon_special"
        )
        
        // Act & Assert
        assertNotNull(category)
        assertEquals("Categoría con ácentos", category.name)
        assertEquals("Descripción con çaracteres especiales: @#$%", category.description)
    }

    @Test
    fun `CategoryModel icon should handle different formats`() {
        // Arrange
        val testIcons = listOf(
            "icon.png",
            "icon.jpg",
            "icon_name",
            "folder/icon.svg",
            "@drawable/ic_icon"
        )
        
        // Act & Assert
        testIcons.forEach { iconName ->
            val category = CategoryModel(
                id = 1L,
                name = "Test Category",
                description = "Test Description",
                icon = iconName
            )
            
            assertNotNull("Category with icon '$iconName' should be created", category)
            assertEquals("Icon should be preserved", iconName, category.icon)
        }
    }

    @Test
    fun `CategoryModel should handle null-like values`() {
        // Arrange & Act
        val category = CategoryModel(
            id = 0L,
            name = "null",
            description = "undefined",
            icon = "none"
        )
        
        // Assert
        assertNotNull(category)
        assertEquals("null", category.name)
        assertEquals("undefined", category.description)
        assertEquals("none", category.icon)
    }
}
