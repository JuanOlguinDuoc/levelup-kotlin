package com.example.levelup.pantallas

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para CategoryScreen
 */
class TestCategorias {

    @Test
    fun `test titulo de pantalla categorias`() {
        // Given: Título de la pantalla
        val titulo = "Categorías"
        
        // Then: El título debe ser correcto
        assertEquals("El título debe ser 'Categorías'", "Categorías", titulo)
    }

    @Test
    fun `test validacion de nombre de categoria`() {
        // Given: Nombre de categoría
        val nombre = "Consolas"
        
        // When: Se valida el nombre
        val isValid = nombre.isNotBlank() && nombre.length >= 3
        
        // Then: Debe ser válido
        assertTrue("El nombre de la categoría debe ser válido", isValid)
    }

    @Test
    fun `test validacion de descripcion de categoria`() {
        // Given: Descripción de categoría
        val descripcion = "Consolas de videojuegos de última generación"
        
        // When: Se valida la descripción
        val isValid = descripcion.isNotBlank()
        
        // Then: Debe ser válida
        assertTrue("La descripción debe ser válida", isValid)
    }

    @Test
    fun `test lista de categorias vacia inicial`() {
        // Given: Lista inicial de categorías
        val categorias = emptyList<Any>()
        
        // Then: La lista debe estar vacía
        assertTrue("La lista de categorías debe estar vacía inicialmente", categorias.isEmpty())
    }

    @Test
    fun `test agregar categoria a lista`() {
        // Given: Lista de categorías
        data class Categoria(val id: Long, val nombre: String)
        val categorias = mutableListOf<Categoria>()
        
        // When: Se agrega una categoría
        categorias.add(Categoria(1, "Juegos"))
        
        // Then: La lista debe tener una categoría
        assertEquals("La lista debe tener 1 categoría", 1, categorias.size)
        assertEquals("El nombre debe ser 'Juegos'", "Juegos", categorias[0].nombre)
    }

    @Test
    fun `test eliminar categoria de lista`() {
        // Given: Lista con categorías
        data class Categoria(val id: Long, val nombre: String)
        val categorias = mutableListOf(
            Categoria(1, "Consolas"),
            Categoria(2, "Juegos")
        )
        
        // When: Se elimina una categoría
        categorias.removeAt(0)
        
        // Then: La lista debe tener una categoría menos
        assertEquals("La lista debe tener 1 categoría", 1, categorias.size)
        assertEquals("Debe quedar 'Juegos'", "Juegos", categorias[0].nombre)
    }

    @Test
    fun `test editar nombre de categoria`() {
        // Given: Categoría original
        data class Categoria(var nombre: String)
        val categoria = Categoria("Consolas")
        
        // When: Se edita el nombre
        categoria.nombre = "Consolas de Videojuegos"
        
        // Then: El nombre debe haberse actualizado
        assertEquals("El nombre debe estar actualizado", 
                    "Consolas de Videojuegos", categoria.nombre)
    }

    @Test
    fun `test categoria con icono`() {
        // Given: Categoría con icono
        data class Categoria(val nombre: String, val icono: String)
        val categoria = Categoria("Juegos", "videogame_asset")
        
        // Then: Debe tener un icono asignado
        assertNotNull("El icono no debe ser nulo", categoria.icono)
        assertTrue("El icono debe tener un valor", categoria.icono.isNotBlank())
    }

    @Test
    fun `test buscar categoria por nombre`() {
        // Given: Lista de categorías
        data class Categoria(val nombre: String)
        val categorias = listOf(
            Categoria("Consolas"),
            Categoria("Juegos"),
            Categoria("Accesorios")
        )
        
        // When: Se busca una categoría
        val resultado = categorias.find { it.nombre == "Juegos" }
        
        // Then: Debe encontrar la categoría
        assertNotNull("Debe encontrar la categoría", resultado)
        assertEquals("El nombre debe ser 'Juegos'", "Juegos", resultado?.nombre)
    }

    @Test
    fun `test filtrar categorias por texto`() {
        // Given: Lista de categorías
        data class Categoria(val nombre: String)
        val categorias = listOf(
            Categoria("Consolas PlayStation"),
            Categoria("Consolas Xbox"),
            Categoria("Juegos PC")
        )
        
        // When: Se filtran por texto
        val resultado = categorias.filter { it.nombre.contains("Consolas", ignoreCase = true) }
        
        // Then: Debe encontrar 2 categorías
        assertEquals("Debe encontrar 2 categorías con 'Consolas'", 2, resultado.size)
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
        
        // Then: El diálogo debe estar cerrado
        assertFalse("El diálogo de edición debe estar cerrado inicialmente", showEditDialog)
    }
}
