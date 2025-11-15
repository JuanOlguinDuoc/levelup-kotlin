package com.example.levelup.utils

// Utility object que agrupa validaciones sencillas reutilizables en la app
object ValidationUtils {
    // Comprueba que una cadena no sea vacía (elimina espacios al inicio/fin)
    fun isNonEmpty(value: String): Boolean = value.trim().isNotEmpty()

    // Intenta convertir una cadena a Int y devuelve el entero si es >= 0,
    // o null si la cadena no representa un número válido o es negativo.
    fun parsePositiveInt(value: String): Int? {
        // Intenta parsear la cadena a entero; si falla devuelve null
        val v = value.toIntOrNull()
        // Si v no es null y es >= 0 devolvemos el valor, en otro caso null
        return if (v != null && v >= 0) v else null
    }
}
