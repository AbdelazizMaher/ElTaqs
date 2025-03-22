package com.example.eltaqs.data.sharedpreference


interface ISharedPreference {
    fun setLocationSource(source: LocationSource)
    fun getLocationSource(): LocationSource

    fun setMapCoordinates(lat: Double, lon: Double)
    fun getMapCoordinates(): Pair<Double, Double>?

    fun clearPreferences()
}