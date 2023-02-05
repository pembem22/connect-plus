package me.andreww7985.connectplus.discovery

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_discovery.*
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.manager.BleScanManager
import me.andreww7985.connectplus.manager.PresenterManager
import me.andreww7985.connectplus.mvp.BaseView
import me.andreww7985.connectplus.ui.activities.SettingsActivity
import timber.log.Timber

class DiscoveryView : AppCompatActivity(), BaseView {
    companion object {
        private const val REQUEST_CODE_PERMISSION = 1
        private const val REQUEST_CODE_BLUETOOTH = 2
        private const val REQUEST_CODE_LOCATION = 3
    }

    private val presenter =
        PresenterManager.getPresenter(DiscoveryPresenter::class.java) as DiscoveryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        UIHelper.updateSystemBarsAppearance(this)
        setContentView(R.layout.activity_discovery)
        setSupportActionBar(toolbar)

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
    }

    fun showConnecting(name: String) {
        lifecycleScope.launch {
            discovery_text.text = getString(R.string.discovery_connecting, name)
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.attachView(this)
        checkPermissions()
    }

    override fun onPause() {
        super.onPause()

        presenter.detachView()
        BleScanManager.stopScan()
    }

    override fun onDestroy() {
        super.onDestroy()

        PresenterManager.destroyPresenter(DiscoveryPresenter::class.java)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.d("onRequestPermissionsResult requestCode = $requestCode")
        checkPermissions()
    }

    private fun checkPermissions() {
        Timber.d("checkPermissions")

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val locationGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!locationGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_PERMISSION
                )
                return
            }
        }

        checkBluetooth()
    }

    private fun checkBluetooth() {
        Timber.d("checkBluetooth")

        if (!BleScanManager.isBleSupported()) {
            UIHelper.showToast(findViewById(android.R.id.content), "Bluetooth Low Energy not supported on this device", Toast.LENGTH_LONG)
            Timber.d("checkBluetooth BLE not supported")
            finish()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothConnectGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            val bluetoothScanGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED

            if (!bluetoothConnectGranted || !bluetoothScanGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    ),
                    REQUEST_CODE_PERMISSION
                )
                return
            }
        }

        if (!BleScanManager.isBluetoothEnabled()) {
            startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_CODE_BLUETOOTH
            )
            return
        }

        checkLocation()
    }

    private fun checkLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && !BleScanManager.isLocationEnabled())
            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                REQUEST_CODE_LOCATION
            )
        else
            BleScanManager.startScan()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
}
