package com.example.eltaqs.Utils.settings.enums

import java.util.Locale

enum class Language(val locale: Locale, val displayNameInEnglish: String, val displayNameInArabic: String) {
    ENGLISH(Locale.ENGLISH, "English", "الإنجليزية"),
    ARABIC(Locale("ar"), "Arabic", "العربية");

    fun getDisplayName(currentLanguage: Language): String {
        return when (currentLanguage) {
            ENGLISH -> displayNameInEnglish
            ARABIC -> displayNameInArabic
        }
    }
}