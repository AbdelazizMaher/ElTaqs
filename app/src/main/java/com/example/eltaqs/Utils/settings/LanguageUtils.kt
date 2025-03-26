package com.example.eltaqs.Utils.settings


import android.app.Activity
import android.content.Context
import android.content.Intent
import java.util.Locale

object LanguageUtils {

    fun convertToArabicNumbers(number: String): String {
        val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }

    fun formatNumberBasedOnLanguage(number: String): String {
        val language = Locale.getDefault().language
        return if (language == "ar") convertToArabicNumbers(number) else number
    }

    fun restartActivity(context: Context) {
        val intent = (context as? Activity)?.intent
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        (context as? Activity)?.finish()
    }
}