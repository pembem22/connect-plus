package me.andreww7985.connectplus.manager.bleoperations

import me.andreww7985.connectplus.bluetooth.BleConnection

class RequestMtuOperation(bleConnection: BleConnection, private val mtu: Int) : BleOperation(bleConnection) {
    override fun perform() {
        bleConnection.requestMtu(mtu)
    }
}