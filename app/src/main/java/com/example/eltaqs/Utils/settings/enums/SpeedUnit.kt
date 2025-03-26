package com.example.eltaqs.Utils.settings.enums

enum class SpeedUnit(val translations: Map<Language, String>, private val toMpsFactor: Double) {
    METER_PER_SECOND(mapOf(Language.ENGLISH to "m/s", Language.ARABIC to "متر/ثانية"), 1.0),
    KILOMETER_PER_HOUR(mapOf(Language.ENGLISH to "km/h", Language.ARABIC to "كم/ساعة"), 0.27778),
    MILE_PER_HOUR(mapOf(Language.ENGLISH to "mph", Language.ARABIC to "ميل/ساعة"), 0.44704);

    fun getDisplayName(language: Language): String {
        return translations[language] ?: translations[Language.ENGLISH]!! // Default to English
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
