package me.andreww7985.connectplus.speaker

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import me.andreww7985.connectplus.Event
import me.andreww7985.connectplus.bluetooth.BleConnection
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.dfu.DfuModel
import me.andreww7985.connectplus.helpers.HexHelper.toHexString
import me.andreww7985.connectplus.manager.BleOperationManager
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.manager.bleoperations.WriteCharacteristicOperation
import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.protocol.AudioChannel
import me.andreww7985.connectplus.protocol.DataToken
import me.andreww7985.connectplus.protocol.Packet
import me.andreww7985.connectplus.protocol.PacketType
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class SpeakerModel(bluetoothDevice: BluetoothDevice, val scanRecord: String) : BaseModel {
    val mac = bluetoothDevice.address!!
    var index = 0
    lateinit var model: ProductModel
    lateinit var color: ProductColor
    var isDiscovered = false

    var audioChannel = AudioChannel.STEREO

    var readCharacteristic: BluetoothGattCharacteristic? = null
    var writeCharacteristic: BluetoothGattCharacteristic? = null
    var bleConnection: BleConnection = BleConnection(bluetoothDevice, this)

    val features = ConcurrentHashMap<Feature.Type, Feature>()
    val featuresUpdatedEvent = Event()

    val dfuModel = DfuModel(this)

    val connectedEvent = Event()

    init {
        bleConnection.connect()
    }

    fun discovered() {
        Timber.d("$mac discovered")
        isDiscovered = true

        sendPacket(Packet(PacketType.REQ_FEEDBACK_SOUNDS))
        sendPacket(Packet(PacketType.REQ_FIRMWARE_VERSION))
        sendPacket(Packet(PacketType.REQ_SPEAKERPHONE_MODE))
        sendPacket(Packet(PacketType.REQ_BASS_LEVEL))

        connectedEvent.fire()
    }

    fun featuresChanged() {
        Timber.d("$mac featuresChanged")
        featuresUpdatedEvent.fire()
    }

    fun onPacket(bytes: ByteArray) {
        if (bytes[0] != 0xAA.toByte()) {
            throw IllegalArgumentException("Packet header != 0xAA")
        }

        val length = bytes[2].toInt() and 0xFF

        if (length + 3 != bytes.size) {
            throw IllegalArgumentException("Payload size from header != actual payload size")
        }

        val payload = ByteArray(length)
        System.arraycopy(bytes, 3, payload, 0, length)

        BluetoothProtocol.onPacket(this, Packet(PacketType.from(bytes[1]), payload))
    }

    fun updateAudioChannel(audioChannel: AudioChannel) {
        if (this.audioChannel != audioChannel) {
            sendPacket(Packet(PacketType.SET_SPEAKER_INFO, byteArrayOf(index.toByte(), DataToken.TOKEN_AUDIO_CHANNEL.id.toByte(), audioChannel.value.toByte())))
            this.audioChannel = audioChannel
        }

        if (isDiscovered) {
            SpeakerManager.linkUpdated()
        }
    }

    fun updateAudioFeedback(audioFeedback: Boolean) {
        sendPacket(Packet(PacketType.SET_FEEDBACK_SOUNDS, byteArrayOf(if (audioFeedback) 1 else 0)))
    }

    fun updateSpeakerphoneMode(speakerphoneMode: Boolean) {
        sendPacket(Packet(PacketType.SET_SPEAKERPHONE_MODE, byteArrayOf(if (speakerphoneMode) 1 else 0)))
    }

    fun updateBassLevel(level: Int) {
        sendPacket(Packet(PacketType.SET_BASS_LEVEL, byteArrayOf(level.toByte())), replacePrevious = true)
    }

    fun playSound() {
        sendPacket(Packet(PacketType.PLAY_SOUND))
    }

    fun sendPacket(packet: Packet) {
        val writeCharacteristic = writeCharacteristic
        val bleConnection = bleConnection

        if (writeCharacteristic == null) {
            Timber.w("sendPacket when writeCharacteristic is null")
        } else {
            val bytes = byteArrayOf(0xAA.toByte(), packet.type.id.toByte(), packet.payload.size.toByte(), *packet.payload)
            Timber.d("$mac sendPacket ${packet.type.name} ${packet.payload.toHexString()}")
            BleOperationManager.request(WriteCharacteristicOperation(bleConnection, writeCharacteristic, bytes))
        }
    }

    fun getOrCreateFeature(featureType: Feature.Type) =
            features[featureType] ?: run {
                val feature = featureType.clazz.newInstance()
                features[featureType] = feature
                feature
            }

    inline fun <reified T> getFeature(): T = features.values.first { feature -> feature is T } as T
}
