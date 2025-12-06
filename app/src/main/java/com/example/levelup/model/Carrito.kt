
package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class CartProduct(
	val productId: Long,
	val quantity: Int
)

@Serializable
data class Carrito(
	val id: Long? = null,
	val userId: Long,
	val date: String,
	val products: List<CartProduct> = emptyList()
)
