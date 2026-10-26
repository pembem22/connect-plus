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
import me.andreww7985.connectplus.protocol.Protocol4
import me.andreww7985.connectplus.speaker.hardware.HwPlatform
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

class SpeakerModel(bluetoothDevice: BluetoothDevice, val scanRecord: String) : BaseModel {
    val mac = bluetoothDevice.address!!
    var index = 0
    lateinit var hardware: SpeakerHardware
    var isDiscovered = false

    var audioChannel = AudioChannel.STEREO

    var readCharacteristic: BluetoothGattCharacteristic? = null
    var writeCharacteristic: BluetoothGattCharacteristic? = null
    var bleConnection: BleConnection = BleConnection(bluetoothDevice, this)

    val features = ConcurrentHashMap<Feature.Type, Feature>()
    val featuresUpdatedEvent = Event()

    val dfuModel = DfuModel(this)

    var isPlaying = false

    init {
        bleConnection.connect()
    }

    fun discovered() {
        Timber.d("$mac discovered")
        isDiscovered = true

        if (hardware.platform != HwPlatform.PROTOCOL4) {
            sendPacket(Packet(PacketType.REQ_FEEDBACK_SOUNDS))
            sendPacket(Packet(PacketType.REQ_FIRMWARE_VERSION))
            sendPacket(Packet(PacketType.REQ_SPEAKERPHONE_MODE))
            sendPacket(Packet(PacketType.REQ_BASS_LEVEL))
        }

        SpeakerManager.speakerConnected(this)
    }

    fun featuresChanged() {
        Timber.d("$mac featuresChanged")
        featuresUpdatedEvent.fire()
    }

    fun onPacket(bytes: ByteArray) {
        if (hardware.platform == HwPlatform.PROTOCOL4) return onProtocol4Packet(bytes)
        if (bytes[0] != 0xAA.toByte()) return

        val length = bytes[2].toInt() and 0xFF

        if (length + 3 != bytes.size) return


        val payload = ByteArray(length)
        System.arraycopy(bytes, 3, payload, 0, length)

        BluetoothProtocol.onPacket(this, Packet(PacketType.from(bytes[1]), payload))
    }

    private fun onProtocol4Packet(bytes: ByteArray) {
        Timber.d("$mac onProtocol4Packet ${bytes.toHexString()}")

        // During a flash the speaker drives us with OTA notifications and an OTA-info reply
        Protocol4.parseOtaNotification(bytes)?.let { dfuModel.onProtocol4OtaNotification(it); return }

        val features = Protocol4.parseFeatures(bytes) ?: return

        if (features.containsKey(Protocol4.FEATURE_OTA_MAX_SIZE)) {
            dfuModel.onProtocol4OtaInfo()
            return
        }

        features[Protocol4.FEATURE_FIRMWARE_VERSION]?.takeIf { it.size >= 2 }?.let { v ->
            updateFeature(Feature.FirmwareVersion(v[0].toInt() and 0xFF, v[1].toInt() and 0xFF, v.getOrNull(2)?.let { it.toInt() and 0xFF }))
        }

        val name = features[Protocol4.FEATURE_DEVICE_NAME]?.let { String(it) }
        val battery = features[Protocol4.FEATURE_BATTERY_STATUS]?.firstOrNull()?.let { it.toInt() and 0xFF }
        if (name != null && battery != null) {
            // Same encoding as the older speakers: >= 128 means charging
            updateFeature(Feature.BatteryName(battery % 128, battery >= 128, name))
        }

        featuresChanged()
        if (!isDiscovered) discovered()
    }

    fun sendProtocol4(bytes: ByteArray) {
        val writeCharacteristic = writeCharacteristic ?: return Timber.w("sendProtocol4 when writeCharacteristic is null")
        Timber.d("$mac sendProtocol4 ${bytes.toHexString()}")
        BleOperationManager.request(WriteCharacteristicOperation(bleConnection, writeCharacteristic, bytes))
    }

    fun requestProtocol4Info() =
        sendProtocol4(Protocol4.getDeviceInfo(Protocol4.FEATURE_DEVICE_NAME, Protocol4.FEATURE_BATTERY_STATUS, Protocol4.FEATURE_FIRMWARE_VERSION))

    fun updateAudioChannel(audioChannel: AudioChannel) {
        if (this.audioChannel == audioChannel) {
            return
        }

        sendPacket(Packet(PacketType.SET_SPEAKER_INFO, byteArrayOf(index.toByte(), DataToken.TOKEN_AUDIO_CHANNEL.id.toByte(), audioChannel.value.toByte())))
        this.audioChannel = audioChannel

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

    fun requestAnalyticsData() {
        sendPacket(Packet(PacketType.REQ_ANALYTICS_DATA))
    }

    fun setName(name: String) {
        val bytes = name.toByteArray()
        sendPacket(Packet(PacketType.SET_SPEAKER_INFO, byteArrayOf(
                index.toByte(),
                DataToken.TOKEN_NAME.id.toByte(),
                bytes.size.toByte(),
                *bytes)))
    }

    fun sendPacket(packet: Packet, replacePrevious: Boolean = false) {
        val writeCharacteristic = writeCharacteristic
        val bleConnection = bleConnection

        if (writeCharacteristic == null) {
            Timber.w("sendPacket when writeCharacteristic is null")
        } else {
            val bytes = byteArrayOf(0xAA.toByte(), packet.type.id.toByte(), packet.payload.size.toByte(), *packet.payload)
            Timber.d("$mac sendPacket ${packet.type.name} ${packet.payload.toHexString()}")
            BleOperationManager.request(WriteCharacteristicOperation(bleConnection, writeCharacteristic, bytes), replacePrevious)
        }
    }

    inline fun <reified T : Feature> updateFeature(updated: T) {
        val type = Feature.Type.fromClass(T::class.java)
        features[type] = updated
    }

    inline fun <reified T : Feature> getFeatureOrNull(): T? =
        features.values.firstOrNull { feature -> feature is T } as T?

    inline fun <reified T : Feature> getFeatureOrThrow(): T =
        getFeatureOrNull<T>()
                    ?: throw IllegalArgumentException("Feature ${T::class.java.simpleName} does not exist")
}

