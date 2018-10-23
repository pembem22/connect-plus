package me.andreww7985.connectplus

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(App.instance)

    fun logSpeakerEvent(eventName: String, log: Bundle.() -> Unit = {}) {
        if (App.sharedPreferences.getBoolean("send_speaker_data", true)) {
            logEvent(eventName, log)
        }
    }

    fun logEvent(eventName: String, log: Bundle.() -> Unit = {}) {
        val bundle = Bundle()
        log(bundle)
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}