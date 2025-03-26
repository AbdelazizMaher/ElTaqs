package com.example.eltaqs.Utils.settings.enums

enum class TemperatureUnit(private val names: Map<Language, String>) {
    KELVIN(mapOf(Language.ENGLISH to "K", Language.ARABIC to "كلفن")),
    CELSIUS(mapOf(Language.ENGLISH to "°C", Language.ARABIC to "درجة مئوية")),
    FAHRENHEIT(mapOf(Language.ENGLISH to "°F", Language.ARABIC to "فهرنهايت"));

    fun getDisplayName(language: Language): String {
        return names[language] ?: names[Language.ENGLISH]!!
    }
}