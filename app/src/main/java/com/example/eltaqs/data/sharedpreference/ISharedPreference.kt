package com.example.eltaqs.data.sharedpreference

import com.example.eltaqs.Utils.settings.enums.Language
import com.example.eltaqs.Utils.settings.enums.LocationSource
import com.example.eltaqs.Utils.settings.enums.SpeedUnit
import com.example.eltaqs.Utils.settings.enums.TemperatureUnit

interface ISharedPreference {
    fun setLocationSource(source: LocationSource)
    fun getLocationSource(): LocationSource

    fun setTemperatureUnit(unit: TemperatureUnit)
    fun getTemperatureUnit(): TemperatureUnit

    fun setWindSpeedUnit(unit: SpeedUnit)
    fun getWindSpeedUnit(): SpeedUnit

    fun setLanguage(language: Language)
    fun getLanguage(): Language

    fun setMapCoordinates(lat: Double, lon: Double)
    fun getMapCoordinates(): Pair<Double, Double>?

    fun clearPreferences()
}