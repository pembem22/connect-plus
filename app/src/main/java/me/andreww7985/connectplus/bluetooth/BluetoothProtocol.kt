package me.andreww7985.connectplus.bluetooth

import android.bluetooth.le.ScanResult
import android.util.Log
import android.util.SparseArray
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.speaker.Feature
import me.andreww7985.connectplus.speaker.ProductColor
import me.andreww7985.connectplus.speaker.ProductModel
import me.andreww7985.connectplus.speaker.SpeakerModel

val Int.byte get() = this.toByte()

object BluetoothProtocol {
    private const val TAG = "BluetoothProtocol"

    fun connect(scanResult: ScanResult) {
        val scanRecord = scanResult.scanRecord

        if (scanRecord == null) {
            Log.w(TAG, "connect scanRecord = null")
            return
        }

        val scanRecordBytes = scanRecord.bytes
        //Log.d(TAG, "connect scanRecordBytes = ${HexHelper.bytesToHex(scanRecordBytes)}")

        val manufacturerData = SparseArray<ByteArray>()
        var currentPos = 0
        while (currentPos < scanRecordBytes.size) {
            val currentPos2 = currentPos + 1
            val length = scanRecordBytes[currentPos].toInt() and 255
            if (length == 0) {
                break
            }
            val dataLength = length - 1
            currentPos = currentPos2 + 1

            when (scanRecordBytes[currentPos2]) {
                0xFF.byte -> manufacturerData.put((scanRecordBytes[currentPos + 1].toInt() and 255 shl 8) + (scanRecordBytes[currentPos].toInt() and 255),
                        scanRecordBytes.copyOfRange(currentPos + 2, currentPos + 2 + dataLength))
            }

            currentPos += dataLength
        }

        val speakerData = manufacturerData[87]

        if (speakerData == null) {
            Log.w(TAG, "connect speakerData = null")
            return
        }

        if (SpeakerManager.speakers.containsKey(scanResult.device.address)) return

        Log.d(TAG, "connect parsed = ${HexHelper.bytesToHex(speakerData)}")
        val speakerModel = ProductModel.from(HexHelper.bytesToHex(byteArrayOf(speakerData[1], speakerData[0])))
        val speakerColor = ProductColor.from(speakerModel, speakerData[2].toInt() and 0xFF)
        //val speakerRole = speakerData[3]

        val speaker = SpeakerModel(speakerModel, speakerColor, scanResult.device, HexHelper.bytesToHex(speakerData))

        SpeakerManager.speakerFound(speaker)
    }

