package com.cattery.util

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

object PhotoUrlsJson {
    private val json = Json { ignoreUnknownKeys = true }

    fun encode(urls: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), urls)

    fun decode(raw: String?): List<String> {
        if (raw.isNullOrBlank() || raw == "[]") return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(String.serializer()), raw)
        }.getOrDefault(emptyList())
    }
}
