package com.example.levelup.model

import org.junit.Test
import org.junit.Assert.*

class ProductModelTest {

    @Test
    fun `ProductModel should create with all fields`() {
        // Arrange
        val id = 1L
        val name = "PlayStation 5"
        val description = "Console de videojuegos de nueva generación"
        val price = 500000
        val stock = 10
        
        // Act
        val product = ProductModel(
            id = id,
            name = name,
            description = description,
            price = price,
            stock = stock
        )
        
        // Assert
        assertEquals(id, product.id)
        assertEquals(name, product.name)
        assertEquals(description, product.description)
        assertEquals(price, product.price)
        assertEquals(stock, product.stock)
    }

    @Test
    fun `ProductModel should handle zero values`() {
        // Arrange & Act
        val product = ProductModel(
            id = 0,
            name = "",
            description = "",
            price = 0,
            stock = 0
        )
        
        // Assert
        assertNotNull(product)
        assertEquals(0, product.price)
        assertEquals(0, product.stock)
        assertTrue(product.name.isEmpty())
    }

    @Test
    fun `ProductModel should support copy with modifications`() {
        // Arrange
        val originalProduct = ProductModel(
            id = 1L,
            name = "PlayStation 5",
            description = "Console de videojuegos",
            price = 500000,
            stock = 10
        )
        
        // Act
        val modifiedProduct = originalProduct.copy(
            price = 450000,
            stock = 15
        )
        
        // Assert
        assertEquals(450000, modifiedProduct.price)
        assertEquals(15, modifiedProduct.stock)
        assertEquals(originalProduct.id, modifiedProduct.id)
        assertEquals(originalProduct.name, modifiedProduct.name)
        assertEquals(originalProduct.description, modifiedProduct.description)
    }

    @Test
    fun `ProductModel price should handle negative values`() {
        // Arrange & Act
        val product = ProductModel(
            id = 1L,
            name = "Test Product",
            description = "Test Description",
            price = -100,
            stock = 10
        )
        
        // Assert
        assertEquals(-100, product.price)
    }

    @Test
    fun `ProductModel stock should handle negative values`() {
        // Arrange & Act
        val product = ProductModel(
            id = 1L,
            name = "Test Product",
            description = "Test Description",
            price = 100,
            stock = -5
        )
        
        // Assert
        assertEquals(-5, product.stock)
    }

    @Test
    fun `ProductModel equals should work correctly`() {
        // Arrange
        val product1 = ProductModel(
            id = 1L,
            name = "PlayStation 5",
            description = "Console",
            price = 500000,
            stock = 10
        )
        
        val product2 = ProductModel(
            id = 1L,
            name = "PlayStation 5",
            description = "Console",
            price = 500000,
            stock = 10
        )
        
        val product3 = product1.copy(id = 2L)
        
        // Act & Assert
        assertEquals(product1, product2)
        assertNotEquals(product1, product3)
    }

    @Test
    fun `ProductModel hashCode should work correctly`() {
        // Arrange
        val product1 = ProductModel(
            id = 1L,
            name = "PlayStation 5",
            description = "Console",
            price = 500000,
            stock = 10
        )
        
        val product2 = ProductModel(
            id = 1L,
            name = "PlayStation 5",
            description = "Console",
            price = 500000,
            stock = 10
        )
        
        // Act & Assert
        assertEquals(product1.hashCode(), product2.hashCode())
    }

    @Test
    fun `ProductModel toString should contain relevant information`() {
        // Arrange
        val product = ProductModel(
            id = 1L,
            name = "PlayStation 5",
            description = "Console de videojuegos",
            price = 500000,
            stock = 10
        )
        
        // Act
        val productString = product.toString()
        
        // Assert
        assertTrue("toString should contain name", productString.contains("PlayStation 5"))
        assertTrue("toString should contain price", productString.contains("500000"))
        assertTrue("toString should contain stock", productString.contains("10"))
        assertTrue("toString should contain id", productString.contains("1"))
    }

    @Test
    fun `ProductModel should handle special characters in name and description`() {
        // Arrange
        val product = ProductModel(
            id = 1L,
            name = "Producto con ácentos & símbolos",
            description = "Descripción con çaracteres especiales: @#$%",
            price = 1000,
            stock = 5
        )
        
        // Act & Assert
        assertNotNull(product)
        assertEquals("Producto con ácentos & símbolos", product.name)
        assertEquals("Descripción con çaracteres especiales: @#$%", product.description)
    }
}
