package me.andreww7985.connectplus

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import me.andreww7985.connectplus.core.App

class ConnectPlusApp : Application() {
    companion object {
        const val TAG = "ConnectPlusApp"
        lateinit var instance: ConnectPlusApp
        lateinit var sharedPreferences: SharedPreferences

        fun logFirebaseEvent(event: String, log: Bundle.() -> Unit = {}) {
            val bundle = Bundle()
            log(bundle)
            //firebaseAnalytics.logEvent(event, bundle)
        }

        fun logSpeakerEvent(event: String, log: Bundle.() -> Unit = {}) {
            if (App.sharedPreferences.getBoolean("send_speaker_data", true)) {
                logFirebaseEvent(event, log)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        App.init(this)

        instance = this
        val sendUsageData = App.sharedPreferences.getBoolean("send_usage_data", true)
    }
}