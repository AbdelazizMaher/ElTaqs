package com.example.eltaqs.Utils.settings.enums

enum class LocationSource(val english: String, val arabic: String) {
    GPS("GPS", "نظام تحديد المواقع"),
    MAP("Map", "الخريطة");

    fun getDisplayName(isArabic: Boolean): String {
        return if (isArabic) arabic else english
    }
}