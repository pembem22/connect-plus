package me.andreww7985.connectplus.ui.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.manager.BleScanManager

class DiscoveryActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "DiscoveryActivity"
        private const val REQUEST_CODE_PERMISSION = 1
        private const val REQUEST_CODE_BLUETOOTH = 2
        private const val REQUEST_CODE_LOCATION = 3
        //lateinit var instance: DiscoveryActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)
        //instance = this
    }

    override fun onResume() {
        super.onResume()

        checkPermissions()
    }

    override fun onPause() {
        super.onPause()

        BleScanManager.stopScan()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult requestCode = $requestCode")
        checkPermissions()
    }

    private fun checkPermissions() {
        Log.d(TAG, "checkPermissions")
        val location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val files = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (location != PackageManager.PERMISSION_GRANTED || files != PackageManager.PERMISSION_GRANTED))
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        else checkBluetooth()
    }

    private fun checkBluetooth() {
        Log.d(TAG, "checkBluetooth")

        if (!BleScanManager.isBleSupported()) {
            UIHelper.showToast("Bluetooth not supported", Toast.LENGTH_LONG)
            Log.d(TAG, "checkBluetooth bluetooth not supported")
            finish()
            return
        }

        if (!BleScanManager.isBluetoothEnabled()) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_BLUETOOTH)
        } else checkLocation()
    }

    private fun checkLocation() {
        if (!BleScanManager.isLocationEnabled())
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_LOCATION)
        else
            BleScanManager.startScan()
    }
}
