package me.andreww7985.connectplus.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.activity_main.*
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.dfu.DfuView
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.speaker.ProductConnect
import me.andreww7985.connectplus.speaker.ProductModel
import me.andreww7985.connectplus.speaker.SpeakerView
import me.andreww7985.connectplus.ui.fragments.ConnectFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    companion object {
        private const val KEY_SELECTED_ITEM = "selectedItem"
    }

    private var selectedItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        savedInstanceState.let {
            if (it == null) {
                updateCurrentFragment(R.id.nav_dashboard)
            } else {
                selectedItem = it.getInt(KEY_SELECTED_ITEM)
            }
        }

        nav_menu.setOnNavigationItemSelectedListener { item ->
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

        /* Only show DFU flash menu on known supported models. */
        val supportedDfu = listOf(
                ProductModel.XTREME,
                ProductModel.CHARGE3,
                ProductModel.CHARGE4,
                ProductModel.FLIP4,
                ProductModel.XTREME2,
                ProductModel.BOOMBOX
        )
        if (!supportedDfu.contains(speaker.model)) {
            nav_menu.menu.removeItem(R.id.nav_flash_dfu)
        }

        nav_menu.menu.findItem(R.id.nav_connect).apply {
            val connect = ProductConnect.from(speaker.model)
            setIcon(connect.iconId)
            setTitle(connect.nameId)
        }

        val view = window.decorView

        view.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            toolbar.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }
    }

    private fun updateCurrentFragment(selectedItemId: Int) {
        Timber.d("updateCurrentFragment")
        if (this.selectedItem == selectedItemId) return
        this.selectedItem = selectedItemId
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_ITEM, selectedItem)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}