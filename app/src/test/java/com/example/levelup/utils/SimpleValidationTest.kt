package com.example.levelup.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SimpleValidationTest {

    @Test
    fun `validateUserFields should work correctly`() {
        assertTrue(validateUserFields("John", "Doe", "john@test.com", "12345678-9"))
        assertFalse(validateUserFields("", "Doe", "john@test.com", "12345678-9"))
        assertFalse(validateUserFields("John", "", "john@test.com", "12345678-9"))
    }

    @Test
    fun `validateProductFields should work correctly`() {
        assertTrue(validateProductFields("Product", "Description", "100", "10"))
        assertFalse(validateProductFields("", "Description", "100", "10"))
        assertFalse(validateProductFields("Product", "", "100", "10"))
        assertFalse(validateProductFields("Product", "Description", "invalid", "10"))
    }

    @Test
    fun `cleanAndTrimString should work correctly`() {
        assertEquals("Hello World", cleanAndTrimString("  Hello World  "))
        assertEquals("Test", cleanAndTrimString("Test"))
        assertEquals("", cleanAndTrimString(""))
        assertEquals("NoSpecial", cleanAndTrimString("No@#\$Special"))
    }

    @Test
    fun `formatChileanPhone should work correctly`() {
        assertEquals("+56912345678", formatChileanPhone("912345678"))
        assertEquals("+56987654321", formatChileanPhone("987654321"))
        assertEquals("", formatChileanPhone(""))
    }

    @Test
    fun `validateStockNumber should work correctly`() {
        assertTrue(validateStockNumber("10"))
        assertTrue(validateStockNumber("0"))
        assertFalse(validateStockNumber("-1"))
        assertFalse(validateStockNumber("abc"))
        assertFalse(validateStockNumber(""))
    }

    // Helper functions
    private fun validateUserFields(firstName: String, lastName: String, email: String, run: String): Boolean {
        return firstName.isNotBlank() && 
               lastName.isNotBlank() && 
               email.contains("@") && 
               run.length >= 9
    }

    private fun validateProductFields(name: String, description: String, price: String, stock: String): Boolean {
        return name.isNotBlank() && 
               description.isNotBlank() && 
               price.toDoubleOrNull() != null && 
               stock.toIntOrNull() != null
    }

    private fun cleanAndTrimString(input: String): String {
        return input.trim()
            .replace(Regex("[^a-zA-Z0-9\\s]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun formatChileanPhone(phone: String): String {
        return if (phone.length == 9 && phone.startsWith("9")) {
            "+56$phone"
        } else {
            ""
        }
    }

    private fun validateStockNumber(stock: String): Boolean {
        val stockInt = stock.toIntOrNull()
        return stockInt != null && stockInt >= 0
    }
}
