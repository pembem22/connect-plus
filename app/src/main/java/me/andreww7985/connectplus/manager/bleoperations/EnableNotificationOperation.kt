package me.andreww7985.connectplus.manager.bleoperations

import android.bluetooth.BluetoothGattCharacteristic
import me.andreww7985.connectplus.bluetooth.BleConnection

class EnableNotificationOperation(bleConnection: BleConnection, private val characteristic: BluetoothGattCharacteristic) : BleOperation(bleConnection) {
    override fun perform() {
        bleConnection.setNotification(characteristic, true)
    }
}