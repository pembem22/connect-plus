package me.andreww7985.connectplus.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log


object BluetoothScanner {
    private const val TAG = "BluetoothScanner"
    var isScanning = false
        private set
    private var bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "onScanResult $result ${result.device} ${result.rssi}")
            BluetoothProtocol.connect(result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            Log.d(TAG, "onBatchScanResults")
            for (result in results) onScanResult(0, result)
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "onScanFailed errorCode = $errorCode")
        }
    }

    fun startScan() {
        Log.d(TAG, "startBluetoothScan")

        isScanning = true
        bluetoothLeScanner.startScan(null,
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), leScanCallback)
    }

    fun stopScan() {
        if (!isScanning) return

        isScanning = false
        bluetoothLeScanner.stopScan(leScanCallback)
    }
}
