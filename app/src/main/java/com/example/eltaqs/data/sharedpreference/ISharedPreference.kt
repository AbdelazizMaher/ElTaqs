package com.example.eltaqs.data.sharedpreference

interface ISharedPreference {
    fun setLocationSource(source: LocationSource)
    fun getLocationSource(): LocationSource

    fun setTemperatureUnit(unit: TemperatureUnit)
    fun getTemperatureUnit(): TemperatureUnit

    fun setWindSpeedUnit(unit: WindSpeedUnit)
    fun getWindSpeedUnit(): WindSpeedUnit

    fun setLanguage(language: Language)
    fun getLanguage(): Language

    fun setMapCoordinates(lat: Double, lon: Double)
    fun getMapCoordinates(): Pair<Double, Double>?

    fun clearPreferences()
}