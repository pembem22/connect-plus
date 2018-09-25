package me.andreww7985.connectplus

import android.util.Log

object Logger {
    fun print(tag: String, message: String) {
        // Crashlytics.log("$tag: $message")
        Log.d(tag, message)
    }
}