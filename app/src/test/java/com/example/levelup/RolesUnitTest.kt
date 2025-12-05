package com.example.levelup

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import java.util.*

// Simulamos las clases del backend para las pruebas
data class Rol(
    val id: Long? = null,
    val nombre: String
) {
    constructor(nombre: String) : this(null, nombre)
    
    companion object {
        const val CLIENTE = "cliente"
        const val VENDEDOR = "vendedor" 
        const val ADMIN = "admin"
        
        val ROLES_VALIDOS = listOf(CLIENTE, VENDEDOR, ADMIN)
        
        fun esRolValido(nombre: String): Boolean {
            return ROLES_VALIDOS.contains(nombre.lowercase())
        }
    }
}

data class RolDto(
    val id: Long? = null,
    val nombre: String
) {
    companion object {
        fun desdeEntidad(rol: Rol): RolDto {
            return RolDto(rol.id, rol.nombre)
        }
        
        fun hastaEntidad(dto: RolDto): Rol {
            return Rol(dto.id, dto.nombre)
        }
    }
}

interface RepositorioRol {
    fun buscarTodos(): List<Rol>
    fun guardar(rol: Rol): Rol
    fun buscarPorId(id: Long): Optional<Rol>
    fun existePorId(id: Long): Boolean
    fun eliminarPorId(id: Long)
    fun buscarPorNombre(nombre: String): Optional<Rol>
}

class ServicioRol {
    lateinit var repoRol: RepositorioRol
    
    constructor(repositorioRol: RepositorioRol) {
        this.repoRol = repositorioRol
    }
    
    fun obtenerRoles(): List<RolDto> {
        return repoRol.buscarTodos().map { RolDto.desdeEntidad(it) }
    }
    
    fun crearRol(dto: RolDto): RolDto {
        // Validar que el rol sea válido
        if (!Rol.esRolValido(dto.nombre)) {
            throw IllegalArgumentException("Rol no válido. Solo se permiten: ${Rol.ROLES_VALIDOS.joinToString(", ")}")
        }
        
        val rol = RolDto.hastaEntidad(dto)
        val guardado = repoRol.guardar(rol)
        return RolDto.desdeEntidad(guardado)
    }
    
    fun buscarPorId(id: Long): RolDto? {
        val rolOpcional = repoRol.buscarPorId(id)
        return if (rolOpcional.isPresent) {
            RolDto.desdeEntidad(rolOpcional.get())
        } else {
            null
        }
    }
    
    fun actualizarRol(id: Long, dto: RolDto): RolDto {
        val rolOpcional = repoRol.buscarPorId(id)
        if (!rolOpcional.isPresent) {
            throw IllegalArgumentException("Rol no encontrado")
        }
        
        // Validar que el nuevo nombre de rol sea válido
        if (!Rol.esRolValido(dto.nombre)) {
            throw IllegalArgumentException("Rol no válido. Solo se permiten: ${Rol.ROLES_VALIDOS.joinToString(", ")}")
        }
        
        val existente = rolOpcional.get()
        val actualizado = existente.copy(nombre = dto.nombre)
        val guardado = repoRol.guardar(actualizado)
        return RolDto.desdeEntidad(guardado)
    }
    
    fun eliminarRol(id: Long) {
        if (!repoRol.existePorId(id)) {
            throw IllegalArgumentException("Rol no encontrado")
        }
        repoRol.eliminarPorId(id)
    }
}

class PruebasRoles {
    
    @Mock
    private lateinit var repositorioRol: RepositorioRol
    
    private lateinit var servicioRol: ServicioRol
    
    @Before
    fun configurar() {
        MockitoAnnotations.openMocks(this)
        servicioRol = ServicioRol(repositorioRol)
    }
    
    // Pruebas para el modelo Rol
    @Test
    fun `debe crear rol con id y nombre`() {
        val rol = Rol(1L, "admin")
        
        assertEquals(1L, rol.id)
        assertEquals("admin", rol.nombre)
    }
    
    @Test
    fun `debe crear rol solo con nombre`() {
        val rol = Rol("cliente")
        
        assertNull(rol.id)
        assertEquals("cliente", rol.nombre)
    }
    
    @Test
    fun `debe copiar rol con nuevo nombre`() {
        val rol = Rol(1L, "admin")
        val rolCopiado = rol.copy(nombre = "vendedor")
        
        assertEquals(1L, rolCopiado.id)
        assertEquals("vendedor", rolCopiado.nombre)
    }
    
