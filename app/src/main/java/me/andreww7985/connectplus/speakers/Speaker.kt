package me.andreww7985.connectplus.speakers

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import me.andreww7985.connectplus.bluetooth.ActiveChannel
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.controller.FragmentController
import me.andreww7985.connectplus.dfu.DFUFirmware
import me.andreww7985.connectplus.dfu.DFUState

class Speaker(val model: SpeakerModel, val color: SpeakerColor, val bluetoothDevice: BluetoothDevice, val scanRecord: String) {
    companion object {
        const val TAG = "Speaker"
    }

    val mac = bluetoothDevice.address.toUpperCase().replace(":", "")
    lateinit var dfu: DFUFirmware
    var dfuState = DFUState.FILE_NOT_SELECTED
        set(value) {
            field = value
            FragmentController.update()
        }

    var index: Int? = 0
    var activeChannel: ActiveChannel = ActiveChannel.BOTH
    var name: String = "Test"
        set(value) {
            field = value
            FragmentController.update()
        }

    var batteryCharging: Boolean = false
        set(value) {
            field = value
            FragmentController.update()
        }

    var audioFeedback: Boolean? = null
        set(value) {
            field = value
            BluetoothProtocol.setAudioFeedback(this, value!!)
            FragmentController.update()
        }

    var batteryLevel: Int = -1
        set(value) {
            field = value
            FragmentController.update()
        }

    var firmware: String? = null
        set(value) {
            field = value
            FragmentController.update()
        }

    lateinit var readCharacteristic: BluetoothGattCharacteristic
    lateinit var writeCharacteristic: BluetoothGattCharacteristic
    lateinit var bluetoothGatt: BluetoothGatt

    fun onPacket(packet: ByteArray) {
        BluetoothProtocol.onPacket(packet)
    }

    fun sendPacket(bytes: ByteArray) {
        writeCharacteristic.value = bytes
        bluetoothGatt.writeCharacteristic(writeCharacteristic)
    }
}
