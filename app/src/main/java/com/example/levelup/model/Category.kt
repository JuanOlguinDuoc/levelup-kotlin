package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String  //usaré la ruta de la imagen aquí (no olvidar)
)