    @Test
    fun `debe validar roles permitidos correctamente`() {
        assertTrue(Rol.esRolValido("cliente"))
        assertTrue(Rol.esRolValido("vendedor"))
        assertTrue(Rol.esRolValido("admin"))
        assertTrue(Rol.esRolValido("CLIENTE")) // Case insensitive
        assertFalse(Rol.esRolValido("usuario"))
        assertFalse(Rol.esRolValido("moderador"))
        assertFalse(Rol.esRolValido(""))
    }
    
    @Test
    fun `debe tener constantes de roles correctas`() {
        assertEquals("cliente", Rol.CLIENTE)
        assertEquals("vendedor", Rol.VENDEDOR)
        assertEquals("admin", Rol.ADMIN)
        assertEquals(3, Rol.ROLES_VALIDOS.size)
    }
    
    // Pruebas para RolDto
    @Test
    fun `debe crear RolDto correctamente`() {
        val dto = RolDto(1L, "cliente")
        
        assertEquals(1L, dto.id)
        assertEquals("cliente", dto.nombre)
    }
    
    @Test
    fun `debe convertir desde entidad a DTO`() {
        val rol = Rol(1L, "admin")
        val dto = RolDto.desdeEntidad(rol)
        
        assertEquals(rol.id, dto.id)
        assertEquals(rol.nombre, dto.nombre)
    }
    
    @Test
    fun `debe convertir desde DTO a entidad`() {
        val dto = RolDto(2L, "vendedor")
        val rol = RolDto.hastaEntidad(dto)
        
        assertEquals(dto.id, rol.id)
        assertEquals(dto.nombre, rol.nombre)
    }
    
    // Pruebas para ServicioRol
    @Test
    fun `debe obtener lista de roles como DTOs`() {
        // Preparar
        val roles = listOf(
            Rol(1L, "admin"),
            Rol(2L, "vendedor"),
            Rol(3L, "cliente")
        )
        whenever(repositorioRol.buscarTodos()).thenReturn(roles)
        
        // Actuar
        val resultado = servicioRol.obtenerRoles()
        
        // Verificar
        assertEquals(3, resultado.size)
        assertEquals("admin", resultado[0].nombre)
        assertEquals("vendedor", resultado[1].nombre)
        assertEquals("cliente", resultado[2].nombre)
        verify(repositorioRol, times(1)).buscarTodos()
    }
    
    @Test
    fun `debe retornar lista vacía cuando no hay roles`() {
        // Preparar
        whenever(repositorioRol.buscarTodos()).thenReturn(emptyList())
        
        // Actuar
        val resultado = servicioRol.obtenerRoles()
        
        // Verificar
        assertTrue(resultado.isEmpty())
        verify(repositorioRol, times(1)).buscarTodos()
    }
    
    @Test
    fun `debe crear rol válido correctamente`() {
        // Preparar
        val dtoEntrada = RolDto(null, "cliente")
        val rolGuardado = Rol(1L, "cliente")
        whenever(repositorioRol.guardar(any<Rol>())).thenReturn(rolGuardado)
        
        // Actuar
        val resultado = servicioRol.crearRol(dtoEntrada)
        
        // Verificar
        assertNotNull(resultado)
        assertEquals(1L, resultado.id)
        assertEquals("cliente", resultado.nombre)
        verify(repositorioRol, times(1)).guardar(any<Rol>())
    }
    
    @Test
    fun `debe lanzar excepción al crear rol inválido`() {
        // Preparar
        val dtoInvalido = RolDto(null, "usuario_invalido")
        
        // Actuar y Verificar
        try {
            servicioRol.crearRol(dtoInvalido)
            fail("Se esperaba IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Rol no válido"))
            assertTrue(e.message!!.contains("cliente, vendedor, admin"))
        }
        
