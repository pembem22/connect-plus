package me.andreww7985.connectplus.core

import android.content.Context
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics(val context: Context) {
    lateinit var analytics: FirebaseAnalytics
    lateinit var crashlytics: CrashlyticsCore

    fun init(enableAnalytics: Boolean, enableCrashlytics: Boolean) {
        //FirebaseApp.initializeApp(context)
        //analytics = FirebaseAnalytics.getInstance(context)

        // Initialize Crashlytics
        //Fabric.with(Fabric.Builder(context).)
        //crashlytics = CrashlyticsCore.getInstance()
    }
}