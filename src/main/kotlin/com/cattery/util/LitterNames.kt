package com.cattery.util

object LitterNames {
    private val pattern = Regex("^[A-Z][0-9]*$")

    fun normalize(raw: String): String = raw.trim().uppercase()

    fun validate(name: String) {
        val normalized = normalize(name)
        if (!pattern.matches(normalized)) {
            throw IllegalArgumentException("Название помёта: латинские буквы A–Z, затем A1, B1…")
        }
    }
}
