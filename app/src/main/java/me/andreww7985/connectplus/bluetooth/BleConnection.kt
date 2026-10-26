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
import me.andreww7985.connectplus.speaker.hardware.HwPlatform
import timber.log.Timber
import java.util.*

class BleConnection(private val bluetoothDevice: BluetoothDevice, private val speaker: SpeakerModel) {
    companion object {
        val UUID_RX_TX_SERVICE = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0000")!!
        val UUID_READ_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0002")!!
        val UUID_WRITE_CHARACTERISTIC = UUID.fromString("65786365-6c70-6f69-6e74-2e636f6d0001")!!
        val UUID_CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")!!
    }

    var gatt: BluetoothGatt? = null
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Timber.d("onConnectionStateChange status = $status, newState = $newState")

            if (status == 133) {
                BleOperationManager.request(DisconnectOperation(this@BleConnection))
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                BleOperationManager.request(RequestMtuOperation(this@BleConnection, speaker.hardware.getMtu()))
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

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            // Only Protocol 4 speakers write a descriptor (the CCCD in setNotification)
            BleOperationManager.operationComplete()
            speaker.requestProtocol4Info()
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            Timber.d("onCharacteristicWrite")
            BleOperationManager.operationComplete()
        }

        @Deprecated("Deprecated in Java")
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

        if (speaker.hardware.platform == HwPlatform.PROTOCOL4) {
            // Also enable notifications on the speaker (CCCD), like the JBL app does; continues in onDescriptorWrite
            characteristic.getDescriptor(UUID_CCCD)?.let { cccd ->
                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                if (gatt.writeDescriptor(cccd)) return
            }
            BleOperationManager.operationComplete()
            speaker.requestProtocol4Info()
            return
        }

        BleOperationManager.operationComplete()

        speaker.sendPacket(Packet(PacketType.REQ_SPEAKER_INFO))
    }

    fun getBluetoothName(): String = gatt!!.device.name
}