package me.andreww7985.connectplus.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import me.andreww7985.connectplus.databinding.ActivitySettingsBinding
import me.andreww7985.connectplus.helpers.UIHelper

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setSupportActionBar(binding.toolbar)
        UIHelper.updateSystemBarsAppearance(this)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}