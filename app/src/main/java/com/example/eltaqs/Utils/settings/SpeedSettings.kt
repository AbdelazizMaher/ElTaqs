package com.example.eltaqs.Utils.settings

import com.example.eltaqs.Utils.settings.enums.Language
import com.example.eltaqs.Utils.settings.enums.SpeedUnit

object SpeedSettings {
    private val arabicNumbers = ('٠'..'٩').toList()
    private val englishNumbers = ('0'..'9').toList()

    private fun convertArabicToEnglish(input: String): String {
        return input.map { char ->
            arabicNumbers.indexOf(char).takeIf { it != -1 }?.let { englishNumbers[it] } ?: char
        }.joinToString("")
    }

    private fun convertEnglishToArabic(input: String): String {
        return input.map { char ->
            englishNumbers.indexOf(char).takeIf { it != -1 }?.let { arabicNumbers[it] } ?: char
        }.joinToString("")
    }

    fun convertSpeed(speed: String, sourceUnit: SpeedUnit, targetUnit: SpeedUnit, language: Language): String {
        val numericSpeed = convertArabicToEnglish(speed).toDoubleOrNull() ?: return "Invalid Input"
        val convertedSpeed = sourceUnit.convert(numericSpeed, targetUnit)

        val formattedSpeed = String.format("%.2f", convertedSpeed)
        return if (language == Language.ARABIC) {
            "${convertEnglishToArabic(formattedSpeed)} ${targetUnit.getDisplayName(language)}"
        } else {
            "$formattedSpeed ${targetUnit.getDisplayName(language)}"
        }
    }
}
