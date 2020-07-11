package me.andreww7985.connectplus.bluetooth

import android.bluetooth.*
import android.os.Build
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.manager.BleOperationManager
import me.andreww7985.connectplus.manager.bleoperations.DisconnectOperation
import me.andreww7985.connectplus.manager.bleoperations.DiscoverOperation
import me.andreww7985.connectplus.manager.bleoperations.EnableNotificationOperation
import me.andreww7985.connectplus.manager.bleoperations.RequestMtuOperation
import me.andreww7985.connectplus.protocol.Packet
import me.andreww7985.connectplus.protocol.PacketType
import me.andreww7985.connectplus.speaker.SpeakerModel
import timber.log.Timber
import java.util.*

class BleConnection(private val bluetoothDevice: BluetoothDevice, private val speaker: SpeakerModel) {
    companion object {
        val UUID_RX_TX_SERVICE = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0000")!!
        val UUID_READ_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0002")!!
        val UUID_WRITE_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0001")!!
    }

    var gatt: BluetoothGatt? = null
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Timber.d("onConnectionStateChange status = $status, newState = $newState")

            if (status == 133) {
                BleOperationManager.request(DisconnectOperation(this@BleConnection))
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                BleOperationManager.request(RequestMtuOperation(this@BleConnection, speaker.model.getMtu()))
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                BleOperationManager.operationComplete()
                gatt!!.close()
                connect()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            gatt ?: return

            val rxTxService = gatt.getService(UUID_RX_TX_SERVICE)
            speaker.readCharacteristic = rxTxService.getCharacteristic(UUID_WRITE_CHARACTERISTIC)
            speaker.writeCharacteristic = rxTxService.getCharacteristic(UUID_READ_CHARACTERISTIC)

            BleOperationManager.operationComplete()
            BleOperationManager.request(EnableNotificationOperation(this@BleConnection, speaker.readCharacteristic!!))
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            BleOperationManager.operationComplete()
            BleOperationManager.request(DiscoverOperation(this@BleConnection))
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Timber.d("onCharacteristicWrite")
            BleOperationManager.operationComplete()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic ?: return

            speaker.onPacket(characteristic.value)
        }
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        Timber.d("writeCharacteristic")
        val gatt = gatt ?: throw IllegalStateException("writeCharacteristic when gatt is null")

        characteristic.value = value
        gatt.writeCharacteristic(characteristic)
    }

    fun reconnect() {

    }

    fun connect() {
        Timber.d("connect")
        gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            bluetoothDevice.connectGatt(App.instance, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        else bluetoothDevice.connectGatt(App.instance, false, gattCallback)
    }

    fun requestMtu(mtu: Int) {
        val gatt = gatt ?: throw IllegalStateException("requestMtu when gatt is null")

        gatt.requestMtu(mtu)
    }

    fun setNotification(characteristic: BluetoothGattCharacteristic, value: Boolean) {
        val gatt = gatt ?: throw IllegalStateException("enableNotification when gatt is null")

        gatt.setCharacteristicNotification(characteristic, value)
        BleOperationManager.operationComplete()

        speaker.sendPacket(Packet(PacketType.REQ_SPEAKER_INFO))
    }

    fun getBluetoothName() = gatt!!.device.name
}