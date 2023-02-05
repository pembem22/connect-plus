package me.andreww7985.connectplus

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class App : Application() {
    companion object {
        lateinit var instance: App
        lateinit var sharedPreferences: SharedPreferences
        lateinit var analytics: Analytics
        lateinit var crashlytics: FirebaseCrashlytics
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(Timber.DebugTree())

        DynamicColors.applyToActivitiesIfAvailable(this)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sendUsageData = sharedPreferences.getBoolean("send_usage_data", true)
        Timber.d("sendUsageData $sendUsageData")

        analytics = Analytics()
        analytics.setAnalyticsCollectionEnabled(sendUsageData)

        crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(sendUsageData)

        updateDarkTheme()
    }

    fun updateDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(
                when (sharedPreferences.getString("dark_theme", "SYSTEM")) {
                    "ENABLED" -> AppCompatDelegate.MODE_NIGHT_YES
                    "DISABLED" -> AppCompatDelegate.MODE_NIGHT_NO
                    "SYSTEM" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
                }
        )
    }
}