package me.andreww7985.connectplus.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.android.synthetic.main.activity_main.nav_menu
import kotlinx.android.synthetic.main.activity_main.toolbar
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.dfu.DfuView
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.speaker.SpeakerView
import me.andreww7985.connectplus.speaker.hardware.HwConnect
import me.andreww7985.connectplus.speaker.hardware.HwPlatform
import me.andreww7985.connectplus.ui.MainActivityViewModel
import me.andreww7985.connectplus.ui.fragments.ConnectFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val model: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        UIHelper.updateSystemBarsAppearance(this, hasNavbar = true)

        if (model.selectedNavbarItem == 0) {
            updateCurrentFragment(R.id.nav_dashboard)
        }

        nav_menu.setOnItemSelectedListener { item ->
            updateCurrentFragment(item.itemId)
            true
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        val speaker = SpeakerManager.selectedSpeaker!!

        /* Only show DFU flash menu on known supported CSR models. */
        if (speaker.hardware.platform != HwPlatform.CSR) {
            nav_menu.menu.removeItem(R.id.nav_flash_dfu)
        }

        nav_menu.menu.findItem(R.id.nav_connect).apply {
            val connect = HwConnect.from(speaker.hardware.model)
            setIcon(connect.iconId)
            setTitle(connect.nameId)
        }
    }

    private fun updateCurrentFragment(selectedItemId: Int) {
        Timber.d("updateCurrentFragment")
        if (model.selectedNavbarItem == selectedItemId) {
            return
        }

        model.selectedNavbarItem = selectedItemId
        nav_menu.selectedItemId = selectedItemId

        val fragment = when (selectedItemId) {
            R.id.nav_dashboard -> SpeakerView()
            R.id.nav_connect -> ConnectFragment()
            R.id.nav_flash_dfu -> DfuView()
            else -> throw IllegalArgumentException("Wrong selectedItemId")
        }

        App.analytics.logEvent("opened_menu") {
            putString("menu_name", fragment.javaClass.simpleName)
        }

        UIHelper.showFragment(this, fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}