        verify(repositorioRol, times(0)).guardar(any<Rol>())
    }
    
    @Test
    fun `debe buscar rol por ID cuando existe`() {
        // Preparar
        val rol = Rol(1L, "admin")
        whenever(repositorioRol.buscarPorId(1L)).thenReturn(Optional.of(rol))
        
        // Actuar
        val resultado = servicioRol.buscarPorId(1L)
        
        // Verificar
        assertNotNull(resultado)
        assertEquals(1L, resultado?.id)
        assertEquals("admin", resultado?.nombre)
        verify(repositorioRol, times(1)).buscarPorId(1L)
    }
    
    @Test
    fun `debe retornar null al buscar rol inexistente`() {
        // Preparar
        whenever(repositorioRol.buscarPorId(999L)).thenReturn(Optional.empty())
        
        // Actuar
        val resultado = servicioRol.buscarPorId(999L)
        
        // Verificar
        assertNull(resultado)
        verify(repositorioRol, times(1)).buscarPorId(999L)
    }
    
    @Test
    fun `debe actualizar rol existente con nombre válido`() {
        // Preparar
        val rolExistente = Rol(1L, "cliente")
        val dtoActualizacion = RolDto(1L, "vendedor")
        val rolActualizado = Rol(1L, "vendedor")
        
        whenever(repositorioRol.buscarPorId(1L)).thenReturn(Optional.of(rolExistente))
        whenever(repositorioRol.guardar(any<Rol>())).thenReturn(rolActualizado)
        
        // Actuar
        val resultado = servicioRol.actualizarRol(1L, dtoActualizacion)
        
        // Verificar
        assertEquals(1L, resultado.id)
        assertEquals("vendedor", resultado.nombre)
        verify(repositorioRol, times(1)).buscarPorId(1L)
        verify(repositorioRol, times(1)).guardar(any<Rol>())
    }
    
    @Test
    fun `debe lanzar excepción al actualizar rol inexistente`() {
        // Preparar
        val dtoActualizacion = RolDto(999L, "admin")
        whenever(repositorioRol.buscarPorId(999L)).thenReturn(Optional.empty())
        
        // Actuar y Verificar
        try {
            servicioRol.actualizarRol(999L, dtoActualizacion)
            fail("Se esperaba IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Rol no encontrado", e.message)
        }
        
        verify(repositorioRol, times(1)).buscarPorId(999L)
        verify(repositorioRol, times(0)).guardar(any<Rol>())
    }
    
    @Test
    fun `debe lanzar excepción al actualizar con nombre de rol inválido`() {
        // Preparar
        val rolExistente = Rol(1L, "admin")
        val dtoInvalido = RolDto(1L, "super_usuario")
        whenever(repositorioRol.buscarPorId(1L)).thenReturn(Optional.of(rolExistente))
        
        // Actuar y Verificar
        try {
            servicioRol.actualizarRol(1L, dtoInvalido)
            fail("Se esperaba IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Rol no válido"))
        }
        
        verify(repositorioRol, times(1)).buscarPorId(1L)
        verify(repositorioRol, times(0)).guardar(any<Rol>())
    }
    
    @Test
    fun `debe eliminar rol existente`() {
        // Preparar
        whenever(repositorioRol.existePorId(1L)).thenReturn(true)
        
        // Actuar
        servicioRol.eliminarRol(1L)
        
        // Verificar
        verify(repositorioRol, times(1)).existePorId(1L)
        verify(repositorioRol, times(1)).eliminarPorId(1L)
    }
    
    @Test
    fun `debe lanzar excepción al eliminar rol inexistente`() {
        // Preparar
        whenever(repositorioRol.existePorId(999L)).thenReturn(false)
        
        // Actuar y Verificar
        try {
            servicioRol.eliminarRol(999L)
            fail("Se esperaba IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Rol no encontrado", e.message)
        }
        
        verify(repositorioRol, times(1)).existePorId(999L)
        verify(repositorioRol, times(0)).eliminarPorId(999L)
    }
    
    // Pruebas para casos especiales
    @Test
    fun `debe manejar nombres de rol con mayúsculas y minúsculas`() {
        val rolMinusculas = Rol("admin")
        val rolMayusculas = Rol("CLIENTE")
        val rolMixto = Rol("VeNdEdOr")
        
        assertEquals("admin", rolMinusculas.nombre)
        assertEquals("CLIENTE", rolMayusculas.nombre)
        assertEquals("VeNdEdOr", rolMixto.nombre)
        
        // La validación debe ser case-insensitive
        assertTrue(Rol.esRolValido("ADMIN"))
        assertTrue(Rol.esRolValido("Cliente"))
        assertTrue(Rol.esRolValido("VeNdEdOr"))
    }
    
    @Test
    fun `debe validar solo los tres roles permitidos`() {
        // Roles válidos
        val rolesValidos = listOf("cliente", "vendedor", "admin")
        rolesValidos.forEach { rol ->
            assertTrue("$rol debería ser válido", Rol.esRolValido(rol))
        }
        
        // Roles inválidos
        val rolesInvalidos = listOf("usuario", "moderador", "super_admin", "guest", "")
        rolesInvalidos.forEach { rol ->
            assertFalse("$rol no debería ser válido", Rol.esRolValido(rol))
        }
    }
    
    @Test
    fun `debe crear todos los tipos de rol válidos`() {
        val cliente = Rol("cliente")
        val vendedor = Rol("vendedor")
        val admin = Rol("admin")
        
        assertEquals("cliente", cliente.nombre)
        assertEquals("vendedor", vendedor.nombre)
        assertEquals("admin", admin.nombre)
        
        assertTrue(Rol.esRolValido(cliente.nombre))
        assertTrue(Rol.esRolValido(vendedor.nombre))
        assertTrue(Rol.esRolValido(admin.nombre))
    }
}