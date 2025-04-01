package com.example.eltaqs.utils.settings.enums

enum class LocationSource(val translations: Map<Language, String>) {
    GPS(mapOf(Language.ENGLISH to "GPS", Language.ARABIC to "نظام تحديد المواقع")),
    MAP(mapOf(Language.ENGLISH to "Map", Language.ARABIC to "الخريطة"));

    fun getDisplayName(language: Language): String {
        return translations[language] ?: translations[Language.ENGLISH]!!
    }

    companion object {
        fun fromDisplayName(displayName: String, language: Language): LocationSource {
            return entries.find { it.getDisplayName(language) == displayName } ?: GPS
        }
    }
}
