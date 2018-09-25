package me.andreww7985.connectplus.ui.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import me.andreww7985.connectplus.Logger
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.ui.fragments.DFUFragment
import me.andreww7985.connectplus.ui.fragments.DashboardFragment
import me.andreww7985.connectplus.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_SELECTED_ITEM = "selectedItem"
        //lateinit var instance: MainActivity
    }

    var selectedItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instance = this
        Logger.print(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        savedInstanceState.let {
            if (it == null) {
                updateCurrentFragment(R.id.nav_dashboard)
            } else {
                selectedItem = it.getInt(KEY_SELECTED_ITEM)
            }
        }


        nav_menu.setOnNavigationItemSelectedListener(this)
    }

    fun updateCurrentFragment(selectedItemId: Int) {
        Logger.print(TAG, "updateCurrentFragment")
        if (this.selectedItem == selectedItemId) return
        this.selectedItem = selectedItemId
        nav_menu.selectedItemId = selectedItemId

        /*
         with (supportFragmentManager) {
            val tag = when (selectedItemId) {
                R.id.nav_dashboard -> DashboardFragment.TAG
                R.id.nav_dfu_update -> DFUFragment.TAG
                R.id.nav_settings -> SettingsFragment.TAG
                else -> throw IllegalArgumentException("Wrong selectedItemId")
            }

            val fragment = findFragmentByTag(tag) ?: when (selectedItemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_dfu_update -> DFUFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> throw IllegalArgumentException("Wrong selectedItemId")
            }
            beginTransaction().replace(R.id.main_fragment, fragment, tag).commitAllowingStateLoss()
        }
         */

        val fragment = when (selectedItemId) {
            R.id.nav_dashboard -> DashboardFragment()
            R.id.nav_dfu_update -> DFUFragment()
            R.id.nav_settings -> SettingsFragment()
            else -> throw IllegalArgumentException("Wrong selectedItemId")
        }
        UIHelper.showFragment(this, fragment)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(KEY_SELECTED_ITEM, selectedItem)
        super.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        updateCurrentFragment(item.itemId)
        return true
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.nav_reload -> {
            if (SpeakerManager.mainSpeaker?.writeCharacteristic == null) {
                SpeakerManager.mainSpeaker?.bluetoothGatt?.requestMtu(517)
            } else {
                BluetoothProtocol.requestSpeakerInfo(SpeakerManager.mainSpeaker!!)
            }

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }*/
}
