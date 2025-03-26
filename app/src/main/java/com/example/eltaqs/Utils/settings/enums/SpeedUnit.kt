package com.example.eltaqs.Utils.settings.enums

enum class SpeedUnit(val english: String, val arabic: String, private val toMpsFactor: Double) {
    METER_PER_SECOND("m/s", "متر/ثانية", 1.0),
    MILE_PER_HOUR("mph", "ميل/ساعة", 0.44704);

    fun convert(value: Double, targetUnit: SpeedUnit): Double {
        return (value * this.toMpsFactor) / targetUnit.toMpsFactor
    }

    companion object {
        fun fromName(name: String): SpeedUnit {
            return values().find { it.name == name } ?: METER_PER_SECOND
        }
    }
}