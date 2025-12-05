package com.example.levelup.utils
object ValidationUtils {
    fun isNonEmpty(value: String): Boolean = value.trim().isNotEmpty()

    fun parsePositiveInt(value: String): Int? {
        val v = value.toIntOrNull()
        return if (v != null && v >= 0) v else null
    }
}
