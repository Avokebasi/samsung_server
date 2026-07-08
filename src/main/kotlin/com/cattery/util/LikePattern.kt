package com.cattery.util

object LikePattern {
    fun escape(value: String): String =
        value
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")

    fun containsPattern(raw: String): String = "%${escape(raw.trim().lowercase())}%"

    fun tokens(query: String): List<String> =
        query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
}
