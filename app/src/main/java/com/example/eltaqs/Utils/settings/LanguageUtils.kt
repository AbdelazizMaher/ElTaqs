package com.example.eltaqs.Utils.settings


import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.eltaqs.Utils.settings.enums.Language
import java.util.Locale



fun String.toArabicNumbers(): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return this.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
}

fun String.formatBasedOnLanguage(): String {
    return if (Locale.getDefault().language == Language.ARABIC.apiCode) this.toArabicNumbers() else this
}


