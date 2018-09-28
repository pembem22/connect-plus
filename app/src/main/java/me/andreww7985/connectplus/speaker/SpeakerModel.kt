package me.andreww7985.connectplus.speaker

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.feature.Feature
import me.andreww7985.connectplus.feature.model.BaseFeatureModel
import me.andreww7985.connectplus.hardware.HardwareColor
import me.andreww7985.connectplus.hardware.HardwareModel
import me.andreww7985.connectplus.mvp.BaseModel

class SpeakerModel(val model: HardwareModel, val color: HardwareColor, val bluetoothDevice: BluetoothDevice, val scanRecord: String): BaseModel {
    val mac = bluetoothDevice.address.toUpperCase().replace(":", "")
    var index = 0
    val featureModules = HashMap<Feature, BaseFeatureModel>()

    lateinit var readCharacteristic: BluetoothGattCharacteristic
    lateinit var writeCharacteristic: BluetoothGattCharacteristic
    lateinit var bluetoothGatt: BluetoothGatt

    var onFeatureAddedCallback: ((Feature, BaseFeatureModel) -> Unit)? = null

    fun setOnFeatureAddedListener(listener: ((Feature, BaseFeatureModel) -> Unit)) {
        onFeatureAddedCallback = listener
    }

    fun onPacket(packet: ByteArray) {
        // TODO: Change to reactive
        BluetoothProtocol.onPacket(packet)
    }

    fun sendPacket(bytes: ByteArray) {
        writeCharacteristic.value = bytes
        bluetoothGatt.writeCharacteristic(writeCharacteristic)
    }

    fun getFeature(feature: Feature): BaseFeatureModel {
        var featureModel = featureModules[feature]

        if (featureModel == null) {
            featureModel = Feature.Factory.makeFeatureModel(feature, this)
            featureModules[feature] = featureModel

            // TODO: Change to reactive
            onFeatureAddedCallback?.invoke(feature, featureModel)
        }

        return featureModel
    }
}
