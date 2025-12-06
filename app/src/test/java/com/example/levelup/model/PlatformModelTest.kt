package com.example.levelup.model

import org.junit.Test
import org.junit.Assert.*

class PlatformModelTest {

    @Test
    fun `PlatformModel should create with all fields`() {
        // Arrange
        val id = 1L
        val name = "PlayStation 5"
        
        // Act
        val platform = PlatformModel(id = id, name = name)
        
        // Assert
        assertEquals(id, platform.id)
        assertEquals(name, platform.name)
    }

    @Test
    fun `PlatformModel should handle empty name`() {
        // Arrange & Act
        val platform = PlatformModel(id = 0, name = "")
        
        // Assert
        assertNotNull(platform)
        assertTrue(platform.name.isEmpty())
        assertEquals(0, platform.id)
    }

    @Test
    fun `PlatformModel should support copy with modifications`() {
        // Arrange
        val originalPlatform = PlatformModel(id = 1L, name = "PlayStation 5")
        
        // Act
        val modifiedPlatform = originalPlatform.copy(name = "PlayStation 5 Pro")
        
        // Assert
        assertEquals("PlayStation 5 Pro", modifiedPlatform.name)
        assertEquals(originalPlatform.id, modifiedPlatform.id)
    }

    @Test
    fun `PlatformModel equals should work correctly`() {
        // Arrange
        val platform1 = PlatformModel(id = 1L, name = "PlayStation 5")
        val platform2 = PlatformModel(id = 1L, name = "PlayStation 5")
        val platform3 = platform1.copy(id = 2L)
        
        // Act & Assert
        assertEquals(platform1, platform2)
        assertNotEquals(platform1, platform3)
    }

    @Test
    fun `PlatformModel hashCode should work correctly`() {
        // Arrange
        val platform1 = PlatformModel(id = 1L, name = "PlayStation 5")
        val platform2 = PlatformModel(id = 1L, name = "PlayStation 5")
        
        // Act & Assert
        assertEquals(platform1.hashCode(), platform2.hashCode())
    }

    @Test
    fun `PlatformModel toString should contain relevant information`() {
        // Arrange
        val platform = PlatformModel(id = 1L, name = "PlayStation 5")
        
        // Act
        val platformString = platform.toString()
        
        // Assert
        assertTrue("toString should contain name", platformString.contains("PlayStation 5"))
        assertTrue("toString should contain id", platformString.contains("1"))
    }

    @Test
    fun `PlatformModel should handle different platform names`() {
        // Arrange
        val platformNames = listOf(
            "PlayStation 5",
            "Xbox Series X",
            "Nintendo Switch",
            "PC",
            "Mobile",
            "Steam Deck"
        )
        
        // Act & Assert
        platformNames.forEachIndexed { index, name ->
            val platform = PlatformModel(id = index.toLong(), name = name)
            
            assertNotNull("Platform with name '$name' should be created", platform)
            assertEquals("Platform name should be preserved", name, platform.name)
            assertEquals("Platform id should be correct", index.toLong(), platform.id)
        }
    }

    @Test
    fun `PlatformModel should handle special characters in name`() {
        // Arrange
        val platform = PlatformModel(
            id = 1L, 
            name = "Plataforma con ácentos & símbolos especiales"
        )
        
        // Act & Assert
        assertNotNull(platform)
        assertEquals("Plataforma con ácentos & símbolos especiales", platform.name)
    }

    @Test
    fun `PlatformModel should handle very long names`() {
        // Arrange
        val longName = "A".repeat(1000)
        val platform = PlatformModel(id = 1L, name = longName)
        
        // Act & Assert
        assertNotNull(platform)
        assertEquals(longName, platform.name)
        assertEquals(1000, platform.name.length)
    }

    @Test
    fun `PlatformModel should handle negative id`() {
        // Arrange & Act
        val platform = PlatformModel(id = -1L, name = "Test Platform")
        
        // Assert
        assertNotNull(platform)
        assertEquals(-1L, platform.id)
        assertEquals("Test Platform", platform.name)
    }

    @Test
    fun `PlatformModel copy should preserve original when no changes`() {
        // Arrange
        val originalPlatform = PlatformModel(id = 1L, name = "PlayStation 5")
        
        // Act
        val copiedPlatform = originalPlatform.copy()
        
        // Assert
        assertEquals(originalPlatform, copiedPlatform)
        assertEquals(originalPlatform.id, copiedPlatform.id)
        assertEquals(originalPlatform.name, copiedPlatform.name)
    }
}
