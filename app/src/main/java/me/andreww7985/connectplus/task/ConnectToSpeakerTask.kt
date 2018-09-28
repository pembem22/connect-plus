package me.andreww7985.connectplus.task

import android.bluetooth.*
import android.os.Build
import android.os.Handler
import android.util.Log
import me.andreww7985.connectplus.ConnectPlusApp
import me.andreww7985.connectplus.Logger
import me.andreww7985.connectplus.speaker.SpeakerManager
import me.andreww7985.connectplus.speaker.SpeakerModel
import java.util.*

class ConnectToSpeakerTask(val speaker: SpeakerModel) : ITask {
    companion object {
        private const val TAG = "ConnectToSpeakerTask"
        private const val GATT_REQUEST_MTU_TIMEOUT = 500L
        private const val GATT_DISCOVER_SERVICES_TIMEOUT = 500L
        private const val GATT_MTU_VALUE = 517 /* What does this value means??? */

        private val UUID_RX_TX_SERVICE = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0000")
        private val UUID_READ_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0002")
        private val UUID_WRITE_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0001")
    }

    private val handler = Handler()
    private val requestMtuRunnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "requestMtuRunnable run")
            bluetoothGatt.requestMtu(GATT_MTU_VALUE)
            handler.postDelayed(this, GATT_REQUEST_MTU_TIMEOUT)
        }
    }
    private val discoverServicesRunnable = object : Runnable {
        override fun run() {
            Log.d(TAG, "discoverServicesRunnable run")
            bluetoothGatt.discoverServices()
            handler.postDelayed(this, GATT_DISCOVER_SERVICES_TIMEOUT)
        }
    }

    private lateinit var bluetoothGatt: BluetoothGatt
    private val bluetoothDevice = speaker.bluetoothDevice
    private var characteristicNotificationEnabled = false
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            Logger.print(TAG, "onConnectionStateChange newState = $newState, status = $status")

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                requestMtuRunnable.run()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "onServicesDiscovered status = $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val bluetoothRxTxService = bluetoothGatt.getService(UUID_RX_TX_SERVICE)

                val writeCharacteristic = bluetoothRxTxService.getCharacteristic(UUID_READ_CHARACTERISTIC)
                val readCharacteristic = bluetoothRxTxService.getCharacteristic(UUID_WRITE_CHARACTERISTIC)

                if (!characteristicNotificationEnabled) {
                    characteristicNotificationEnabled = true
                    bluetoothGatt.setCharacteristicNotification(readCharacteristic, true)
                }

                speaker.writeCharacteristic = writeCharacteristic
                speaker.readCharacteristic = readCharacteristic

                handler.removeCallbacks(discoverServicesRunnable)

                SpeakerManager.onSpeakerConnected(speaker)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Log.d(TAG, "onCharacteristicChanged")
            speaker.onPacket(characteristic.value)
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            Log.d(TAG, "onMtuChanged mtu = $mtu status = $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handler.removeCallbacks(requestMtuRunnable)
                discoverServicesRunnable.run()
            }
        }
    }

    override fun execute() {
        bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            bluetoothDevice.connectGatt(ConnectPlusApp.instance, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE)
        else bluetoothDevice.connectGatt(ConnectPlusApp.instance, false, bluetoothGattCallback)

        speaker.bluetoothGatt = bluetoothGatt
    }
}