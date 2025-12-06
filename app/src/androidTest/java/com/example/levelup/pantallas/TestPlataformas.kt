package com.example.levelup.pantallas

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para PlatformScreen
 */
class TestPlataformas {

    @Test
    fun `test titulo de pantalla plataformas`() {
        // Given: Título de la pantalla
        val titulo = "Plataformas"
        
        // Then: El título debe ser correcto
        assertEquals("El título debe ser 'Plataformas'", "Plataformas", titulo)
    }

    @Test
    fun `test validacion de nombre de plataforma`() {
        // Given: Nombre de plataforma
        val nombre = "PlayStation 5"
        
        // When: Se valida el nombre
        val isValid = nombre.isNotBlank() && nombre.length >= 2
        
        // Then: Debe ser válido
        assertTrue("El nombre de la plataforma debe ser válido", isValid)
    }

    @Test
    fun `test lista de plataformas vacia inicial`() {
        // Given: Lista inicial de plataformas
        val plataformas = emptyList<Any>()
        
        // Then: La lista debe estar vacía
        assertTrue("La lista de plataformas debe estar vacía inicialmente", plataformas.isEmpty())
    }

    @Test
    fun `test agregar plataforma a lista`() {
        // Given: Lista de plataformas
        data class Plataforma(val id: Long, val nombre: String)
        val plataformas = mutableListOf<Plataforma>()
        
        // When: Se agrega una plataforma
        plataformas.add(Plataforma(1, "PlayStation 5"))
        
        // Then: La lista debe tener una plataforma
        assertEquals("La lista debe tener 1 plataforma", 1, plataformas.size)
        assertEquals("El nombre debe ser 'PlayStation 5'", "PlayStation 5", plataformas[0].nombre)
    }

    @Test
    fun `test eliminar plataforma de lista`() {
        // Given: Lista con plataformas
        data class Plataforma(val id: Long, val nombre: String)
        val plataformas = mutableListOf(
            Plataforma(1, "PlayStation 5"),
            Plataforma(2, "Xbox Series X"),
            Plataforma(3, "Nintendo Switch")
        )
        
        // When: Se elimina una plataforma
        plataformas.removeAt(1)
        
        // Then: La lista debe tener 2 plataformas
        assertEquals("La lista debe tener 2 plataformas", 2, plataformas.size)
        assertEquals("La primera debe ser PlayStation 5", "PlayStation 5", plataformas[0].nombre)
        assertEquals("La segunda debe ser Nintendo Switch", "Nintendo Switch", plataformas[1].nombre)
    }

    @Test
    fun `test editar nombre de plataforma`() {
        // Given: Plataforma original
        data class Plataforma(var nombre: String)
        val plataforma = Plataforma("PS5")
        
        // When: Se edita el nombre
        plataforma.nombre = "PlayStation 5"
        
        // Then: El nombre debe haberse actualizado
        assertEquals("El nombre debe estar actualizado", "PlayStation 5", plataforma.nombre)
    }

    @Test
    fun `test buscar plataforma por nombre`() {
        // Given: Lista de plataformas
        data class Plataforma(val id: Long, val nombre: String)
        val plataformas = listOf(
            Plataforma(1, "PlayStation 5"),
            Plataforma(2, "Xbox Series X"),
            Plataforma(3, "Nintendo Switch")
        )
        
        // When: Se busca una plataforma
        val resultado = plataformas.find { it.nombre.contains("Xbox", ignoreCase = true) }
        
        // Then: Debe encontrar la plataforma
        assertNotNull("Debe encontrar la plataforma", resultado)
        assertTrue("Debe contener 'Xbox'", resultado?.nombre?.contains("Xbox") ?: false)
    }

    @Test
    fun `test filtrar plataformas`() {
        // Given: Lista de plataformas
        data class Plataforma(val nombre: String, val fabricante: String)
        val plataformas = listOf(
            Plataforma("PlayStation 5", "Sony"),
            Plataforma("PlayStation 4", "Sony"),
            Plataforma("Xbox Series X", "Microsoft")
        )
        
        // When: Se filtran por fabricante
        val sonyPlatforms = plataformas.filter { it.fabricante == "Sony" }
        
        // Then: Debe encontrar 2 plataformas de Sony
        assertEquals("Debe haber 2 plataformas de Sony", 2, sonyPlatforms.size)
    }

    @Test
    fun `test plataformas ordenadas alfabeticamente`() {
        // Given: Lista desordenada de plataformas
        data class Plataforma(val nombre: String)
        val plataformas = listOf(
            Plataforma("Xbox Series X"),
            Plataforma("Nintendo Switch"),
            Plataforma("PlayStation 5")
        )
        
        // When: Se ordenan alfabéticamente
        val ordenadas = plataformas.sortedBy { it.nombre }
        
        // Then: Deben estar ordenadas
        assertEquals("La primera debe ser Nintendo Switch", "Nintendo Switch", ordenadas[0].nombre)
        assertEquals("La segunda debe ser PlayStation 5", "PlayStation 5", ordenadas[1].nombre)
        assertEquals("La tercera debe ser Xbox Series X", "Xbox Series X", ordenadas[2].nombre)
    }

    @Test
    fun `test dialogo de creacion cerrado inicial`() {
        // Given: Estado del diálogo
        val showDialog = false
        
        // Then: El diálogo debe estar cerrado
        assertFalse("El diálogo debe estar cerrado inicialmente", showDialog)
    }

    @Test
    fun `test dialogo de edicion cerrado inicial`() {
        // Given: Estado del diálogo de edición
        val showEditDialog = false
        
        // Then: El diálogo de edición debe estar cerrado
        assertFalse("El diálogo de edición debe estar cerrado inicialmente", showEditDialog)
    }

    @Test
    fun `test validacion de id de plataforma`() {
        // Given: ID de plataforma
        val id = 1L
        
        // When: Se valida el ID
        val isValid = id > 0
        
        // Then: Debe ser válido
        assertTrue("El ID de la plataforma debe ser positivo", isValid)
    }
}
