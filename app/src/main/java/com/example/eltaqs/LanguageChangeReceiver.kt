package com.example.eltaqs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Locale

class LanguageChangeReceiver(
    private val onLanguageChanged: (Locale) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
            val currentLocale =
                context.resources.configuration.locales[0]
            onLanguageChanged(currentLocale)
        }
    }
}