package me.andreww7985.connectplus.bluetooth

import android.bluetooth.*
import android.os.Build
import android.util.Log
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.manager.BleOperationManager
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.manager.bleoperations.DiscoverOperation
import me.andreww7985.connectplus.manager.bleoperations.EnableNotificationOperation
import me.andreww7985.connectplus.manager.bleoperations.RequestMtuOperation
import me.andreww7985.connectplus.speaker.SpeakerModel
import java.util.*

class BleConnection(private val bluetoothDevice: BluetoothDevice, private val speaker: SpeakerModel) {
    companion object {
        const val TAG = "BleConnection"
        const val GATT_MTU_VALUE = 517

        val UUID_RX_TX_SERVICE = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0000")
        val UUID_READ_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0002")
        val UUID_WRITE_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0001")
    }

    private var gatt: BluetoothGatt? = null
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange status = $status, newState = $newState")

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                BleOperationManager.request(RequestMtuOperation(this@BleConnection, GATT_MTU_VALUE))
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt ?: return

            val rxTxService = gatt.getService(UUID_RX_TX_SERVICE)
            speaker.readCharacteristic = rxTxService.getCharacteristic(UUID_WRITE_CHARACTERISTIC)
            speaker.writeCharacteristic = rxTxService.getCharacteristic(UUID_READ_CHARACTERISTIC)

            BleOperationManager.operationComplete()
            BleOperationManager.request(EnableNotificationOperation(this@BleConnection, speaker.readCharacteristic!!))

            SpeakerManager.speakerConnected(speaker)
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            BleOperationManager.operationComplete()
            BleOperationManager.request(DiscoverOperation(this@BleConnection))
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Log.d(TAG, "onCharacteristicWrite")
            BleOperationManager.operationComplete()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic ?: return

            speaker.onPacket(characteristic.value)
        }
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        Log.d(TAG, "writeCharacteristic")
        val gatt = gatt ?: throw IllegalStateException("writeCharacteristic when gatt is null")

        characteristic.value = value
        gatt.writeCharacteristic(characteristic)
    }

    fun connect() {
        Log.d(TAG, "connect")
        gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            bluetoothDevice.connectGatt(App.instance, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        else bluetoothDevice.connectGatt(App.instance, false, gattCallback)
    }

    fun discoverServices() {
        val gatt = gatt ?: throw IllegalStateException("discoverServices when gatt is null")

        gatt.discoverServices()
    }

    fun requestMtu(mtu: Int) {
        val gatt = gatt ?: throw IllegalStateException("requestMtu when gatt is null")

        gatt.requestMtu(mtu)
    }

    fun setNotification(characteristic: BluetoothGattCharacteristic, value: Boolean) {
        val gatt = gatt ?: throw IllegalStateException("enableNotification when gatt is null")

        gatt.setCharacteristicNotification(characteristic, value)
        BleOperationManager.operationComplete()
    }
}