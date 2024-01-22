package me.andreww7985.connectplus.manager

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
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
    private val adapter: BluetoothAdapter? by lazy { (App.instance.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter }
    private val scanner by lazy { adapter?.bluetoothLeScanner }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return

            BluetoothProtocol.connect(result)
        }
    }

    var isScanning = false

    fun startScan() {
        isScanning = true
        scanner?.startScan(null,
                ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build(),
                scanCallback)
        App.analytics.logEvent("scan_started")
    }

    fun stopScan() {
        isScanning = false
        scanner?.stopScan(scanCallback)
        App.analytics.logEvent("scan_stopped")
    }

    fun isBleSupported() = App.instance.packageManager.hasSystemFeature(FEATURE_BLUETOOTH_LE) && scanner != null
    fun isBluetoothEnabled() = adapter?.isEnabled ?: false
    fun isLocationEnabled() = LocationManagerCompat.isLocationEnabled(App.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
}