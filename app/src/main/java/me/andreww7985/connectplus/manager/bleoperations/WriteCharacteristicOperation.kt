package me.andreww7985.connectplus.manager.bleoperations

import android.bluetooth.BluetoothGattCharacteristic
import me.andreww7985.connectplus.bluetooth.BleConnection

class WriteCharacteristicOperation(bleConnection: BleConnection, private val characteristic: BluetoothGattCharacteristic, private val value: ByteArray) : BleOperation(bleConnection) {
    override fun perform() {
        bleConnection.writeCharacteristic(characteristic, value)
    }
}