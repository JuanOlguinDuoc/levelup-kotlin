package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: Int,
    val run: String,
    val direccion: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val password: String
)