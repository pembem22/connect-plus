package me.andreww7985.connectplus.manager.bleoperations

import me.andreww7985.connectplus.bluetooth.BleConnection

class DisconnectOperation(bleConnection: BleConnection) : BleOperation(bleConnection) {
    override fun perform() {
        bleConnection.gatt!!.disconnect()
    }
}