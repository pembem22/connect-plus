package me.andreww7985.connectplus

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : Application() {
    companion object {
        lateinit var instance: App
        lateinit var sharedPreferences: SharedPreferences
        lateinit var analytics: Analytics
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Timber.plant(Timber.DebugTree())

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val sendUsageData = sharedPreferences.getBoolean("send_usage_data", true)

        Timber.d("sendUsageData $sendUsageData")

        analytics = Analytics()
        analytics.setAnalyticsEnabled(sendUsageData)

        if (sendUsageData) {
            Fabric.with(this, Crashlytics())
            Timber.d("Crashlytics enabled")
        }

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