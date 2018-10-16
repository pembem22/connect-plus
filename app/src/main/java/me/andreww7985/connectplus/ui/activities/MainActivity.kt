package me.andreww7985.connectplus.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.dfu.DfuView
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.speaker.SpeakerView
import me.andreww7985.connectplus.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_SELECTED_ITEM = "selectedItem"
    }

    var selectedItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instance = this
        Log.d(TAG, "onCreate")
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

        ad_view.loadAd(AdRequest.Builder().build())
    }

    fun updateCurrentFragment(selectedItemId: Int) {
        Log.d(TAG, "updateCurrentFragment")
        if (this.selectedItem == selectedItemId) return
        this.selectedItem = selectedItemId
        nav_menu.selectedItemId = selectedItemId

        /*
         with (supportFragmentManager) {
            val tag = when (selectedItemId) {
                R.id.nav_dashboard -> SpeakerView.TAG
                R.id.nav_dfu_update -> DfuView.TAG
                R.id.nav_settings -> SettingsFragment.TAG
                else -> throw IllegalArgumentException("Wrong selectedItemId")
            }

            val fragment = findFragmentByTag(tag) ?: when (selectedItemId) {
                R.id.nav_dashboard -> SpeakerView()
                R.id.nav_dfu_update -> DfuView()
                R.id.nav_settings -> SettingsFragment()
                else -> throw IllegalArgumentException("Wrong selectedItemId")
            }
            beginTransaction().replace(R.id.main_fragment, fragment, tag).commitAllowingStateLoss()
        }
         */

        val fragment = when (selectedItemId) {
            R.id.nav_dashboard -> SpeakerView()
            R.id.nav_dfu_update -> DfuView()
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