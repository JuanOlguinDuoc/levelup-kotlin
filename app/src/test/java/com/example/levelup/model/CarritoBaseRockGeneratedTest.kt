package com.example.levelup.model

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarritoBaseRockGeneratedTest {

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct creation with valid parameters`() {
        // Given
        val productId = 1L
        val quantity = 5
        
        // When
        val cartProduct = CartProduct(productId, quantity)
        
        // Then
        assertThat(cartProduct.productId, equalTo(1L))
        assertThat(cartProduct.quantity, equalTo(5))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct creation with zero quantity`() {
        // Given
        val productId = 2L
        val quantity = 0
        
        // When
        val cartProduct = CartProduct(productId, quantity)
        
        // Then
        assertThat(cartProduct.productId, equalTo(2L))
        assertThat(cartProduct.quantity, equalTo(0))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct creation with negative quantity`() {
        // Given
        val productId = 3L
        val quantity = -1
        
        // When
        val cartProduct = CartProduct(productId, quantity)
        
        // Then
        assertThat(cartProduct.productId, equalTo(3L))
        assertThat(cartProduct.quantity, equalTo(-1))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct creation with maximum long value`() {
        // Given
        val productId = Long.MAX_VALUE
        val quantity = Int.MAX_VALUE
        
        // When
        val cartProduct = CartProduct(productId, quantity)
        
        // Then
        assertThat(cartProduct.productId, equalTo(Long.MAX_VALUE))
        assertThat(cartProduct.quantity, equalTo(Int.MAX_VALUE))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct creation with minimum long value`() {
        // Given
        val productId = Long.MIN_VALUE
        val quantity = Int.MIN_VALUE
        
        // When
        val cartProduct = CartProduct(productId, quantity)
        
        // Then
        assertThat(cartProduct.productId, equalTo(Long.MIN_VALUE))
        assertThat(cartProduct.quantity, equalTo(Int.MIN_VALUE))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito creation with null id and empty products`() {
        // Given
        val userId = 1L
        val date = "2023-01-01"
        
        // When
        val carrito = Carrito(userId = userId, date = date)
        
        // Then
        assertThat(carrito.id, nullValue())
        assertThat(carrito.userId, equalTo(1L))
        assertThat(carrito.date, equalTo("2023-01-01"))
        assertThat(carrito.products, hasSize(0))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito creation with specific id`() {
        // Given
        val id = 100L
        val userId = 2L
        val date = "2023-02-15"
        
        // When
        val carrito = Carrito(id = id, userId = userId, date = date)
        
        // Then
        assertThat(carrito.id, equalTo(100L))
        assertThat(carrito.userId, equalTo(2L))
        assertThat(carrito.date, equalTo("2023-02-15"))
        assertThat(carrito.products, hasSize(0))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito creation with single product`() {
        // Given
        val userId = 3L
        val date = "2023-03-20"
        val cartProduct = CartProduct(productId = 10L, quantity = 2)
        val products = listOf(cartProduct)
        
        // When
        val carrito = Carrito(userId = userId, date = date, products = products)
        
        // Then
        assertThat(carrito.id, nullValue())
        assertThat(carrito.userId, equalTo(3L))
        assertThat(carrito.date, equalTo("2023-03-20"))
        assertThat(carrito.products, hasSize(1))
        assertThat(carrito.products[0].productId, equalTo(10L))
        assertThat(carrito.products[0].quantity, equalTo(2))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito creation with multiple products`() {
        // Given
        val userId = 4L
        val date = "2023-04-10"
        val product1 = CartProduct(productId = 20L, quantity = 3)
        val product2 = CartProduct(productId = 30L, quantity = 1)
        val product3 = CartProduct(productId = 40L, quantity = 5)
        val products = listOf(product1, product2, product3)
        
        // When
        val carrito = Carrito(userId = userId, date = date, products = products)
        
        // Then
        assertThat(carrito.id, nullValue())
        assertThat(carrito.userId, equalTo(4L))
        assertThat(carrito.date, equalTo("2023-04-10"))
        assertThat(carrito.products, hasSize(3))
        assertThat(carrito.products[0].productId, equalTo(20L))
        assertThat(carrito.products[0].quantity, equalTo(3))
        assertThat(carrito.products[1].productId, equalTo(30L))
        assertThat(carrito.products[1].quantity, equalTo(1))
        assertThat(carrito.products[2].productId, equalTo(40L))
        assertThat(carrito.products[2].quantity, equalTo(5))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito creation with all parameters including id`() {
        // Given
        val id = 500L
        val userId = 5L
        val date = "2023-05-25"
        val product = CartProduct(productId = 50L, quantity = 10)
        val products = listOf(product)
        
        // When
        val carrito = Carrito(id = id, userId = userId, date = date, products = products)
        
        // Then
        assertThat(carrito.id, equalTo(500L))
        assertThat(carrito.userId, equalTo(5L))
        assertThat(carrito.date, equalTo("2023-05-25"))
        assertThat(carrito.products, hasSize(1))
        assertThat(carrito.products[0].productId, equalTo(50L))
        assertThat(carrito.products[0].quantity, equalTo(10))
    }

    @ParameterizedTest
    @MethodSource("provideCartProductTestData")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct with various parameter combinations`(productId: Long, quantity: Int) {
        // When
        val cartProduct = CartProduct(productId, quantity)
        
        // Then
        assertThat(cartProduct.productId, equalTo(productId))
        assertThat(cartProduct.quantity, equalTo(quantity))
    }

    @ParameterizedTest
    @MethodSource("provideCarritoTestData")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito with various parameter combinations`(id: Long?, userId: Long, date: String, productCount: Int) {
        // Given
        val products = (1..productCount).map { CartProduct(it.toLong(), it) }
        
        // When
        val carrito = Carrito(id = id, userId = userId, date = date, products = products)
        
        // Then
        assertThat(carrito.id, equalTo(id))
        assertThat(carrito.userId, equalTo(userId))
        assertThat(carrito.date, equalTo(date))
        assertThat(carrito.products, hasSize(productCount))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test CartProduct data class properties`() {
        // Given
        val cartProduct1 = CartProduct(1L, 5)
        val cartProduct2 = CartProduct(1L, 5)
        val cartProduct3 = CartProduct(2L, 5)
        
        // Then
        assertThat(cartProduct1, equalTo(cartProduct2))
        assertThat(cartProduct1 == cartProduct3, equalTo(false))
        assertThat(cartProduct1.toString(), notNullValue())
        assertThat(cartProduct1.hashCode(), equalTo(cartProduct2.hashCode()))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito data class properties`() {
        // Given
        val product = CartProduct(1L, 1)
        val carrito1 = Carrito(1L, 1L, "2023-01-01", listOf(product))
        val carrito2 = Carrito(1L, 1L, "2023-01-01", listOf(product))
        val carrito3 = Carrito(2L, 1L, "2023-01-01", listOf(product))
        
        // Then
        assertThat(carrito1, equalTo(carrito2))
        assertThat(carrito1 == carrito3, equalTo(false))
        assertThat(carrito1.toString(), notNullValue())
        assertThat(carrito1.hashCode(), equalTo(carrito2.hashCode()))
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `test Carrito with empty date string`() {
        // Given
        val userId = 6L
        val date = ""
        
        // When
        val carrito = Carrito(userId = userId, date = date)
        
        // Then
        assertThat(carrito.userId, equalTo(6L))
        assertThat(carrito.date, equalTo(""))
        assertThat(carrito.products, hasSize(0))
    }

    fun provideCartProductTestData(): List<Array<Any>> {
        return listOf(
            arrayOf(1L, 1),
            arrayOf(0L, 0),
            arrayOf(-1L, -1),
            arrayOf(100L, 50),
            arrayOf(Long.MAX_VALUE, Int.MAX_VALUE),
            arrayOf(Long.MIN_VALUE, Int.MIN_VALUE)
        )
    }

    fun provideCarritoTestData(): List<Array<Any?>> {
        return listOf(
            arrayOf(null, 1L, "2023-01-01", 0),
            arrayOf(1L, 1L, "2023-01-01", 1),
            arrayOf(100L, 2L, "2023-02-15", 3),
            arrayOf(null, 0L, "", 0),
            arrayOf(Long.MAX_VALUE, Long.MAX_VALUE, "2023-12-31", 5)
        )
    }
}