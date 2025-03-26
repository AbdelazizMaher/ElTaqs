package com.example.eltaqs.Utils.settings.enums

enum class TemperatureUnit(val english: String, val arabic: String) {
    KELVIN("K", "كلفن"),
    CELSIUS("°C", "درجة مئوية"),
    FAHRENHEIT("°F", "فهرنهايت");

    fun getDisplayName(isArabic: Boolean): String {
        return if (isArabic) arabic else english
    }
}