package com.example.levelup.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SimpleStringUtilsTest {

    @Test
    fun `formatPrice should format correctly`() {
        assertEquals("$1.000", formatPrice(1000))
        assertEquals("$10.000", formatPrice(10000))
        assertEquals("$0", formatPrice(0))
    }

    @Test
    fun `capitalizeWords should work correctly`() {
        assertEquals("Juan Carlos", capitalizeWords("juan carlos"))
        assertEquals("José", capitalizeWords("josé"))
        assertEquals("", capitalizeWords(""))
    }

    @Test
    fun `validatePassword should work correctly`() {
        assertTrue(isValidPassword("Password123!"))
        assertFalse(isValidPassword("password"))
        assertFalse(isValidPassword("123456"))
    }

    @Test
    fun `validateEmail should work correctly`() {
        assertTrue(isValidEmail("admin@levelup.cl"))
        assertFalse(isValidEmail("invalid"))
        assertFalse(isValidEmail(""))
    }

    // Helper functions
    private fun formatPrice(amount: Int): String {
        return when {
            amount == 0 -> "$0"
            amount < 1000 -> "$$amount"
            else -> {
                val formatted = amount.toString()
                    .reversed()
                    .chunked(3)
                    .joinToString(".")
                    .reversed()
                "$$formatted"
            }
        }
    }

    private fun capitalizeWords(text: String): String {
        return text.lowercase()
            .split(" ")
            .joinToString(" ") { word ->
                if (word.isNotEmpty()) {
                    word.first().uppercase() + word.drop(1)
                } else word
            }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isEmpty()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
}
