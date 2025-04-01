package com.example.eltaqs.utils.settings

import com.example.eltaqs.utils.settings.enums.Language
import java.util.Locale



fun String.toArabicNumbers(): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun String.formatBasedOnLanguage(): String {
    return if (Locale.getDefault().language == Language.ARABIC.apiCode) this.toArabicNumbers() else this
}


