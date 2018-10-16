package me.andreww7985.connectplus.manager.bleoperations

import me.andreww7985.connectplus.bluetooth.BleConnection

class DiscoverOperation(bleConnection: BleConnection) : BleOperation(bleConnection) {
    override fun perform() {
        bleConnection.discoverServices()
    }
}