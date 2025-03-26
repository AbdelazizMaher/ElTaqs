package com.example.eltaqs.Utils.settings

import com.example.eltaqs.Utils.settings.enums.SpeedUnit

object SpeedSettings {
    private val arabicNumbers = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
    private val englishNumbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

    private fun convertArabicToEnglish(input: String): String {
        var result = input
        for (i in arabicNumbers.indices) {
            result = result.replace(arabicNumbers[i], englishNumbers[i])
        }
        return result
    }

    private fun convertEnglishToArabic(input: String): String {
        var result = input
        for (i in englishNumbers.indices) {
            result = result.replace(englishNumbers[i], arabicNumbers[i])
        }
        return result
    }

    fun convertSpeed(speed: String, targetUnit: SpeedUnit, outputInArabic: Boolean): String {
        val numericSpeed = convertArabicToEnglish(speed).toDouble()
        val convertedSpeed = SpeedUnit.METER_PER_SECOND.convert(numericSpeed, targetUnit)

        return if (outputInArabic) {
            "${convertEnglishToArabic(String.format("%.2f", convertedSpeed))} ${targetUnit.arabic}"
        } else {
            "${String.format("%.2f", convertedSpeed)} ${targetUnit.english}"
        }
    }
}