    fun requestSpeakerInfo(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_SPEAKER_INFO, byteArrayOf(0)))
    }

    fun requestSpeakerFirmware(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_FIRMWARE_VERSION))
    }

    fun setActiveChannel(speaker: SpeakerModel, channel: ActiveChannel) {
        setSpeakerData(speaker, DataToken.TOKEN_ACTIVE_CHANNEL, byteArrayOf(channel.value))
    }

    fun requestAudioFeedback(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_FEEDBACK_SOUNDS))
    }

    fun setSpeakerphoneMode(speaker: SpeakerModel, speakerphoneMode: Boolean) {
        sendPacket(makePacket(PacketType.SET_SPEAKERPHONE_MODE, byteArrayOf(if (speakerphoneMode) 1 else 0)))
    }

    fun requestSpeakerphoneMode(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_SPEAKERPHONE_MODE))
    }

    fun setAudioFeedback(speaker: SpeakerModel, audioFeedback: Boolean) {
        sendPacket(makePacket(PacketType.SET_FEEDBACK_SOUNDS, byteArrayOf(if (audioFeedback) 1 else 0)))
    }

    fun renameSpeaker(speaker: SpeakerModel, name: String) {
        val bytes = name.toByteArray()
        setSpeakerData(speaker, DataToken.TOKEN_NAME, byteArrayOf(bytes.size.byte, *bytes))
    }

    fun playSound(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.PLAY_SOUND))
    }

    fun setSpeakerData(speaker: SpeakerModel, token: DataToken, bytes: ByteArray) {
        sendPacket(makePacket(PacketType.SET_SPEAKER_INFO, byteArrayOf(speaker.index.byte, token.id.toByte(), *bytes)))
    }

    fun sendPacket(bytes: ByteArray) {
        Log.d(TAG, "sendPacket ${HexHelper.bytesToHex(bytes)}")
        SpeakerManager.mainSpeaker!!.sendPacket(bytes)
    }

    fun makePacket(packetType: PacketType, bytes: ByteArray = byteArrayOf()): ByteArray {
        Log.d(TAG, "makePacket $packetType ${HexHelper.bytesToHex(bytes)}")

        if (bytes.size > 255) {
            throw IllegalArgumentException("too long p")
        }

        return byteArrayOf(0xAA.byte, packetType.id.byte, bytes.size.byte, *bytes)
    }

    fun packetToBytes(bluetoothPacket: BluetoothPacket) = makePacket(bluetoothPacket.type, bluetoothPacket.payload)

    fun onPacket(speaker: SpeakerModel, packet: BluetoothPacket) {
        Log.d(TAG, "onPacket ${packet.type} ${HexHelper.bytesToHex(packet.payload)}")

        val payload = packet.payload

        when (packet.type) {
            PacketType.ACK -> {
                Log.d(TAG, "Ack!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!}")
            }
            PacketType.RES_SPEAKER_INFO -> {
                speaker.index = payload[0].toInt() and 0xFF

                var name: String? = null
                var batteryCharging: Boolean? = null
                var batteryLevel: Int? = null

                var pointer = 1
                while (pointer < payload.size) {
                    when (DataToken.from(payload[pointer].toInt() and 0xFF)) {
                        DataToken.TOKEN_MODEL -> {
                            pointer += 3
                        }
                        DataToken.TOKEN_COLOR -> {
                            pointer += 2
                        }
                        DataToken.TOKEN_BATTERY_STATUS -> {
                            val batteryStatus = payload[pointer + 1].toInt() and 0xFF
                            pointer += 2

                            batteryCharging = batteryStatus > 100
                            batteryLevel = batteryStatus % 128
                        }
                        DataToken.TOKEN_LINKED_DEVICE_COUNT -> {
                            val likedDeviceCount = payload[pointer + 1].toInt() and 0xFF
                            pointer += 2
                        }
                        DataToken.TOKEN_ACTIVE_CHANNEL -> {
                            val activeChannel = ActiveChannel.from(payload[pointer + 1])!!
                            // speaker.activeChannel = activeChannel
                            pointer += 2

                        }
                        DataToken.TOKEN_AUDIO_SOURCE -> {
                            pointer += 2
                        }
                        DataToken.TOKEN_MAC -> {
                            pointer += 8
                        }
                        DataToken.TOKEN_NAME -> {
                            val nameBytesLength = payload[pointer + 1].toInt() and 0xFF
                            val nameBytes = ByteArray(nameBytesLength)
                            for (i in 0 until nameBytesLength) nameBytes[i] = payload[pointer + 2 + i]
                            pointer += 2 + nameBytesLength

                            name = String(nameBytes)

                        }
                        else -> {
                            Log.d(TAG, "onPacket RES_SPEAKER_INFO unknown token")
                            pointer++
                        }
                    }
                }

                if (name != null && batteryLevel != null && batteryCharging != null) {
                    val feature = speaker.getOrCreateFeature(Feature.Type.BATTERY_NAME) as Feature.BatteryName

                    feature.batteryCharging = batteryCharging
                    feature.batteryLevel = batteryLevel
                    feature.deviceName = name
                    speaker.featuresChanged()
                }

                requestSpeakerFirmware(speaker)
            }
            PacketType.RES_FIRMWARE_VERSION -> {
                val feature = speaker.getOrCreateFeature(Feature.Type.FIRMWARE_VERSION) as Feature.FirmwareVersion

                feature.major = payload[0].toInt() and 0xFF
                feature.minor = payload[1].toInt() and 0xFF
                feature.build = if (payload.size >= 3) payload[2].toInt() and 0xFF else -1
                speaker.featuresChanged()

                requestAudioFeedback(speaker)
            }
            PacketType.RES_FEEDBACK_SOUNDS -> {
                val feature = speaker.getOrCreateFeature(Feature.Type.FEEDBACK_SOUNDS) as Feature.FeedbackSounds

                feature.enabled = payload[0] == 1.byte
                speaker.featuresChanged()

                requestSpeakerphoneMode(speaker)
            }
            PacketType.RES_SPEAKERPHONE_MODE -> {
                val feature = speaker.getOrCreateFeature(Feature.Type.SPEAKERPHONE_MODE) as Feature.SpeakerphoneMode

                feature.enabled = payload[0] == 1.byte
                speaker.featuresChanged()
            }
            PacketType.RES_DFU_STATUS_CHANGE -> {
                /* val speaker = SpeakerManager.mainSpeaker!!
                val dfu = speaker.dfu
                val status = if (payload.size == 1) payload[0].toInt() else payload[1].toInt()

                when (status) {
                    0 -> {
                        App.logFirebaseEvent("dfu_error")
                        speaker.dfuState = DfuState.FLASHING_ERROR
                    }
                    1, 2 -> {
                        Log.d(TAG, "onPacket RES_DFU_STATUS_CHANGE sending ${dfu.currentChunk} chunk")

                        if (status == 1) {
                            speaker.dfuState = DfuState.FLASHING
                        }

                        sendPacket(makePacket(PacketType.SET_DFU_DATA, dfu.getNextChunk()))
                        FragmentController.update()
                    }
                    3 -> {
                        App.logFirebaseEvent("dfu_done")
                        speaker.dfuState = DfuState.FLASHING_DONE
                    }
                }*/
            }
        }
    }
}