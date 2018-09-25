package me.andreww7985.connectplus.ui.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import me.andreww7985.connectplus.Logger
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.bluetooth.BluetoothScanner
import me.andreww7985.connectplus.helpers.UIHelper

class DiscoveryActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "DiscoveryActivity"
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

        BluetoothScanner.stopScan()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Logger.print(TAG, "onRequestPermissionsResult requestCode = $requestCode")
        when (requestCode) {
            1 -> checkPermissions()
            2 -> checkBluetooth()
        }
    }

    private fun checkPermissions() {
        Logger.print(TAG, "checkPermissions")
        val location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val files = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (location != PackageManager.PERMISSION_GRANTED || files != PackageManager.PERMISSION_GRANTED))
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        else checkBluetooth()
    }

    private fun checkBluetooth() {
        Logger.print(TAG, "checkBluetooth")
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        bluetoothAdapter ?: run {
            UIHelper.showToast("Bluetooth not supported", Toast.LENGTH_LONG)
            Logger.print(TAG, "checkBluetooth bluetooth not supported")
            finish()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 2)
        } else BluetoothScanner.startScan()
    }
}
