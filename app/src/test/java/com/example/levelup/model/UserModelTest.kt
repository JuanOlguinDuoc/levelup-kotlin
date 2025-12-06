package com.example.levelup.model

import org.junit.Test
import org.junit.Assert.*

class UserModelTest {

    @Test
    fun `UserModel should create with all fields`() {
        // Arrange
        val id = 1L
        val run = "12345678-9"
        val direccion = "Av. Principal 123"
        val firstName = "Juan"
        val lastName = "Pérez"
        val email = "juan@test.com"
        val password = "password123"
        val role = "user"
        
        // Act
        val user = UserModel(
            id = id,
            run = run,
            direccion = direccion,
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            role = role
        )
        
        // Assert
        assertEquals(id, user.id)
        assertEquals(run, user.run)
        assertEquals(direccion, user.direccion)
        assertEquals(firstName, user.firstName)
        assertEquals(lastName, user.lastName)
        assertEquals(email, user.email)
        assertEquals(password, user.password)
        assertEquals(role, user.role)
    }

    @Test
    fun `UserModel should handle empty fields`() {
        // Arrange & Act
        val user = UserModel(
            id = 0,
            run = "",
            direccion = "",
            firstName = "",
            lastName = "",
            email = "",
            password = "",
            role = ""
        )
        
        // Assert
        assertNotNull(user)
        assertTrue(user.run.isEmpty())
        assertTrue(user.firstName.isEmpty())
        assertTrue(user.email.isEmpty())
    }

    @Test
    fun `UserModel should support copy with modifications`() {
        // Arrange
        val originalUser = UserModel(
            id = 1L,
            run = "12345678-9",
            direccion = "Av. Principal 123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "password123",
            role = "user"
        )
        
        // Act
        val modifiedUser = originalUser.copy(
            firstName = "Carlos",
            email = "carlos@test.com"
        )
        
        // Assert
        assertEquals("Carlos", modifiedUser.firstName)
        assertEquals("carlos@test.com", modifiedUser.email)
        assertEquals(originalUser.id, modifiedUser.id)
        assertEquals(originalUser.lastName, modifiedUser.lastName)
        assertEquals(originalUser.role, modifiedUser.role)
    }

    @Test
    fun `UserModel equals should work correctly`() {
        // Arrange
        val user1 = UserModel(
            id = 1L,
            run = "12345678-9",
            direccion = "Av. Principal 123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "password123",
            role = "user"
        )
        
        val user2 = UserModel(
            id = 1L,
            run = "12345678-9",
            direccion = "Av. Principal 123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "password123",
            role = "user"
        )
        
        val user3 = user1.copy(id = 2L)
        
        // Act & Assert
        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }

    @Test
    fun `UserModel hashCode should work correctly`() {
        // Arrange
        val user1 = UserModel(
            id = 1L,
            run = "12345678-9",
            direccion = "Av. Principal 123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "password123",
            role = "user"
        )
        
        val user2 = UserModel(
            id = 1L,
            run = "12345678-9",
            direccion = "Av. Principal 123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "password123",
            role = "user"
        )
        
        // Act & Assert
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `UserModel toString should contain relevant information`() {
        // Arrange
        val user = UserModel(
            id = 1L,
            run = "12345678-9",
            direccion = "Av. Principal 123",
            firstName = "Juan",
            lastName = "Pérez",
            email = "juan@test.com",
            password = "password123",
            role = "user"
        )
        
        // Act
        val userString = user.toString()
        
        // Assert
        assertTrue("toString should contain firstName", userString.contains("Juan"))
        assertTrue("toString should contain lastName", userString.contains("Pérez"))
        assertTrue("toString should contain email", userString.contains("juan@test.com"))
        assertTrue("toString should contain id", userString.contains("1"))
    }
}
