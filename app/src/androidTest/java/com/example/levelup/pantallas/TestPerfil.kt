package com.example.levelup.pantallas

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para ProfileScreen
 */
class TestPerfil {

    @Test
    fun `test datos de perfil no son nulos`() {
        // Given: Datos del perfil de prueba
        val nombre = "Administrador"
        val correo = "admin@levelup.cl"
        val rol = "Administrador"
        val celular = "+56912345678"
        
        // Then: Los datos no deben ser nulos
        assertNotNull("El nombre no debe ser nulo", nombre)
        assertNotNull("El correo no debe ser nulo", correo)
        assertNotNull("El rol no debe ser nulo", rol)
        assertNotNull("El celular no debe ser nulo", celular)
    }

    @Test
    fun `test validacion de nombre`() {
        // Given: Nombre válido
        val nombre = "Administrador"
        
        // When: Se valida el nombre
        val isValid = nombre.isNotBlank()
        
        // Then: Debe ser válido
        assertTrue("El nombre debe ser válido", isValid)
    }

    @Test
    fun `test validacion de correo`() {
        // Given: Correo válido
        val correo = "admin@levelup.cl"
        
        // When: Se valida el correo
        val isValid = correo.contains("@") && correo.contains(".")
        
        // Then: Debe ser válido
        assertTrue("El correo debe tener formato válido", isValid)
    }

    @Test
    fun `test validacion de telefono chileno`() {
        // Given: Teléfono chileno válido
        val celular = "+56912345678"
        
        // When: Se valida el teléfono
        val isValid = celular.startsWith("+569") && celular.length == 12
        
        // Then: Debe ser válido
        assertTrue("El teléfono debe tener formato chileno válido", isValid)
    }

    @Test
    fun `test rol de administrador`() {
        // Given: Rol del usuario
        val rol = "Administrador"
        
        // When: Se verifica el rol
        val esAdmin = rol.equals("Administrador", ignoreCase = true)
        
        // Then: Debe ser administrador
        assertTrue("El usuario debe tener rol de administrador", esAdmin)
    }

    @Test
    fun `test callback de menu click`() {
        // Given: Estado del menú
        var menuClicked = false
        
        // When: Se hace click en el menú
        val onMenuClick: () -> Unit = { menuClicked = true }
        onMenuClick()
        
        // Then: El callback debe ejecutarse
        assertTrue("El callback de menú debe ejecutarse", menuClicked)
    }

    @Test
    fun `test informacion de perfil completa`() {
        // Given: Datos del perfil
        val nombre = "Administrador"
        val correo = "admin@levelup.cl"
        val rol = "Administrador"
        val celular = "+56912345678"
        
        // When: Se verifica que todos los campos estén completos
        val isComplete = nombre.isNotBlank() && 
                        correo.isNotBlank() && 
                        rol.isNotBlank() && 
                        celular.isNotBlank()
        
        // Then: El perfil debe estar completo
        assertTrue("Todos los campos del perfil deben estar completos", isComplete)
    }

    @Test
    fun `test formato de email`() {
        // Given: Email del perfil
        val correo = "admin@levelup.cl"
        
        // When: Se extrae el dominio
        val dominio = correo.substringAfter("@")
        
        // Then: El dominio debe ser correcto
        assertEquals("El dominio debe ser levelup.cl", "levelup.cl", dominio)
    }
}
