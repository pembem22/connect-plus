package me.andreww7985.connectplus.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol

object BleScanManager {
    private val bleScanner: BluetoothLeScanner by lazy {
        BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    }
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return

            BluetoothProtocol.connect(result)
        }
    }

    var isScanning = false

    fun startScan() {
        isScanning = true
        bleScanner.startScan(null,
                ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build(),
                scanCallback)
    }

    fun stopScan() {
        isScanning = false
        bleScanner.stopScan(scanCallback)
    }

    fun isBleSupported() = BluetoothAdapter.getDefaultAdapter() != null && App.instance.packageManager.hasSystemFeature(FEATURE_BLUETOOTH_LE)
    fun isBluetoothEnabled() = BluetoothAdapter.getDefaultAdapter().isEnabled
    fun isLocationEnabled() = LocationManagerCompat.isLocationEnabled(App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
}