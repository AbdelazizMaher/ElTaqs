package com.example.eltaqs.utils.settings.enums

enum class SpeedUnit(
    val translations: Map<Language, String>,
    private val toMpsFactor: Double,
    val apiUnit: String
) {
    METER_PER_SECOND(mapOf(Language.ENGLISH to "m/s", Language.ARABIC to "متر/ثانية"), 1.0, "metric"),
    MILE_PER_HOUR(mapOf(Language.ENGLISH to "mph", Language.ARABIC to "ميل/ساعة"), 0.44704, "imperial");

    fun getDisplayName(language: Language): String {
        return translations[language] ?: translations[Language.ENGLISH]!!
    }

    fun convert(value: Double, targetUnit: SpeedUnit): Double {
        return (value * this.toMpsFactor) / targetUnit.toMpsFactor
    }

    companion object {
        fun fromDisplayName(displayName: String, language: Language): SpeedUnit {
            return entries.find { it.getDisplayName(language) == displayName } ?: METER_PER_SECOND
        }
    }
}

