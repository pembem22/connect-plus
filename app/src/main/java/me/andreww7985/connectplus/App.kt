package me.andreww7985.connectplus

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
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

        analytics = Analytics()
        analytics.setAnalyticsEnabled(sharedPreferences.getBoolean("send_usage_data", true))
    }
}