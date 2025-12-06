package com.example.levelup.utils
object ValidationUtils {
    fun isNonEmpty(value: String): Boolean = value.trim().isNotEmpty()

    fun parsePositiveInt(value: String): Int? {
        val v = value.toIntOrNull()
        return if (v != null && v >= 0) v else null
    }

    fun parsePositiveLong(value: String): Long? {
        val v = value.toLongOrNull()
        return if (v != null && v >= 0L) v else null
    }
}
