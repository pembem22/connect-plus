package me.andreww7985.connectplus.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.android.synthetic.main.activity_main.toolbar
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        UIHelper.updateSystemBarsAppearance(this)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}