package me.andreww7985.connectplus

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager

class App : Application() {
    companion object {
        const val TAG = "App"
        lateinit var instance: App
        lateinit var sharedPreferences: SharedPreferences

        fun logFirebaseEvent(event: String, log: Bundle.() -> Unit = {}) {
            val bundle = Bundle()
            log(bundle)
            //firebaseAnalytics.logEvent(event, bundle)
        }

        fun logSpeakerEvent(event: String, log: Bundle.() -> Unit = {}) {
            if (sharedPreferences.getBoolean("send_speaker_data", true)) {
                logFirebaseEvent(event, log)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val sendUsageData = sharedPreferences.getBoolean("send_usage_data", true)
    }
}