package me.andreww7985.connectplus.manager.bleoperations

import me.andreww7985.connectplus.bluetooth.BleConnection

abstract class BleOperation(val bleConnection: BleConnection) {
    abstract fun perform()
}