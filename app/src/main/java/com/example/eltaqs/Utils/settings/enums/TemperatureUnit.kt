package com.example.eltaqs.Utils.settings.enums

enum class TemperatureUnit(
    private val displayNames: Map<Language, String>,
    private val symbols: Map<Language, String>,
    val apiUnit: String
) {
    KELVIN(
        mapOf(Language.ENGLISH to "Kelvin", Language.ARABIC to "كلفن"),
        mapOf(Language.ENGLISH to "°K", Language.ARABIC to "°ك"),
        "standard"
    ),
    CELSIUS(
        mapOf(Language.ENGLISH to "Celsius", Language.ARABIC to "درجة مئوية"),
        mapOf(Language.ENGLISH to "°C", Language.ARABIC to "°م"),
        "metric"
    ),
    FAHRENHEIT(
        mapOf(Language.ENGLISH to "Fahrenheit", Language.ARABIC to "فهرنهايت"),
        mapOf(Language.ENGLISH to "°F", Language.ARABIC to "°ف"),
        "imperial"
    );

    fun getDisplayName(language: Language): String {
        return displayNames[language] ?: displayNames[Language.ENGLISH]!!
    }

    fun getSymbol(language: Language): String {
        return symbols[language] ?: symbols[Language.ENGLISH]!!
    }
}
