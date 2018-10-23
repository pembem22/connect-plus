package me.andreww7985.connectplus.manager.bleoperations

import me.andreww7985.connectplus.bluetooth.BleConnection
import timber.log.Timber

abstract class BleOperation(val bleConnection: BleConnection) {
    init {
        if (bleConnection.gatt == null) {
            Timber.e("perform bleConnection.gatt is null")
        }
    }

    abstract fun perform()
}