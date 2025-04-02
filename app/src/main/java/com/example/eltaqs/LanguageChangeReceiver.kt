package com.example.eltaqs

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.eltaqs.data.local.AppDataBase
import com.example.eltaqs.data.local.WeatherLocalDataSource
import com.example.eltaqs.data.remote.WeatherRemoteDataSource
import com.example.eltaqs.data.repo.WeatherRepository
import com.example.eltaqs.data.sharedpreference.SharedPrefDataSource
import com.example.eltaqs.utils.restartActivity
import com.example.eltaqs.utils.settings.enums.Language
import java.util.Locale

class LanguageChangeReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {

            val systemLocale = getActualSystemLocale(context)
            Log.d("TAG", "onReceive: ${systemLocale.language}")

            val repository = WeatherRepository.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(AppDataBase.getInstance(context).getFavouritesDAO()),
                SharedPrefDataSource.getInstance(context)
            )

            val newLanguage = when (systemLocale.language) {
                "ar" -> Language.ARABIC
                else -> Language.ENGLISH
            }

            repository.setLanguage(newLanguage)
        }
    }



    @SuppressLint("ObsoleteSdkInt")
    private fun getActualSystemLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // For newer devices - get the first preferred locale
            context.resources.configuration.locales[0]
        } else {
            // For older devices
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }
}