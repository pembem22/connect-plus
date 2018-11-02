package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.ui.FragmentName

class SettingsFragment : PreferenceFragmentCompat(), FragmentName {
    companion object {
        const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }


    override fun getName() = "Settings"
}