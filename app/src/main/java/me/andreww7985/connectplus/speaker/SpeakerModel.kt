package me.andreww7985.connectplus.speaker

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import io.reactivex.subjects.PublishSubject
import me.andreww7985.connectplus.bluetooth.*
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.manager.BleOperationManager
import me.andreww7985.connectplus.manager.bleoperations.WriteCharacteristicOperation
import me.andreww7985.connectplus.mvp.BaseModel
import java.util.concurrent.ConcurrentHashMap

class SpeakerModel(val model: ProductModel, val color: ProductColor, val bluetoothDevice: BluetoothDevice, val scanRecord: String) : BaseModel {
    val mac = bluetoothDevice.address
    var index = 0

    var readCharacteristic: BluetoothGattCharacteristic? = null
    var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var bleConnection: BleConnection = BleConnection(bluetoothDevice, this)

    val features = ConcurrentHashMap<Feature.Type, Feature>()
    var onFeatureChangedCallback: (() -> Unit)? = null

    val packetObservable = PublishSubject.create<BluetoothPacket>()

    init {
        bleConnection.connect()
    }

    fun setOnFeatureChangedListener(listener: () -> Unit) {
        onFeatureChangedCallback = listener
    }

    fun featuresChanged() {
        onFeatureChangedCallback?.invoke()
    }

    fun onPacket(bytes: ByteArray) {
        if (bytes[0] != 0xAA.byte) {
            throw IllegalArgumentException("Packet header != 0xAA")
        }

        val length = bytes[2].toInt() and 0xFF

        if (length + 3 != bytes.size) {
            throw IllegalArgumentException("Payload size from header != actual payload size")
        }

        val payload = ByteArray(length)
        System.arraycopy(bytes, 3, payload, 0, length)

        BluetoothProtocol.onPacket(this, BluetoothPacket(PacketType.from(bytes[1]), payload))
    }

    fun sendPacket(bytes: ByteArray) {
        Log.d("speakermodel", "sendPacket bytes = ${HexHelper.bytesToHex(bytes)}")
        val writeCharacteristic = writeCharacteristic
        val bleConnection = bleConnection

        if (writeCharacteristic == null) {
            Log.w("Speaker", "sendPacket when writeCharacteristic is null")
        } else {
            BleOperationManager.request(WriteCharacteristicOperation(bleConnection, writeCharacteristic, bytes))
        }
    }

    fun getOrCreateFeature(featureType: Feature.Type): Feature {
        var feature = features[featureType]

        if (feature == null) {
            feature = featureType.clazz.newInstance()!!
            features[featureType] = feature
        }

        return feature
    }

    fun getFeature(featureType: Feature.Type) = features[featureType]
}
