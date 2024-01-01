package me.andreww7985.connectplus.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.content.getSystemService
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper
import java.io.BufferedReader
import java.io.InputStreamReader


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        findPreference<Preference>("copy_logs")!!.setOnPreferenceClickListener {
            val process = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            val log = StringBuilder()
            while (true) {
                log.appendLine(bufferedReader.readLine() ?: break)
            }

            requireContext().getSystemService<ClipboardManager>()!!.setPrimaryClip(ClipData.newPlainText("logs", log))

            UIHelper.showToast(this@SettingsFragment.requireView(), "Copied logs to clipboard")
            false
        }

        App.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, key: String?) {
        if (key == "dark_theme") {
            App.instance.updateDarkTheme()
        }
    }
}