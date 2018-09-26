package me.andreww7985.connectplus.core

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object App {
    lateinit var sharedPreferences: SharedPreferences
    //lateinit var analytics

    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }
}