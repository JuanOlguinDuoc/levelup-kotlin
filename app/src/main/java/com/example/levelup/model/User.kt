package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Long = 0,  // Cambio: Int -> Long para coincidir con backend
    val run: String = "",
    val firstName: String = "",  
    val lastName: String = "",
    val email: String = "",     
    val password: String = "",
    val role: String = "usuario",
    // Campo extra para uso local (no se envía al backend)
    val direccion: String = ""
) {
    // Propiedades computadas para compatibilidad con UI existente
    val nombres: String get() = firstName
    val apellidos: String get() = lastName  
    val correo: String get() = email
}