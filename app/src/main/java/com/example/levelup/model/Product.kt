package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductModel(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val stock: Int
)