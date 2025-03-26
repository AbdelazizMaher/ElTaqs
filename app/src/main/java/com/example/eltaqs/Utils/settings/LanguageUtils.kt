package com.example.eltaqs.Utils.settings

import com.example.eltaqs.Utils.settings.enums.Language
import java.text.NumberFormat

object LanguageUtils {


    var currentLanguage: Language = Language.ENGLISH

    fun translateNumber(number: Int): String {
        val numberFormat = NumberFormat.getInstance(currentLanguage.locale)
        return numberFormat.format(number)
    }

    fun convertToArabicNumber(englishNumberInput: String): String {
        val arabicNumbers = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val builder = StringBuilder()

        for (char in englishNumberInput) {
            val index = englishNumbers.indexOf(char)
            builder.append(if (index != -1) arabicNumbers[index] else char)
        }

        return builder.toString()
    }

    fun convertToEnglishNumber(arabicNumberInput: String): String {
        val arabicNumbers = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val builder = StringBuilder()

        for (char in arabicNumberInput) {
            val index = arabicNumbers.indexOf(char)
            builder.append(if (index != -1) englishNumbers[index] else char)
        }

        return builder.toString()
    }

    fun formatDateToLocale(dateString: String, format: String): String {
        val locale = currentLanguage.locale
        val formatter = java.text.SimpleDateFormat(format, locale)
        return formatter.format(java.util.Date(dateString.toLong()))
    }
}