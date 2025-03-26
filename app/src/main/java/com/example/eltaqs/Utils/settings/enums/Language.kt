package com.example.eltaqs.Utils.settings.enums

import java.util.Locale

enum class Language(val locale: Locale, val arabicName: String) {
    ENGLISH(Locale.ENGLISH, "الإنجليزية"),
    ARABIC(Locale("ar"), "العربية");
}